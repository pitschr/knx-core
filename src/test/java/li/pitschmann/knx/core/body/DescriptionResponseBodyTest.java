/*
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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.dib.DeviceInformationDIB;
import li.pitschmann.knx.core.dib.IPConfigDIB;
import li.pitschmann.knx.core.dib.IPCurrentConfigDIB;
import li.pitschmann.knx.core.dib.KnxAddressesDIB;
import li.pitschmann.knx.core.dib.ManufacturerDataDIB;
import li.pitschmann.knx.core.dib.SupportedServiceFamiliesDIB;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Bytes;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link DescriptionResponseBody}
 *
 * @author PITSCHR
 */
class DescriptionResponseBodyTest {
    private final DeviceInformationDIB deviceInformation = DeviceInformationDIB.of(new byte[]{ //
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
    });
    private final SupportedServiceFamiliesDIB supportedServiceFamilies = SupportedServiceFamiliesDIB.of(new byte[]{ //
            0x0a, // Structure Length
            0x02, // Description Type Code
            0x02, 0x01, // Service Family ID + Version #1
            0x03, 0x02, // Service Family ID + Version #2
            0x04, 0x01, // Service Family ID + Version #3
            0x05, 0x03 // Service Family ID + Version #4
    });
    private final IPConfigDIB ipConfig = IPConfigDIB.of(new byte[]{ //
            0x10, // Structure Length
            0x03, // Description Type Code
            0x11, 0x22, 0x33, 0x44, // IP Address
            0x55, 0x66, 0x77, (byte) 0x88, // Subnet Mask
            (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // Default Gateway
            (byte) 0xDD, // IP Capabilities
            (byte) 0xEE // IP assignment method
    });
    private final IPCurrentConfigDIB ipCurrentConfig = IPCurrentConfigDIB.of(new byte[]{ //
            0x14, // Structure Length
            0x04, // Description Type Code
            0x11, 0x22, 0x33, 0x44, // Current IP Address
            0x55, 0x66, 0x77, (byte) 0x88, // Current Subnet Mask
            (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // Current Default Gateway
            (byte) 0xD1, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, // DHCP Server
            (byte) 0xDD, // IP Capabilities
            (byte) 0x00 // (reserved)
    });
    private final KnxAddressesDIB knxAddresses = KnxAddressesDIB.of(new byte[]{ //
            0x0A, // Structure Length
            0x05, // Description Type Code
            0x00, 0x00, // KNX Individual Address (0.0.0)
            0x12, 0x03, // Additional Individual Address 1 (1.2.3)
            0x78, 0x7F, // Additional Individual Address 2 (7.8.127)
            (byte) 0xFF, (byte) 0xFF, // Additional Individual Address 3 (15.15.255)
    });
    private final ManufacturerDataDIB manufacturerData = ManufacturerDataDIB.of(new byte[]{ //
            0x13, // Structure Length
            (byte) 0xFE, // Description Type Code
            0x21, 0x22, // KNX Manufacturer ID
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, // Data
            0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47 // Data (continued)
    });

    /**
     * Test {@link DescriptionResponseBody} with following DIBs:
     * <ul>
     * <li>{@link DeviceInformationDIB}</li>
     * <li>{@link SupportedServiceFamiliesDIB}</li>
     * </ul>
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
     * 	            Structure Length: 54 octets (0x36)
     * 	            Description Type: DEVICE_INFO (0x01)
     * 	            KNX medium: KNX TP (0x02)
     * 	            Device Status: 0x00
     * 	                0000 000. = reserved: 0x00
     * 	                .... ...0 = program mode: 0
     * 	            KNX Address 1.0.0
     * 	            Project-Installation identifier: 0x0000
     * 	                Project number 0
     * 	                Installation number 0
     * 	            KNX device serial number: 0083497f01ec
     * 	            KNX device multicast address: 224.0.23.12 (224.0.23.12)
     * 	            KNX device MAC address: PIT-KNX.pit-router (cc:1b:e0:80:08:da)
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
    void validCase() {
        // create by bytes
        final var bodyByBytes = DescriptionResponseBody.of(
                Bytes.concat(
                        deviceInformation.toByteArray(), //
                        supportedServiceFamilies.toByteArray() //
                )
        );

        // create
        final var body = DescriptionResponseBody.of(deviceInformation, supportedServiceFamilies);
        assertThat(body.getServiceType()).isSameAs(ServiceType.DESCRIPTION_RESPONSE);
        assertThat(body.getDeviceInformation()).isSameAs(deviceInformation);
        assertThat(body.getSupportedDeviceFamilies()).isSameAs(supportedServiceFamilies);
        assertThat(body.getIPConfig()).isNull();
        assertThat(body.getIPCurrentConfig()).isNull();
        assertThat(body.getKnxAddresses()).isNull();
        assertThat(body.getManufacturerData()).isNull();

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("DescriptionResponseBody{" + //
                                "deviceInformation=%s, " + //
                                "supportedServiceFamilies=%s, " + //
                                "ipConfig=null, " + //
                                "ipCurrentConfig=null, " + //
                                "knxAddresses=null, " + //
                                "manufacturerData=null" + //
                                "}", //
                        deviceInformation, //
                        supportedServiceFamilies //
                )
        );
    }

    /**
     * Test {@link DescriptionResponseBody} with following DIBs:
     * <ul>
     * <li>{@link DeviceInformationDIB}</li>
     * <li>{@link SupportedServiceFamiliesDIB}</li>
     * <li>{@link IPConfigDIB}</li>
     * <li>{@link IPCurrentConfigDIB}</li>
     * <li>{@link KnxAddressesDIB}</li>
     * <li>{@link ManufacturerDataDIB}</li>
     * </ul>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(... all DIBs ...)")
    void validCase_AllDIBs() {
        // create by bytes
        final var bodyByBytes = DescriptionResponseBody.of(
                Bytes.concat(
                        deviceInformation.toByteArray(), //
                        supportedServiceFamilies.toByteArray(), //
                        ipConfig.toByteArray(), //
                        ipCurrentConfig.toByteArray(), //
                        knxAddresses.toByteArray(), //
                        manufacturerData.toByteArray() //
                )
        );

        // create
        final var body = DescriptionResponseBody.of(deviceInformation, supportedServiceFamilies, ipConfig, ipCurrentConfig, knxAddresses, manufacturerData);
        assertThat(body.getServiceType()).isSameAs(ServiceType.DESCRIPTION_RESPONSE);
        assertThat(body.getDeviceInformation()).isSameAs(deviceInformation);
        assertThat(body.getSupportedDeviceFamilies()).isSameAs(supportedServiceFamilies);
        assertThat(body.getIPConfig()).isSameAs(ipConfig);
        assertThat(body.getIPCurrentConfig()).isSameAs(ipCurrentConfig);
        assertThat(body.getKnxAddresses()).isSameAs(knxAddresses);
        assertThat(body.getManufacturerData()).isSameAs(manufacturerData);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("DescriptionResponseBody{" + //
                                "deviceInformation=%s, " + //
                                "supportedServiceFamilies=%s, " + //
                                "ipConfig=%s, " + //
                                "ipCurrentConfig=%s, " + //
                                "knxAddresses=%s, " + //
                                "manufacturerData=%s" + //
                                "}", //
                        deviceInformation, //
                        supportedServiceFamilies, //
                        ipConfig, //
                        ipCurrentConfig, //
                        knxAddresses, //
                        manufacturerData //
                )
        );
    }


    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        // null
        assertThatThrownBy(() -> DescriptionResponseBody.of(null))
                .isInstanceOf(NullPointerException.class);

        // invalid length
        assertThatThrownBy(() -> DescriptionResponseBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [56..255] but was: 0");
    }

    @Test
    @DisplayName("No Device Information for #of(byte[])")
    void invalidCase_NoDeviceInfo() {
        final var bytes = Bytes.concat(
                // Device Information
                deviceInformation.toByteArray(),
                // Supported Service Families
                supportedServiceFamilies.toByteArray()
        );
        // change byte of Device Information Description Type which is on 1st byte array position
        bytes[1] = 0x00;

        // device information DIB not found because DEVICE_INFO has 0x01
        assertThatThrownBy(() -> DescriptionResponseBody.of(bytes))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure. No Device Information DIB.");
    }

    @Test
    @DisplayName("No Supported Service Families for #of(byte[])")
    void invalidCase_NoSupportedServiceFamilies() {
        final var bytes = Bytes.concat(
                // Device Information
                deviceInformation.toByteArray(),
                // Supported Service Families
                supportedServiceFamilies.toByteArray()
        );
        // change byte of Supported Service Families Description Type which is on 55th byte array position
        bytes[55] = 0x00;

        // supported service families DIB not found because SUPPORTED_SERVICE_FAMILIES has 0x02
        assertThatThrownBy(() -> DescriptionResponseBody.of(bytes))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure. No Supported Service Families DIB.");
    }

    @Test
    @DisplayName("Endless loop for #of(byte[]) due corrupted data")
    void invalidCase_endlessLoop() {
        final var bytes = Bytes.concat(
                // Device Information
                deviceInformation.toByteArray(),
                // Supported Service Families
                supportedServiceFamilies.toByteArray(),
                // add dummy byte array which causes an endless loop
                new byte[3]
        );

        assertThatThrownBy(() -> DescriptionResponseBody.of(bytes))
                .isInstanceOf(KnxException.class)
                .hasMessageStartingWith("Bad bytes provided. This would result into an endless loop:");
    }

    @Test
    @DisplayName("Invalid cases for #of(DeviceInformationDIB, SupportedServiceFamiliesDIB)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> DescriptionResponseBody.of(null, mock(SupportedServiceFamiliesDIB.class)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DIB about Device Information is required.");
        assertThatThrownBy(() -> DescriptionResponseBody.of(mock(DeviceInformationDIB.class), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DIB about Supported Service Families is required.");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DescriptionResponseBody.class).verify();
    }

}
