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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.dib.DeviceHardwareInformationDIB;
import li.pitschmann.knx.core.dib.SupportedDeviceFamiliesDIB;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.HostProtocol;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Networker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link SearchResponseBody}
 *
 * @author PITSCHR
 */
public class SearchResponseBodyTest {
    private HPAI controlEndpoint;
    private DeviceHardwareInformationDIB deviceHardwareInformation;
    private SupportedDeviceFamiliesDIB supportedDeviceFamilies;

    @BeforeEach
    public void before() {
        this.controlEndpoint = HPAI.of(HostProtocol.IPV4_UDP, Networker.getByAddress(6, 2, 77, 4), 8332);
        this.deviceHardwareInformation = DeviceHardwareInformationDIB.of(new byte[]{ //
                0x36, // Structure Length
                0x01, // Description Type Code
                0x02, // KNX medium
                0x00, // Device Status
                0x10, 0x00, // KNX Individual Address
                0x00, 0x00, // Project-Installation identifier
                0x00, (byte) 0x88, (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // KNX device Serial Number
                (byte) 0xe0, 0x00, 0x17, 0x0c, // KNX device routing multicast address
                0x01, 0x02, 0x03, 0x04, 0x05, (byte) 0xAA, // KNX device MAC address
                0x4d, 0x44, 0x54, 0x20, 0x4b, 0x4e, 0x58, 0x20, // Device Friendly Name
                0x49, 0x50, 0x20, 0x52, 0x6f, 0x75, 0x74, 0x65, // Device Friendly Name (continued)
                0x72, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // Device Friendly Name (continued)
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // Device Friendly Name (continued)
        });
        this.supportedDeviceFamilies = SupportedDeviceFamiliesDIB.of(new byte[]{ //
                0x0a, // Structure Length
                0x02, // Description Type Code
                0x02, 0x01, // Service Family ID + Version #1
                0x03, 0x02, // Service Family ID + Version #2
                0x04, 0x01, // Service Family ID + Version #3
                0x05, 0x03 // Service Family ID + Version #4
        });
    }

    /**
     * Tests the
     * {@link SearchResponseBody#of(HPAI, DeviceHardwareInformationDIB, SupportedDeviceFamiliesDIB)}
     * and {@link SearchResponseBody#of(byte[])} methods.
     *
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: DESCRIPTION_RESPONSE (0x0204)
     * 	        Total Length: 70 octets
     * 	    Body
     * 	        DIB: DEVICE_INFO
     * 	            Structure Length: 54 octets
     * 	            Description Type: DEVICE_INFO (0x01)
     * 	            KNX medium: KNX TP (0x02)
     * 	            Device Status: 0x00
     * 	                0000 000. = reserved: 0x00
     * 	                .... ...0 = program mode: 0
     * 	            KNX Address 1.0.0
     * 	            Project-Installation identifier: 0x0000
     * 	                Project number 0
     * 	                Installation number 0
     * 	            KNX device serial number: 0088AABBCC
     * 	            KNX device multicast address: 224.0.23.12
     * 	            KNX device MAC address: 01:02:03:04:05:aa
     * 	            Device Friendly Name: MDT KNX IP Router
     * 	        DIB: SUPP_SVC_FAMILIES
     * 	            Structure Length: 10 octets
     * 	            Description Type: SUPP_SVC_FAMILIES (0x02)
     * 	            Service ID: KNX/IP Core (0x02)
     * 	                Version: 1
     * 	            Service ID: KNX device Management (0x03)
     * 	                Version: 1
     * 	            Service ID: KNX/IP Tunneling (0x04)
     * 	                Version: 1
     * 	            Service ID: KNX/IP Routing (0x05)
     * 	                Version: 1
     * </pre>
     */
    @Test
    public void validCases() {
        // create
        final var body = SearchResponseBody.of(this.controlEndpoint, this.deviceHardwareInformation, this.supportedDeviceFamilies);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.SEARCH_RESPONSE);
        assertThat(body.getControlEndpoint()).isEqualTo(this.controlEndpoint);
        assertThat(body.getDeviceInformation()).isEqualTo(this.deviceHardwareInformation);
        assertThat(body.getSupportedDeviceFamilies()).isEqualTo(this.supportedDeviceFamilies);

        // create by bytes
        byte[] bytes = Bytes.concat(this.controlEndpoint.getRawData(), this.deviceHardwareInformation.getRawData(), this.supportedDeviceFamilies.getRawData());
        final var bodyByBytes = SearchResponseBody.of(bytes);

        // compare raw data of 'create' and 'create by bytes'
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format(
                "SearchResponseBody{controlEndpoint=%s, deviceHardwareInformation=%s, supportedDeviceFamilies=%s, rawData=%s}",
                this.controlEndpoint.toString(false), //
                this.deviceHardwareInformation.toString(false), //
                this.supportedDeviceFamilies.toString(false), //
                ByteFormatter.formatHexAsString(bytes)));
    }

    /**
     * Tests {@link SearchResponseBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> SearchResponseBody.of(null, this.deviceHardwareInformation, this.supportedDeviceFamilies)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("controlEndpoint");
        assertThatThrownBy(() -> SearchResponseBody.of(this.controlEndpoint, null, this.supportedDeviceFamilies)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("deviceHardwareInformation");
        assertThatThrownBy(() -> SearchResponseBody.of(this.controlEndpoint, this.deviceHardwareInformation, null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("supportedDeviceFamilies");

        // invalid raw data length
        assertThatThrownBy(() -> SearchResponseBody.of(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");

        // not proper size of byte array provided
        assertThatThrownBy(() -> SearchResponseBody.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class);

        // size if byte array must be divisible by two
        assertThatThrownBy(() -> SearchResponseBody.of(new byte[59])).isInstanceOf(KnxIllegalArgumentException.class)
                .hasMessageContaining("The size of 'rawData' must be divisible by two.");

        // missing device information DIB
        final var bytesNoDeviceHardwareInfo = Bytes.concat(this.controlEndpoint.getRawData(), new byte[DeviceHardwareInformationDIB.STRUCTURE_LENGTH]);//Bytes.padRight(new byte[]{0x36, 0x02, 0x02}, (byte)0x00, DeviceHardwareInformationDIB.STRUCTURE_LENGTH));
        assertThatThrownBy(() -> SearchResponseBody.of(bytesNoDeviceHardwareInfo)).isInstanceOf(KnxException.class)
                .hasMessage("Could not find device hardware information DIB array.");

        // missing supported device families DIB
        final var bytesNoDeviceFamilies = Bytes.concat(this.controlEndpoint.getRawData(), this.deviceHardwareInformation.getRawData(), new byte[2]);//Bytes.padRight(new byte[]{0x36, 0x02, 0x02}, (byte)0x00, DeviceHardwareInformationDIB.STRUCTURE_LENGTH));
        assertThatThrownBy(() -> SearchResponseBody.of(bytesNoDeviceFamilies)).isInstanceOf(KnxException.class)
                .hasMessage("Could not find supported device families DIB array.");
    }
}
