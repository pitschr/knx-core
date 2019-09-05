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

package li.pitschmann.knx.link.body.dib;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;
import li.pitschmann.utils.Networker;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Device Information DIB to specify DIB for type {@link DescriptionType#DEVICE_INFO}
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length            | Description Type Code           |
 * | (1 octet)                   | (1 octet)                       |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | KNX medium                  | Device Status                   |
 * | (1 octet)                   | (1 octet)                       |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | KNX Individual Address                                        |
 * | (2 octets)                                                    |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Project-Installation identifier                               |
 * | (2 octets)                                                    |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | KNX device KNX Serial Number                            |
 * | (6 octets)                                                    |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | KNX device routing multicast address                    |
 * | (4 octets)                                                    |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | KNX device MAC address                                  |
 * | (6 octets)                                                    |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Device Friendly Name                                          |
 * | (30 octets)                                                   |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * |                                                               |
 * | ... (more) ...                                                |
 * |                                                               |
 * |                                                               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * See: KNX Standard Core
 *
 * @author PITSCHR
 */
public final class DeviceHardwareInformationDIB extends AbstractDIB {
    /**
     * Structure Length for {@link DeviceHardwareInformationDIB}
     * <p>
     * 1 byte for Structure Length<br>
     * 1 byte for Description Type Code<br>
     * 1 byte for KNX medium<br>
     * 1 byte for Device Status<br>
     * 2 bytes for KNX Individual Address<br>
     * 2 bytes for Project-Installation identifier<br>
     * 6 bytes for KNX device KNX Serial Number<br>
     * 4 bytes for KNX device routing multicast address<br>
     * 6 bytes for KNX device MAC address<br>
     * 30 bytes for Device Friendly Name<br>
     */
    public static final int STRUCTURE_LENGTH = 54;
    private final MediumType mediumType;
    private final boolean programmingMode;
    private final IndividualAddress individualAddress;
    private final int projectNumber;
    private final int projectInstallationIdentifier;
    private final String serialNumber;
    private final InetAddress multicastAddress;
    private final String macAddress;
    private final String deviceFriendlyName;

    private DeviceHardwareInformationDIB(final @Nonnull byte[] rawData) {
        super(rawData);

        // rawData[0] -> length already covered in abstract class DIB
        // rawData[1] -> description type already covered in abstract class DIB
        this.mediumType = MediumType.valueOf(rawData[2]);
        // .... ...0 = not in programming mode (default)
        // .... ...1 = in programming mode
        this.programmingMode = (rawData[3] & 0x01) == 0x01;
        this.individualAddress = IndividualAddress.of(new byte[]{rawData[4], rawData[5]});
        this.projectNumber = (Bytes.toUnsignedInt(rawData[6], rawData[7]) & 0xFFF8) >> 3;
        this.projectInstallationIdentifier = rawData[7] & 0x07;
        this.serialNumber = ByteFormatter.formatHexAsString(Arrays.copyOfRange(rawData, 8, 14));
        this.multicastAddress = Networker.getByAddress(rawData[14], rawData[15], rawData[16], rawData[17]);

        // mac address
        byte[] macAddressAsBytes = Arrays.copyOfRange(rawData, 18, 24);
        this.macAddress = IntStream.range(0, macAddressAsBytes.length).mapToObj(i -> String.format("%02X", macAddressAsBytes[i]))
                .collect(Collectors.joining(":"));

        // device friendly name (and removes all characters with 0x00 at the end)
        // according to specification the device friendly name is ISO 8859-1 encoded
        this.deviceFriendlyName = new String(Arrays.copyOfRange(rawData, 24, 54), StandardCharsets.ISO_8859_1).replaceAll("\0+$", "");
    }

    /**
     * Builds a new {@link DeviceHardwareInformationDIB} instance
     *
     * @param bytes complete byte array for {@link DeviceHardwareInformationDIB}
     * @return a new immutable {@link DeviceHardwareInformationDIB}
     */
    @Nonnull
    public static DeviceHardwareInformationDIB of(final @Nonnull byte[] bytes) {
        return new DeviceHardwareInformationDIB(bytes);
    }

    @Override
    protected void validate(final @Nonnull byte[] rawData) {
        if (rawData.length != STRUCTURE_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_LENGTH, STRUCTURE_LENGTH, rawData.length, rawData);
        }
    }

    @Nonnull
    public MediumType getMediumType() {
        return this.mediumType;
    }

    public boolean isProgrammingMode() {
        return this.programmingMode;
    }

    @Nonnull
    public IndividualAddress getIndividualAddress() {
        return this.individualAddress;
    }

    public int getProjectNumber() {
        return this.projectNumber;
    }

    public int getProjectInstallationIdentifier() {
        return this.projectInstallationIdentifier;
    }

    @Nonnull
    public String getSerialNumber() {
        return this.serialNumber;
    }

    @Nonnull
    public InetAddress getMulticastAddress() {
        return this.multicastAddress;
    }

    @Nonnull
    public String getMacAddress() {
        return this.macAddress;
    }

    @Nonnull
    public String getDeviceFriendlyName() {
        return this.deviceFriendlyName;
    }

    @Nonnull
    @Override
    public String toString(boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("length", this.getLength() + " (" + ByteFormatter.formatHex(this.getLength()) + ")")
                .add("descriptionType", this.getDescriptionType())
                .add("mediumType", this.mediumType)
                .add("programmingMode", this.programmingMode)
                .add("individualAddress", this.individualAddress.toString(false))
                .add("projectNumber", this.projectNumber)
                .add("projectInstallationIdentifier", this.projectInstallationIdentifier)
                .add("serialNumber", this.serialNumber)
                .add("multicastAddress", this.multicastAddress)
                .add("macAddress", this.macAddress)
                .add("deviceFriendlyName", this.deviceFriendlyName);
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }

}
