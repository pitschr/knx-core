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

import li.pitschmann.knx.core.dib.DeviceInformationDIB;
import li.pitschmann.knx.core.dib.SupportedServiceFamiliesDIB;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.HostProtocol;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Networker;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link SearchResponseBody}
 *
 * @author PITSCHR
 */
class SearchResponseBodyTest {
    private HPAI controlEndpoint = HPAI.of(
            HostProtocol.IPV4_UDP,
            Networker.getByAddress(6, 2, 77, 4),
            8332
    );
    private DeviceInformationDIB deviceInformation = DeviceInformationDIB.of(new byte[]{ //
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
    private SupportedServiceFamiliesDIB supportedServiceFamilies = SupportedServiceFamiliesDIB.of(new byte[]{ //
            0x0a, // Structure Length
            0x02, // Description Type Code
            0x02, 0x01, // Service Family ID + Version #1
            0x03, 0x02, // Service Family ID + Version #2
            0x04, 0x01, // Service Family ID + Version #3
            0x05, 0x03  // Service Family ID + Version #4
    });

    /**
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
     * 	                Version: 2
     * 	            Service ID: KNX/IP Tunneling (0x04)
     * 	                Version: 1
     * 	            Service ID: KNX/IP Routing (0x05)
     * 	                Version: 3
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(HPAI, DeviceInformationDIB, SupportedServiceFamiliesDIB)")
    void validCases() {
        // create by bytes
        final var bodyByBytes = SearchResponseBody.of(
                Bytes.concat(
                        controlEndpoint.getRawData(),
                        deviceInformation.getRawData(),
                        supportedServiceFamilies.getRawData()
                )
        );

        // create
        final var body = SearchResponseBody.of(controlEndpoint, deviceInformation, supportedServiceFamilies);
        assertThat(body.getServiceType()).isSameAs(ServiceType.SEARCH_RESPONSE);
        assertThat(body.getControlEndpoint()).isSameAs(controlEndpoint);
        assertThat(body.getDeviceInformation()).isSameAs(deviceInformation);
        assertThat(body.getSupportedDeviceFamilies()).isSameAs(supportedServiceFamilies);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("SearchResponseBody{controlEndpoint=%s, deviceInformation=%s, supportedServiceFamilies=%s}",
                        controlEndpoint, //
                        deviceInformation, //
                        supportedServiceFamilies //
                )
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        assertThatThrownBy(() -> SearchResponseBody.of(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> SearchResponseBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [58..254] but was: 0");
    }

    @Test
    @DisplayName("No Device Information for #of(byte[])")
    void invalidCase_NoDeviceInfo() {
        final var bytes = Bytes.concat(
                // Control Endpoint
                controlEndpoint.getRawData(),
                // Device Information
                deviceInformation.getRawData(),
                // Supported Service Families
                supportedServiceFamilies.getRawData()
        );
        // change byte of Device Information Description Type which is on 9th byte array position
        bytes[9] = 0x00;

        // device information DIB not found because DEVICE_INFO has 0x01
        assertThatThrownBy(() -> SearchResponseBody.of(bytes))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure. No Device Information DIB.");
    }

    @Test
    @DisplayName("No Supported Service Families for #of(byte[])")
    void invalidCase_NoSupportedServiceFamilies() {
        final var bytes = Bytes.concat(
                // Control Endpoint
                controlEndpoint.getRawData(),
                // Device Information
                deviceInformation.getRawData(),
                // Supported Service Families
                supportedServiceFamilies.getRawData()
        );
        // change byte of Supported Service Families Description Type which is on 63th byte array position
        bytes[63] = 0x00;

        // supported service families DIB not found because SUPPORTED_SERVICE_FAMILIES has 0x02
        assertThatThrownBy(() -> SearchResponseBody.of(bytes))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure. No Supported Service Families DIB.");
    }

    @Test
    @DisplayName("Invalid cases for #of(HPAI, DeviceInformationDIB, SupportedServiceFamiliesDIB)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> SearchResponseBody.of(null, mock(DeviceInformationDIB.class), mock(SupportedServiceFamiliesDIB.class)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Control Endpoint is required.");
        assertThatThrownBy(() -> SearchResponseBody.of(mock(HPAI.class), null, mock(SupportedServiceFamiliesDIB.class)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DIB about Device Information is required.");
        assertThatThrownBy(() -> SearchResponseBody.of(mock(HPAI.class), mock(DeviceInformationDIB.class), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DIB about Supported Service Families is required.");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(SearchResponseBody.class).verify();
    }

}
