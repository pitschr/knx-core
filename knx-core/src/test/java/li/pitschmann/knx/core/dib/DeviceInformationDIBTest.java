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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Bytes.FillDirection;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link DeviceHardwareInformationDIB}
 *
 * @author PITSCHR
 */
public final class DeviceInformationDIBTest {
    private static final byte[] BYTES = new byte[]{ //
            0x36, // Structure Length
            0x01, // Description Type Code
            0x02, // KNX medium
            0x00, // Device Status
            0x10, 0x00, // KNX Individual Address
            0x12, 0x34, // Project-Installation identifier
            0x00, (byte) 0x88, (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // KNX device Serial Number
            (byte) 0xe0, 0x00, 0x17, 0x0c, // KNX device routing multicast address
            0x01, 0x02, 0x03, 0x04, 0x05, (byte) 0xAA, // KNX device MAC address
            0x4d, 0x44, 0x54, 0x20, 0x4b, 0x4e, 0x58, 0x20, // Device Friendly Name
            0x49, 0x50, 0x20, 0x52, 0x6f, 0x75, 0x74, 0x65, // Device Friendly Name (continued)
            0x72, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // Device Friendly Name (continued)
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // Device Friendly Name (continued)
    };

    /**
     * Tests {@link DeviceHardwareInformationDIB#of(byte[])}
     *
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
    public void valueOfMDT() {
        // create by bytes
        final var infoByValueOf = DeviceHardwareInformationDIB.of(BYTES);

        // compare
        assertThat(infoByValueOf.getLength()).isEqualTo(54);
        assertThat(infoByValueOf.getDescriptionType()).isEqualTo(DescriptionType.DEVICE_INFO);
        assertThat(infoByValueOf.getMediumType()).isEqualTo(MediumType.TP);
        assertThat(infoByValueOf.isProgrammingMode()).isFalse();
        assertThat(infoByValueOf.getIndividualAddress().getAddress()).isEqualTo("1.0.0");
        assertThat(infoByValueOf.getProjectNumber()).isEqualTo(582);
        assertThat(infoByValueOf.getProjectInstallationIdentifier()).isEqualTo(4);
        assertThat(infoByValueOf.getSerialNumber()).isEqualTo("0x00 88 99 AA BB CC");
        assertThat(infoByValueOf.getMulticastAddress().getHostAddress()).isEqualTo("224.0.23.12");
        assertThat(infoByValueOf.getMacAddress()).isEqualTo("01:02:03:04:05:AA");
        assertThat(infoByValueOf.getDeviceFriendlyName()).isEqualTo("MDT KNX IP Router");
    }

    /**
     * Tests {@link DeviceHardwareInformationDIB#of(byte[])}
     *
     * <pre>
     * 	DIB: DEVICE_INFO
     * 		Structure Length: 54 octets
     * 		Description Type: DEVICE_INFO (0x01)
     * 		KNX medium: KNX TP (0x02)
     * 		Device Status: 0x00
     * 			0000 000. = reserved: 0x00
     * 			.... ...0 = program mode: 0
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
    public void valueOfWireGate() {
        // create by bytes
        final var infoByValueOf = DeviceHardwareInformationDIB
                .of(new byte[]{0x36, 0x01, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00, 0x36, 0x01, 0x02, 0x00, 0x00, 0x00, (byte) 0xe0, 0x00,
                        0x17, 0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x65, 0x69, 0x62, 0x64, 0x20, 0x6f, 0x6e, 0x20, 0x57, 0x69, 0x72,
                        0x65, 0x47, 0x61, 0x74, 0x65, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});

        // compare
        assertThat(infoByValueOf.getLength()).isEqualTo(54);
        assertThat(infoByValueOf.getDescriptionType()).isEqualTo(DescriptionType.DEVICE_INFO);
        assertThat(infoByValueOf.getMediumType()).isEqualTo(MediumType.TP);
        assertThat(infoByValueOf.isProgrammingMode()).isTrue();
        assertThat(infoByValueOf.getIndividualAddress().getAddress()).isEqualTo("0.0.0");
        assertThat(infoByValueOf.getProjectNumber()).isEqualTo(0);
        assertThat(infoByValueOf.getProjectInstallationIdentifier()).isEqualTo(0);
        assertThat(infoByValueOf.getSerialNumber()).isEqualTo("0x36 01 02 00 00 00");
        assertThat(infoByValueOf.getMulticastAddress().getHostAddress()).isEqualTo("224.0.23.12");
        assertThat(infoByValueOf.getMacAddress()).isEqualTo("00:00:00:00:00:00");
        assertThat(infoByValueOf.getDeviceFriendlyName()).isEqualTo("eibd on WireGate");
    }

    /**
     * Tests {@link DeviceHardwareInformationDIB} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // incorrect size of bytes
        assertThatThrownBy(() -> DeviceHardwareInformationDIB
                .of(Bytes.fillByteArray(new byte[55], new byte[]{0x37, 0x01, 0x02}, FillDirection.LEFT_TO_RIGHT)))
                .isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("rawData");
    }

    /**
     * Test {@link DeviceHardwareInformationDIB#toString()}
     */
    @Test
    public void testToString() {
        assertThat(DeviceHardwareInformationDIB.of(BYTES)).hasToString(String.format(
                "DeviceHardwareInformationDIB{"
                        + "length=54 (0x36), descriptionType=%s, mediumType=%s, programmingMode=false, individualAddress=%s, "
                        + "projectNumber=582, projectInstallationIdentifier=4, serialNumber=0x00 88 99 AA BB CC, "
                        + "multicastAddress=224.0.23.12, macAddress=01:02:03:04:05:AA, deviceFriendlyName=MDT KNX IP Router, rawData=%s}",
                DescriptionType.DEVICE_INFO, MediumType.TP, IndividualAddress.of(new byte[]{0x10, 0x00}).toString(false),
                ByteFormatter.formatHexAsString(BYTES)));
    }
}
