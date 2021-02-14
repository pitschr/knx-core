/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.MultiRawDataAware;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Networker;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
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
 * | KNX device KNX Serial Number                                  |
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
public final class DeviceInformationDIB implements MultiRawDataAware {
    /**
     * Structure Length for {@link DeviceInformationDIB}
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
    private final byte[] serialNumber;
    private final InetAddress multicastAddress;
    private final byte[] macAddress;
    private final String deviceFriendlyName;
    private final byte[] bytes;

    private DeviceInformationDIB(final byte[] bytes) {
        this.bytes = bytes.clone();

        // bytes[0] -> length not relevant
        // bytes[1] -> description type not relevant

        this.mediumType = MediumType.valueOf(bytes[2]);
        // .... ...0 = not in programming mode (default)
        // .... ...1 = in programming mode
        this.programmingMode = (bytes[3] & 0x01) == 0x01;
        this.individualAddress = IndividualAddress.of(new byte[]{bytes[4], bytes[5]});
        this.projectNumber = (Bytes.toUnsignedInt(bytes[6], bytes[7]) & 0xFFF8) >> 3;
        this.projectInstallationIdentifier = bytes[7] & 0x07;
        this.serialNumber = Arrays.copyOfRange(bytes, 8, 14);
        this.multicastAddress = Networker.getByAddress(bytes[14], bytes[15], bytes[16], bytes[17]);
        this.macAddress = Arrays.copyOfRange(bytes, 18, 24);

        // device friendly name (and removes all characters with 0x00 at the end)
        // according to specification the device friendly name is ISO 8859-1 encoded
        this.deviceFriendlyName = new String(Arrays.copyOfRange(bytes, 24, 54), StandardCharsets.ISO_8859_1).replaceAll("\0+$", "");
    }

    /**
     * Builds a new {@link DeviceInformationDIB} instance
     *
     * @param bytes complete byte array for {@link DeviceInformationDIB}
     * @return a new immutable {@link DeviceInformationDIB}
     */
    public static DeviceInformationDIB of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        Preconditions.checkArgument(bytes[0] == STRUCTURE_LENGTH,
                "Incompatible value for bytes[0]. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes[0]);
        Preconditions.checkArgument(bytes[1] == DescriptionType.DEVICE_INFO.getCodeAsByte(),
                "Incompatible value for bytes[1]. Expected '{}' but was: {}", DescriptionType.DEVICE_INFO.getCodeAsByte(), bytes[1]);

        return new DeviceInformationDIB(bytes);
    }

    public MediumType getMediumType() {
        return mediumType;
    }

    public boolean isProgrammingMode() {
        return programmingMode;
    }

    public IndividualAddress getIndividualAddress() {
        return individualAddress;
    }

    public int getProjectNumber() {
        return projectNumber;
    }

    public int getProjectInstallationIdentifier() {
        return projectInstallationIdentifier;
    }

    public String getSerialNumber() {
        return ByteFormatter.formatHexAsString(serialNumber);
    }

    public InetAddress getMulticastAddress() {
        return multicastAddress;
    }

    public String getMacAddress() {
        return IntStream.range(0, macAddress.length).mapToObj(i -> String.format("%02X", macAddress[i]))
                .collect(Collectors.joining(":"));
    }

    public String getDeviceFriendlyName() {
        return deviceFriendlyName;
    }

    @Override
    public byte[] toByteArray() {
        return bytes.clone();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("mediumType", mediumType.name())
                .add("programmingMode", programmingMode)
                .add("individualAddress", individualAddress.getAddress())
                .add("projectNumber", projectNumber)
                .add("projectInstallationIdentifier", projectInstallationIdentifier)
                .add("serialNumber", getSerialNumber())
                .add("multicastAddress", multicastAddress.getHostAddress())
                .add("macAddress", getMacAddress())
                .add("deviceFriendlyName", deviceFriendlyName)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof DeviceInformationDIB) {
            final var other = (DeviceInformationDIB) obj;
            return Objects.equals(this.mediumType, other.mediumType)
                    && Objects.equals(this.programmingMode, other.programmingMode)
                    && Objects.equals(this.individualAddress, other.individualAddress)
                    && Objects.equals(this.projectNumber, other.projectNumber)
                    && Objects.equals(this.projectInstallationIdentifier, other.projectInstallationIdentifier)
                    && Arrays.equals(this.serialNumber, other.serialNumber)
                    && Objects.equals(this.multicastAddress, other.multicastAddress)
                    && Arrays.equals(this.macAddress, other.macAddress)
                    && Objects.equals(this.deviceFriendlyName, other.deviceFriendlyName)
                    && Arrays.equals(this.bytes, other.bytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediumType, programmingMode, individualAddress, projectNumber,
                projectInstallationIdentifier, Arrays.hashCode(serialNumber), multicastAddress,
                Arrays.hashCode(macAddress), deviceFriendlyName, Arrays.hashCode(bytes));
    }
}
