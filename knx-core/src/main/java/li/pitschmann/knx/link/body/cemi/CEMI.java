/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2019 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.knx.link.body.cemi;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.AbstractMultiRawData;
import li.pitschmann.knx.link.body.address.AddressType;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.link.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Common External Message Interface
 * <p>
 * The cEMI message format is a generic structure for medium independent KNX messages, which can be added with
 * information like a timestamp or other.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Message Code                  | Additional Info Length        |
 * | (1 octet = 08h)               | (1 octet)                     |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | Additional Information                                        |
 * | (optional, variable length)                                   |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | Service Information                                           |
 * | (variable length)                                             |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class CEMI extends AbstractMultiRawData {
    private final MessageCode messageCode;
    private final AdditionalInfo additionalInfo;
    private final ControlByte1 controlByte1;
    private final ControlByte2 controlByte2;
    private final IndividualAddress sourceAddress;
    private final KnxAddress destinationAddress;
    private final int npduLength;
    private final TPCI tpci;
    private final int tpciPacketNumber;
    private final APCI apci;
    private final byte[] apciData;

    private CEMI(final byte[] cemiRawData) {
        super(cemiRawData);

        this.messageCode = MessageCode.valueOf(Bytes.toUnsignedInt(cemiRawData[0]));

        // ------------------------------------------
        // Additional Information
        // 1 byte for additional info length
        // N byte (dynamic)
        // Total: min. 1 byte
        // ------------------------------------------
        final var addInfoIndex = 1;
        if (Bytes.toUnsignedInt(cemiRawData[addInfoIndex + 0]) > 0) {
            throw new UnsupportedOperationException("Additional Info Length is not supported yet! CEMI Raw: " + this.getRawDataAsHexString());
        }
        this.additionalInfo = AdditionalInfo.empty();

        // ------------------------------------------
        // Control Bytes
        // 1 byte for 1st control
        // 1 byte for 2nd control
        // Total: 2 bytes
        // ------------------------------------------
        final var controlIndex = addInfoIndex + this.additionalInfo.getTotalLength();
        this.controlByte1 = ControlByte1.valueOf(cemiRawData[controlIndex + 0]);
        this.controlByte2 = ControlByte2.valueOf(cemiRawData[controlIndex + 1]);

        // ------------------------------------------
        // Addresses
        // 2 bytes for source address
        // 2 bytes for destination address
        // Total: 4 bytes
        // ------------------------------------------
        final var addrIndex = controlIndex + 2;
        this.sourceAddress = IndividualAddress.of(new byte[]{cemiRawData[addrIndex + 0], cemiRawData[addrIndex + 1]});
        // destination address is either individual or group (depends on address type on control 2 bit)
        final var destinationAddressBytes = new byte[]{cemiRawData[addrIndex + 2], cemiRawData[addrIndex + 3]};
        if (this.controlByte2.getAddressType() == AddressType.INDIVIDUAL) {
            this.destinationAddress = IndividualAddress.of(destinationAddressBytes);
        } else {
            this.destinationAddress = GroupAddress.of(destinationAddressBytes);
        }

        // ------------------------------------------
        // NPDU (Network Layer Protocol Data Unit)
        // * TPCI Transport Layer Protocol Control Information
        // * APCI Application Layer Protocol Control Information
        // 1 byte for NPDU length
        // 2 bytes for TCPI/APCI data
        // N bytes for data (optional, depends on APCI and NDPU length)
        // Total: min. 3 bytes
        // ------------------------------------------
        final var npduIndex = addrIndex + 4;
        // data bytes without TCPI/APCI bits
        this.npduLength = Bytes.toUnsignedInt(cemiRawData[npduIndex]);

        // 00.. .... UDT unnumbered package
        // 01.. .... NDT numbered package
        // 10.. .... UCD unnumbered control data
        // 11.. .... NCD numbered control data
        this.tpci = TPCI.valueOf(cemiRawData[npduIndex + 1] & 0xC0);
        // ..xx xx.. package number
        this.tpciPacketNumber = (cemiRawData[npduIndex + 1] & 0x3C) >>> 2;
        // .... ..xx xx.. .... APCI code
        // .... ..00 0000 0000 group value request
        // .... ..00 01nn nnnn group value response
        // .... ..00 10nn nnnn group value write (not requested)
        // .... ..00 1100 0000 individual address write
        // .... ..01 0000 0000 individual address read
        // .... ..01 0100 0000 individual address response
        this.apci = APCI.valueOf(Bytes.toUnsignedInt((byte) (cemiRawData[npduIndex + 1] & 0x03), (byte) (cemiRawData[npduIndex + 2] & 0xC0)));

        // APCI data
        if (this.apci == APCI.GROUP_VALUE_READ || this.apci == APCI.INDIVIDUAL_ADDRESS_READ) {
            // no data when APCI code is a read command
            this.apciData = new byte[0];
        } else if (this.npduLength == 1) {
            // in case data is up to 6 bits, then it is a part of APCI
            this.apciData = new byte[]{(byte) (cemiRawData[npduIndex + 2] & 0x3F)};
        } else {
            // in case data is more than 6 bits then bytes are appended
            final var npduIndexEnd = npduIndex + 3 + this.npduLength - 1;
            if (npduIndexEnd > cemiRawData.length) {
                // should never happen NPDU length is correct!
                throw new KnxNumberOutOfRangeException("cemiRawData/npdu", npduIndex + 3, cemiRawData.length, npduIndexEnd);
            }
            this.apciData = Arrays.copyOfRange(cemiRawData, npduIndex + 3, npduIndexEnd);
        }
    }

    /**
     * Builds a new {@link CEMI} instance
     *
     * @param bytes complete byte array for {@link CEMI}
     * @return immutable {@link CEMI}
     */
    public static CEMI valueOf(final byte[] bytes) {
        return new CEMI(bytes);
    }

    /**
     * Creates a new {@link CEMI} instance with default settings taken from
     * {@link #useDefault(KnxAddress, APCI, byte[])} with pre-defined setting:
     *
     * <ul>
     * <li>APCI = {@link APCI#GROUP_VALUE_WRITE}</li>
     * <li>APCI data = N/A (empty)</li>
     * </ul>
     *
     * @param destinationAddress
     * @return immutable {@link CEMI} with default settings and {@link KnxAddress} for read request
     */
    public static CEMI useDefaultForGroupValueRead(final KnxAddress destinationAddress) {
        // no validation required here

        // create new instance
        return useDefault(destinationAddress, APCI.GROUP_VALUE_READ, (byte[]) null);
    }

    /**
     * Creates a new {@link CEMI} instance with default settings taken from
     * {@link #useDefault(KnxAddress, APCI, byte[])} with pre-defined setting:
     *
     * <ul>
     * <li>APCI = {@link APCI#GROUP_VALUE_WRITE}</li>
     * </ul>
     *
     * @param destinationAddress
     * @param dataPointValue
     * @return immutable {@link CEMI} with default settings and {@link KnxAddress} and byte array for write request
     */
    public static CEMI useDefaultForGroupValueWrite(final KnxAddress destinationAddress, final DataPointValue<?> dataPointValue) {
        return useDefaultForGroupValueWrite(destinationAddress, dataPointValue.toByteArray());
    }

    /**
     * Creates a new {@link CEMI} instance with default settings taken from
     * {@link #useDefault(KnxAddress, APCI, byte[])} with pre-defined setting:
     *
     * <ul>
     * <li>APCI = {@link APCI#GROUP_VALUE_WRITE}</li>
     * </ul>
     *
     * @param destinationAddress
     * @param apciData
     * @return immutable {@link CEMI} with default settings and {@link KnxAddress} and byte array for write request
     */
    public static CEMI useDefaultForGroupValueWrite(final KnxAddress destinationAddress, final byte[] apciData) {
        // no validation required here

        // create new instance
        return useDefault(destinationAddress, APCI.GROUP_VALUE_WRITE, apciData);
    }

    /**
     * Creates a new {@link CEMI} instance with default settings. It is a wrapper of
     * {@link #useDefault(KnxAddress, APCI, byte[])} while the byte array is taken
     * from {@link DataPointValue} parameter.
     *
     * @param destinationAddress
     * @param apci
     * @param dataPointValue
     * @return immutable {@link CEMI} with default settings and {@link KnxAddress}, {@link APCI} and byte array with
     * data for {@link APCI}.
     */
    public static CEMI useDefault(final KnxAddress destinationAddress, final APCI apci, final DataPointValue<?> dataPointValue) {
        return useDefault(destinationAddress, apci, dataPointValue.toByteArray());
    }

    /**
     * Creates a new {@link CEMI} instance with default settings:
     *
     * <ul>
     * <li>Message Code = {@link MessageCode#L_DATA_REQ}</li>
     * <li>Additional Info = N/A (empty)</li>
     * <li>ControlByte 1 = {@link ControlByte1#useDefault()}</li>
     * <li>ControlByte 2 = {@link ControlByte2#useDefault(KnxAddress)} (KnxAddress taken from parameter
     * {@code destinationAddress})</li>
     * <li>TPCI = {@link TPCI#UNNUMBERED_PACKAGE}</li>
     * <li>TPCI Packet Number = 0 (zero)</li>
     * </ul>
     *
     * @param destinationAddress
     * @param apci
     * @param apciData
     * @return immutable {@link CEMI} with default settings and {@link KnxAddress}, {@link APCI} and byte array with
     * data for {@link APCI}.
     */
    public static CEMI useDefault(final KnxAddress destinationAddress, final APCI apci, final byte[] apciData) {
        // no validation required here

        // default settings
        final var messageCode = MessageCode.L_DATA_REQ;
        final var additionalInfo = AdditionalInfo.empty();
        final var controlByte1 = ControlByte1.useDefault();
        final var controlByte2 = ControlByte2.useDefault(destinationAddress);
        final var sourceAddress = IndividualAddress.useDefault();
        final var tpci = TPCI.UNNUMBERED_PACKAGE;
        final var tpciPacketNumber = 0;

        // create new instance
        return create(messageCode, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, tpci, tpciPacketNumber, apci,
                apciData);
    }

    /**
     * Creates a new {@link CEMI} instance
     *
     * @param messageCode
     * @param additionalInfo
     * @param controlByte1
     * @param controlByte2
     * @param sourceAddress
     * @param destinationAddress
     * @param tpci
     * @param tpciPacketNumber
     * @param apci
     * @param dataPointValue
     * @return immutable {@link CEMI}
     */
    public static CEMI create(final MessageCode messageCode, final AdditionalInfo additionalInfo, final ControlByte1 controlByte1,
                              final ControlByte2 controlByte2, final IndividualAddress sourceAddress, final KnxAddress destinationAddress, final TPCI tpci,
                              final int tpciPacketNumber, final APCI apci, final DataPointValue<?> dataPointValue) {
        return create(messageCode, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, tpci, tpciPacketNumber, apci,
                dataPointValue.toByteArray());
    }

    /**
     * Creates a new {@link CEMI} instance
     *
     * @param messageCode
     * @param additionalInfo
     * @param controlByte1
     * @param controlByte2
     * @param sourceAddress
     * @param destinationAddress
     * @param tpci
     * @param tpciPacketNumber
     * @param apci
     * @param apciData
     * @return immutable {@link CEMI}
     */
    public static CEMI create(final MessageCode messageCode, final AdditionalInfo additionalInfo, final ControlByte1 controlByte1,
                              final ControlByte2 controlByte2, final IndividualAddress sourceAddress, final KnxAddress destinationAddress, final TPCI tpci,
                              final int tpciPacketNumber, final APCI apci, final byte[] apciData) {
        // validate
        if (messageCode == null) {
            throw new KnxNullPointerException("messageCode");
        } else if (additionalInfo == null) {
            throw new KnxNullPointerException("additionalInfo");
        } else if (controlByte1 == null) {
            throw new KnxNullPointerException("controlByte1");
        } else if (controlByte2 == null) {
            throw new KnxNullPointerException("controlByte2");
        } else if (sourceAddress == null) {
            throw new KnxNullPointerException("sourceAddress");
        } else if (destinationAddress == null) {
            throw new KnxNullPointerException("destinationAddress");
        } else if (tpci == null) {
            throw new KnxNullPointerException("tpci");
        } else if (apci == null) {
            throw new KnxNullPointerException("apci");
        } else if (apciData != null && apciData.length > 14) {
            throw new KnxNumberOutOfRangeException("apciData", 0, 14, apciData.length);
        } else if (tpciPacketNumber < 0 || tpciPacketNumber > 0xFF) {
            throw new KnxNumberOutOfRangeException("tpciPacketNumber", 0, 0xFF, tpciPacketNumber);
        } else if ((tpci == TPCI.UNNUMBERED_PACKAGE || tpci == TPCI.UNNUMBERED_CONTROL_DATA) && tpciPacketNumber != 0) {
            throw new KnxIllegalArgumentException("TPCI packet number should not be set when TCPI is unnumbered", tpci, tpciPacketNumber);
        } else if (apci == APCI.GROUP_VALUE_READ && apciData != null && apciData.length > 0) {
            throw new KnxIllegalArgumentException("APCI data should not be set when APCI#READ is used.", apci,
                    ByteFormatter.formatHexAsString(apciData));
        } else if ((apci == APCI.GROUP_VALUE_WRITE || apci == APCI.GROUP_VALUE_RESPONSE) && (apciData == null || apciData.length == 0)) {
            throw new KnxIllegalArgumentException("APCI data should be set when APCI write or response is used.", apci,
                    ByteFormatter.formatHexAsString(apciData));
        } else if ((destinationAddress instanceof GroupAddress && controlByte2.getAddressType() != AddressType.GROUP)
                || (destinationAddress instanceof IndividualAddress && controlByte2.getAddressType() != AddressType.INDIVIDUAL)) {
            throw new KnxIllegalArgumentException("Missconfiguration between address type in ControlByte2 and destination address type.",
                    controlByte2.getAddressType(), destinationAddress);
        }

        final var sourceAddressAsBytes = sourceAddress.getRawData();
        final var destinationAddressAsBytes = destinationAddress.getRawData();

        // xx.. .... TCPI code
        // 00.. .... UDT unnumbered package
        // 01.. .... NDT numbered package
        // 10.. .... UCD unnumbered control data
        // 11.. .... NCD numbered control data
        final var tpciPaketTypeAsByte = tpci.getCodeAsByte();
        // ..xx xx.. package number
        final var tpciPacketNumberAsByte = (byte) ((tpciPacketNumber & 0x0F) << 2);

        // .... ..xx xx.. .... APCI code
        // .... ..00 0000 0000 group value request
        // .... ..00 01nn nnnn group value response
        // .... ..00 10nn nnnn group value write (not requested)
        // .... ..00 1100 0000 individual address write
        // .... ..01 0000 0000 individual address read
        // .... ..01 0100 0000 individual address response
        final var acpiCodeAsByte = apci.getCodeAsBytes();
        final byte apciDataAsByte;
        final boolean apciDataSeparate;
        final int npduLengthNew;
        if (apci == APCI.GROUP_VALUE_READ) {
            // request only
            apciDataAsByte = 0x00;
            apciDataSeparate = false;
            npduLengthNew = 1;
        } else if (apci == APCI.GROUP_VALUE_RESPONSE || apci == APCI.GROUP_VALUE_WRITE) {
            if (apciData.length == 1 && Byte.toUnsignedInt(apciData[0]) < 0x40) {
                // optimized version available only for group value write and response
                // data is within 6 bits
                apciDataAsByte = (byte) (apciData[0] & 0x3F);
                apciDataSeparate = false;
                npduLengthNew = 1;
            } else {
                // data are as separate bytes
                apciDataAsByte = 0x00;
                apciDataSeparate = true;
                npduLengthNew = 1 + apciData.length;
            }
        } else {
            throw new KnxException("Current APCI is not supported: " + apci);
        }

        // create bytes
        final var bytes = new byte[255];
        var byteCount = 0;
        // message code
        bytes[byteCount++] = messageCode.getCodeAsByte();
        // add info
        System.arraycopy(additionalInfo.getRawData(), 0, bytes, byteCount, additionalInfo.getTotalLength());
        byteCount += additionalInfo.getTotalLength();
        // control bytes
        bytes[byteCount++] = controlByte1.getRawData();
        bytes[byteCount++] = controlByte2.getRawData();
        // source address
        System.arraycopy(sourceAddressAsBytes, 0, bytes, byteCount, sourceAddressAsBytes.length);
        byteCount += sourceAddressAsBytes.length;
        // destination address
        System.arraycopy(destinationAddressAsBytes, 0, bytes, byteCount, destinationAddressAsBytes.length);
        byteCount += destinationAddressAsBytes.length;
        // npdu
        bytes[byteCount++] = (byte) npduLengthNew;
        bytes[byteCount++] = (byte) (tpciPaketTypeAsByte | tpciPacketNumberAsByte | acpiCodeAsByte[0]);
        bytes[byteCount++] = (byte) (acpiCodeAsByte[1] | apciDataAsByte);
        if (apciDataSeparate) {
            System.arraycopy(apciData, 0, bytes, byteCount, apciData.length);
            byteCount += apciData.length;
        }
        return valueOf(Arrays.copyOf(bytes, byteCount));
    }

    @Override
    protected void validate(final byte[] cemiRawData) {
        if (cemiRawData == null) {
            throw new KnxNullPointerException("cemiRawData");
        } else if (cemiRawData.length < 11) {
            // 1 byte message code
            // 1 byte additional info length (+ N bytes data)
            // 2 byte control bytes
            // 4 bytes for address
            // 3 bytes for NPDU
            throw new KnxNumberOutOfRangeException("cemiRawData", 11, 0xFF, cemiRawData.length, cemiRawData);
        }
    }

    public MessageCode getMessageCode() {
        return this.messageCode;
    }

    public AdditionalInfo getAdditionalInfo() {
        return this.additionalInfo;
    }

    public ControlByte1 getControlByte1() {
        return this.controlByte1;
    }

    public ControlByte2 getControlByte2() {
        return this.controlByte2;
    }

    public IndividualAddress getSourceAddress() {
        return this.sourceAddress;
    }

    public KnxAddress getDestinationAddress() {
        return this.destinationAddress;
    }

    public int getNpduLength() {
        return this.npduLength;
    }

    public TPCI getTpci() {
        return this.tpci;
    }

    public int getTpciPacketNumber() {
        return this.tpciPacketNumber;
    }

    public APCI getApci() {
        return this.apci;
    }

    public @Nonnull
    byte[] getApciData() {
        return this.apciData;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("messageCode", this.messageCode)
                .add("additionalInfo", this.additionalInfo)
                .add("controlByte1", this.controlByte1)
                .add("controlByte2", this.controlByte2)
                .add("sourceAddress", this.sourceAddress)
                .add("destinationAddress", this.destinationAddress)
                .add("npduLength", this.npduLength + " (" + ByteFormatter.formatHex(this.npduLength) + ")")
                .add("tpci", this.tpci)
                .add("tpciPacketNumber", this.tpciPacketNumber + " (" + ByteFormatter.formatHex(this.tpciPacketNumber) + ")")
                .add("apci", this.apci)
                .add("apciData", Arrays.toString(this.apciData) + " (" + ByteFormatter.formatHexAsString(this.apciData) + ")");
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
