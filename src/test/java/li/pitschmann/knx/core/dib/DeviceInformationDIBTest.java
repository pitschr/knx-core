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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link DeviceInformationDIB}
 *
 * @author PITSCHR
 */
final class DeviceInformationDIBTest {

    /**
     * <pre>
     *  DIB: DEVICE_INFO
     *       Structure Length: 54 octets
     *       Description Type: DEVICE_INFO (0x01)
     *       KNX medium: KNX TP (0x02)
     *       Device Status: 0x00
     *           0000 000. = reserved: 0x00
     *           .... ...0 = program mode: 0
     *       KNX Address 1.0.0
     *       Project-Installation identifier: 0x1234 (0001 0010 0011 0100)
     *           Project number 582
     *           Installation number 4
     *       KNX device serial number: 008899AABBCC
     *       KNX device multicast address: 224.0.23.12 (224.0.23.12)
     *       KNX device MAC address: knx-ip-router (01:02:03:04:05:AA)
     *       Device Friendly Name: MDT KNX IP Router
     * </pre>
     */
    @Test
    @DisplayName("Test #of(byte[]) for MDT KNX/IP Router")
    void testOf_MDT_KNX_IP_Router() {
        final var bytes = new byte[]{
                0x36,                                           // Structure Length
                0x01,                                           // Description Type Code
                0x02,                                           // KNX medium
                0x00,                                           // Device Status
                0x10, 0x00,                                     // KNX Individual Address
                0x12, 0x34,                                     // Project-Installation identifier
                0x00, (byte) 0x88, (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // KNX device Serial Number
                (byte) 0xe0, 0x00, 0x17, 0x0c,                  // KNX device routing multicast address
                0x01, 0x02, 0x03, 0x04, 0x05, (byte) 0xAA,      // KNX device MAC address
                0x4d, 0x44, 0x54, 0x20, 0x4b, 0x4e, 0x58, 0x20, // Device Friendly Name
                0x49, 0x50, 0x20, 0x52, 0x6f, 0x75, 0x74, 0x65, // Device Friendly Name (continued)
                0x72, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // Device Friendly Name (continued)
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00              // Device Friendly Name (continued)
        };
        final var dibByBytes = DeviceInformationDIB.of(bytes);

        // compare
        assertThat(dibByBytes.getMediumType()).isSameAs(MediumType.TP);
        assertThat(dibByBytes.isProgrammingMode()).isFalse();
        assertThat(dibByBytes.getIndividualAddress().getAddress()).isEqualTo("1.0.0");
        assertThat(dibByBytes.getProjectNumber()).isEqualTo(582);
        assertThat(dibByBytes.getProjectInstallationIdentifier()).isEqualTo(4);
        assertThat(dibByBytes.getSerialNumber()).isEqualTo("0x00 88 99 AA BB CC");
        assertThat(dibByBytes.getMulticastAddress().getHostAddress()).isEqualTo("224.0.23.12");
        assertThat(dibByBytes.getMacAddress()).isEqualTo("01:02:03:04:05:AA");
        assertThat(dibByBytes.getDeviceFriendlyName()).isEqualTo("MDT KNX IP Router");
        assertThat(dibByBytes.toByteArray()).containsExactly(bytes);
        assertThat(dibByBytes).hasToString(
                "DeviceInformationDIB{" +
                        "mediumType=TP, " +
                        "programmingMode=false, " +
                        "individualAddress=1.0.0, " +
                        "projectNumber=582, " +
                        "projectInstallationIdentifier=4, " +
                        "serialNumber=0x00 88 99 AA BB CC, " +
                        "multicastAddress=224.0.23.12, " +
                        "macAddress=01:02:03:04:05:AA, " +
                        "deviceFriendlyName=MDT KNX IP Router" +
                        "}"
        );
    }

    /**
     * <pre>
     * 	DIB: DEVICE_INFO
     * 		Structure Length: 54 octets
     * 		Description Type: DEVICE_INFO (0x01)
     * 		KNX medium: KNX TP (0x02)
     * 		Device Status: 0x01
     * 			0000 000. = reserved: 0x00
     * 			.... ...1 = program mode: 1
     * 		KNX Address 0.0.0
     * 		Project-Installation identifier: 0x0000
     * 			Project number 0
     * 			Installation number 0
     * 		KNX device serial number: 360102000000
     * 		KNX device multicast address: 224.0.23.12 (224.0.23.12)
     * 		KNX device MAC address: 00:00:00_00:00:00 (00:00:00:00:00:00)
     * 		Device Friendly Name: eibd on WireGate
     * </pre>
     */
    @Test
    @DisplayName("Test #of(byte[]) for MDT KNX/IP Router")
    void testOf_EIBD_on_WireGate() {
        final var bytes = new byte[]{
                0x36,                                           // Structure Length
                0x01,                                           // Description Type Code
                0x02,                                           // KNX medium
                0x01,                                           // Device Status
                0x00, 0x00,                                     // KNX Individual Address
                0x00, 0x00,                                     // Project-Installation identifier
                0x36, 0x01, 0x02, 0x00, 0x00, 0x00,             // KNX device Serial Number
                (byte) 0xe0, 0x00, 0x17, 0x0c,                  // KNX device routing multicast address
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00,             // KNX device MAC address
                0x65, 0x69, 0x62, 0x64, 0x20, 0x6f, 0x6e, 0x20, // Device Friendly Name
                0x57, 0x69, 0x72, 0x65, 0x47, 0x61, 0x74, 0x65, // Device Friendly Name (continued)
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // Device Friendly Name (continued)
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00              // Device Friendly Name (continued)

        };
        final var dibByBytes = DeviceInformationDIB.of(bytes);

        // compare
        assertThat(dibByBytes.getMediumType()).isEqualTo(MediumType.TP);
        assertThat(dibByBytes.isProgrammingMode()).isTrue();
        assertThat(dibByBytes.getIndividualAddress().getAddress()).isEqualTo("0.0.0");
        assertThat(dibByBytes.getProjectNumber()).isEqualTo(0);
        assertThat(dibByBytes.getProjectInstallationIdentifier()).isEqualTo(0);
        assertThat(dibByBytes.getSerialNumber()).isEqualTo("0x36 01 02 00 00 00");
        assertThat(dibByBytes.getMulticastAddress().getHostAddress()).isEqualTo("224.0.23.12");
        assertThat(dibByBytes.getMacAddress()).isEqualTo("00:00:00:00:00:00");
        assertThat(dibByBytes.getDeviceFriendlyName()).isEqualTo("eibd on WireGate");
        assertThat(dibByBytes.toByteArray()).containsExactly(bytes);
        assertThat(dibByBytes).hasToString(
                "DeviceInformationDIB{" +
                        "mediumType=TP, " +
                        "programmingMode=true, " +
                        "individualAddress=0.0.0, " +
                        "projectNumber=0, " +
                        "projectInstallationIdentifier=0, " +
                        "serialNumber=0x36 01 02 00 00 00, " +
                        "multicastAddress=224.0.23.12, " +
                        "macAddress=00:00:00:00:00:00, " +
                        "deviceFriendlyName=eibd on WireGate" +
                        "}"
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_of_Bytes() {
        // null
        assertThatThrownBy(() -> DeviceInformationDIB.of(null))
                .isInstanceOf(NullPointerException.class);

        // incorrect size of bytes
        assertThatThrownBy(() -> DeviceInformationDIB.of(new byte[3]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '54' but was: 3");

        // incorrect structure length on index 0
        final var bytesInvalidStructureLength = new byte[54];
        bytesInvalidStructureLength[0] = 0x25; // not correct
        bytesInvalidStructureLength[1] = DescriptionType.DEVICE_INFO.getCodeAsByte(); // correct
        assertThatThrownBy(() -> DeviceInformationDIB.of(bytesInvalidStructureLength))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible value for bytes[0]. Expected '54' but was: 37");

        // incorrect description type on index 1
        final var bytesInvalidDescriptionType = new byte[54];
        bytesInvalidDescriptionType[0] = 0x36; // correct
        bytesInvalidDescriptionType[1] = DescriptionType.UNKNOWN.getCodeAsByte(); // not correct
        assertThatThrownBy(() -> DeviceInformationDIB.of(bytesInvalidDescriptionType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible value for bytes[1]. Expected '1' but was: -1");

    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DeviceInformationDIB.class).verify();
    }

}
