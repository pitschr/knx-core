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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.AbstractMultiRawData;
import li.pitschmann.knx.core.address.AddressType;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.address.KnxAddress;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.datapoint.value.PayloadOptimizable;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Common External Message Interface
 * <p>
 * The cEMI message format is a generic structure for medium
 * independent KNX messages, which can be added with information
 * like a timestamp or other.
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
 * See: KNX Specification, EMI/IMI
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
    private final int packetNumber;
    private final APCI apci;
    private final byte[] data;

    private CEMI(final byte[] cemiRawData) {
        super(cemiRawData);

        messageCode = MessageCode.valueOf(Bytes.toUnsignedInt(cemiRawData[0]));

        // ------------------------------------------
        // Additional Information
        // 1 byte for additional info length
        // N byte (dynamic)
        // Total: min. 1 byte
        // ------------------------------------------
        final var addInfoIndex = 1;
        if (Bytes.toUnsignedInt(cemiRawData[addInfoIndex]) > 0) {
            throw new UnsupportedOperationException("Additional Info Length is not supported yet! CEMI Raw: " + getRawDataAsHexString());
        }
        additionalInfo = AdditionalInfo.empty();

        // ------------------------------------------
        // Control Bytes
        // 1 byte for 1st control
        // 1 byte for 2nd control
        // Total: 2 bytes
        // ------------------------------------------
        final var controlIndex = addInfoIndex + additionalInfo.getTotalLength();
        controlByte1 = ControlByte1.of(cemiRawData[controlIndex]);
        controlByte2 = ControlByte2.of(cemiRawData[controlIndex + 1]);

        // ------------------------------------------
        // Addresses
        // 2 bytes for source address
        // 2 bytes for destination address
        // Total: 4 bytes
        // ------------------------------------------
        final var addrIndex = controlIndex + 2;
        sourceAddress = IndividualAddress.of(new byte[]{cemiRawData[addrIndex], cemiRawData[addrIndex + 1]});
        // destination address is either individual or group (depends on address type on control 2 bit)
        final var destinationAddressBytes = new byte[]{cemiRawData[addrIndex + 2], cemiRawData[addrIndex + 3]};
        if (controlByte2.getAddressType() == AddressType.INDIVIDUAL) {
            destinationAddress = IndividualAddress.of(destinationAddressBytes);
        } else {
            destinationAddress = GroupAddress.of(destinationAddressBytes);
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
        npduLength = Bytes.toUnsignedInt(cemiRawData[npduIndex]);

        // 00.. .... UDT unnumbered package
        // 01.. .... NDT numbered package
        // 10.. .... UCD unnumbered control data
        // 11.. .... NCD numbered control data
        tpci = TPCI.valueOf(cemiRawData[npduIndex + 1] & 0xC0);
        // ..xx xx.. packet number
        packetNumber = (cemiRawData[npduIndex + 1] & 0x3C) >>> 2;
        // .... ..xx xx.. .... APCI code
        // .... ..00 0000 0000 group value request
        // .... ..00 01nn nnnn group value response
        // .... ..00 10nn nnnn group value write (not requested)
        // .... ..00 1100 0000 individual address write
        // .... ..01 0000 0000 individual address read
        // .... ..01 0100 0000 individual address response
        apci = APCI.valueOf(Bytes.toUnsignedInt((byte) (cemiRawData[npduIndex + 1] & 0x03), (byte) (cemiRawData[npduIndex + 2] & 0xC0)));

        // APCI data
        if (apci == APCI.GROUP_VALUE_READ) {
            // no data when APCI code is a read command
            data = new byte[0];
        } else if (npduLength == 1) {
            // in case data is up to 6 bits, then it is a part of APCI
            data = new byte[]{(byte) (cemiRawData[npduIndex + 2] & 0x3F)};
        } else {
            // in case data is more than 6 bits then bytes are appended
            final var npduIndexStart = npduIndex + 3;
            final var npduIndexEnd = npduIndexStart + npduLength - 1;
            if (npduIndexEnd != cemiRawData.length) {
                // should never happen, assuming NPDU length is correct!
                throw new KnxIllegalArgumentException("There seems be a conflict with NDPU length ({}), " +
                        "NPDU Start Index ({}), NPDU End Index ({}) and CEMI raw (length={}): {}",
                        npduLength,
                        npduIndexStart,
                        npduIndexEnd,
                        cemiRawData.length,
                        getRawDataAsHexString()
                );
            }
            data = Arrays.copyOfRange(cemiRawData, npduIndexStart, npduIndexEnd);
        }
    }

    /**
     * Builds a new {@link CEMI} instance
     *
     * @param bytes complete byte array for {@link CEMI}
     * @return a new immutable {@link CEMI}
     */
    public static CEMI of(final byte[] bytes) {
        return new CEMI(bytes);
    }

    /**
     * Creates a new {@link CEMI} instance with default settings:
     *
     * <ul>
     * <li>Additional Info = N/A (empty)</li>
     * <li>ControlByte 1 = {@link ControlByte1#useDefault()}</li>
     * <li>ControlByte 2 = {@link ControlByte2#of(KnxAddress)}
     * (KnxAddress taken from parameter {@code destinationAddress})</li>
     * <li>TPCI = {@link TPCI#UNNUMBERED_PACKAGE}</li>
     * <li>TPCI Packet Number = 0 (zero)</li>
     * </ul>
     *
     * @param messageCode        the message code
     * @param destinationAddress destination address
     * @param apci               APCI for application layer control information
     * @param dataPointValue     the data point type value that contains value to be sent to KNX Net/IP device,
     *                           may be {@code null} if request for read only.
     * @return a new immutable {@link CEMI} with default settings and {@link MessageCode,} {@link KnxAddress},
     * {@link APCI} and byte array with data for {@link APCI} taken from {@link DataPointValue}.
     */
    public static CEMI useDefault(final MessageCode messageCode,
                                  final KnxAddress destinationAddress,
                                  final APCI apci,
                                  final @Nullable DataPointValue dataPointValue) {
        // no validation required here

        // default settings
        final var additionalInfo = AdditionalInfo.empty();
        final var controlByte1 = ControlByte1.useDefault();
        final var controlByte2 = ControlByte2.of(destinationAddress);
        final var sourceAddress = IndividualAddress.useDefault();
        final var tpci = TPCI.UNNUMBERED_PACKAGE;
        final var packetNumber = 0;

        // create new instance
        return of(messageCode, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, tpci, packetNumber, apci,
                dataPointValue);
    }

    /**
     * Creates a new {@link CEMI} instance
     *
     * @param messageCode        message code
     * @param additionalInfo     additional information for CEMI
     * @param controlByte1       control byte #1
     * @param controlByte2       control byte #2
     * @param sourceAddress      source address (physical address of KNX device)
     * @param destinationAddress destination address
     * @param tpci               TCPI for transport information
     * @param packetNumber       packet number for TCPI (optional)
     * @param apci               APCI for application layer control information
     * @param dataPointValue     the data point type value that contains value to be sent to KNX Net/IP device,
     *                           may be {@code null} if request for read only.
     * @return a new immutable {@link CEMI}
     */
    public static CEMI of(final MessageCode messageCode,
                          final AdditionalInfo additionalInfo,
                          final ControlByte1 controlByte1,
                          final ControlByte2 controlByte2,
                          final IndividualAddress sourceAddress,
                          final KnxAddress destinationAddress,
                          final TPCI tpci,
                          final int packetNumber,
                          final APCI apci,
                          final @Nullable DataPointValue dataPointValue) {
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
        } else if (packetNumber < 0 || packetNumber > 0xFF) {
            throw new KnxNumberOutOfRangeException("packetNumber", 0, 0xFF, packetNumber);
        } else if ((tpci == TPCI.UNNUMBERED_PACKAGE || tpci == TPCI.UNNUMBERED_CONTROL_DATA) && packetNumber != 0) {
            throw new KnxIllegalArgumentException(
                    "TPCI packet number should not be set when TCPI is unnumbered: tpci={}, packetNumber={}",
                    tpci.name(), packetNumber
            );
        } else if ((destinationAddress instanceof GroupAddress && controlByte2.getAddressType() != AddressType.GROUP)
                || (destinationAddress instanceof IndividualAddress && controlByte2.getAddressType() != AddressType.INDIVIDUAL)) {
            throw new KnxIllegalArgumentException(
                    "Address type in ControlByte2 ({}) is not compatible with destination address type: {}",
                    controlByte2.getAddressType().name(), destinationAddress.getAddressType().name()
            );
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
        final var tpciPacketNumberAsByte = (byte) ((packetNumber & 0x0F) << 2);

        // .... ..xx xx.. .... APCI code
        // .... ..00 0000 0000 group value request
        // .... ..00 01nn nnnn group value response
        // .... ..00 10nn nnnn group value write (not requested)
        // .... ..00 1100 0000 individual address write
        // .... ..01 0000 0000 individual address read
        // .... ..01 0100 0000 individual address response
        final var acpiCodeAsByte = apci.getCodeAsBytes();
        final byte[] apciDataAsByteArray;
        final byte apciDataAsByte;
        final boolean apciDataOptimized;
        final int npduLength;
        if (apci == APCI.GROUP_VALUE_READ) {
            // request only
            apciDataOptimized = false;
            apciDataAsByte = 0x00;
            apciDataAsByteArray = new byte[0];
            npduLength = 1;
        } else if (apci == APCI.GROUP_VALUE_RESPONSE || apci == APCI.GROUP_VALUE_WRITE) {
            Objects.requireNonNull(dataPointValue, "DataPointType is null.");
            if (dataPointValue instanceof PayloadOptimizable) {
                // optimized version available only for group value write and response
                // data is within 6 bits
                apciDataOptimized = true;
                apciDataAsByte = (byte) (dataPointValue.toByteArray()[0] & 0x3F);
                apciDataAsByteArray = new byte[0];
                npduLength = 1;
            } else {
                // data are as separate bytes
                apciDataOptimized = false;
                apciDataAsByte = 0x00;
                apciDataAsByteArray = dataPointValue.toByteArray();
                npduLength = 1 + apciDataAsByteArray.length;
            }
        } else {
            throw new KnxIllegalArgumentException("Current APCI is not supported: {}", apci.name());
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
        // NDPU (incl. TCPI and APCI and its data)
        bytes[byteCount++] = (byte) npduLength;
        bytes[byteCount++] = (byte) (tpciPaketTypeAsByte | tpciPacketNumberAsByte | acpiCodeAsByte[0]);
        bytes[byteCount++] = (byte) (acpiCodeAsByte[1] | apciDataAsByte);
        if (!apciDataOptimized) {
            System.arraycopy(apciDataAsByteArray, 0, bytes, byteCount, apciDataAsByteArray.length);
            byteCount += apciDataAsByteArray.length;
        }
        return of(Arrays.copyOf(bytes, byteCount));
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
            // 3 bytes for NPDU (minimum)
            throw new KnxNumberOutOfRangeException("cemiRawData", 11, 0xFF, cemiRawData.length, cemiRawData);
        }
    }

    public MessageCode getMessageCode() {
        return messageCode;
    }

    public AdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public ControlByte1 getControlByte1() {
        return controlByte1;
    }

    public ControlByte2 getControlByte2() {
        return controlByte2;
    }

    public IndividualAddress getSourceAddress() {
        return sourceAddress;
    }

    public KnxAddress getDestinationAddress() {
        return destinationAddress;
    }

    public int getLength() { return npduLength; }

    public TPCI getTPCI() {
        return tpci;
    }

    public int getPacketNumber() {
        return packetNumber;
    }

    public APCI getAPCI() {
        return apci;
    }

    public byte[] getData() {
        return data.clone();
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("messageCode", messageCode)
                .add("additionalInfo", additionalInfo)
                .add("controlByte1", controlByte1)
                .add("controlByte2", controlByte2)
                .add("sourceAddress", sourceAddress)
                .add("destinationAddress", destinationAddress)
                .add("npduLength", npduLength + " (" + ByteFormatter.formatHex(npduLength) + ")")
                .add("tpci", tpci)
                .add("packetNumber", packetNumber + " (" + ByteFormatter.formatHex(packetNumber) + ")")
                .add("apci", apci)
                .add("data", Arrays.toString(data) + " (" + ByteFormatter.formatHexAsString(data) + ")");
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", getRawDataAsHexString());
        }
        return h.toString();
    }
}
