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

package li.pitschmann.knx.link.body;

import li.pitschmann.knx.link.body.dib.DeviceHardwareInformationDIB;
import li.pitschmann.knx.link.body.dib.IPConfigDIB;
import li.pitschmann.knx.link.body.dib.IPCurrentConfigDIB;
import li.pitschmann.knx.link.body.dib.KnxAddressesDIB;
import li.pitschmann.knx.link.body.dib.ManufacturerDataDIB;
import li.pitschmann.knx.link.body.dib.SupportedDeviceFamiliesDIB;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.utils.ByteFormatter;
import li.pitschmann.knx.utils.Bytes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link DescriptionResponseBody}
 *
 * @author PITSCHR
 */
public class DescriptionResponseBodyTest {
    // prepare
    private DeviceHardwareInformationDIB deviceHardwareInformation;
    private SupportedDeviceFamiliesDIB supportedDeviceFamilies;
    private IPConfigDIB ipConfig;
    private IPCurrentConfigDIB ipCurrentConfig;
    private KnxAddressesDIB knxAddresses;
    private ManufacturerDataDIB manufacturerData;

    @BeforeEach
    public void before() {
        this.deviceHardwareInformation = DeviceHardwareInformationDIB.of(new byte[]{ //
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
        this.supportedDeviceFamilies = SupportedDeviceFamiliesDIB.of(new byte[]{ //
                0x0a, // Structure Length
                0x02, // Description Type Code
                0x02, 0x01, // Service Family ID + Version #1
                0x03, 0x02, // Service Family ID + Version #2
                0x04, 0x01, // Service Family ID + Version #3
                0x05, 0x03 // Service Family ID + Version #4
        });
        this.ipConfig = IPConfigDIB.of(new byte[]{ //
                0x10, // Structure Length
                0x03, // Description Type Code
                0x11, 0x22, 0x33, 0x44, // IP Address
                0x55, 0x66, 0x77, (byte) 0x88, // Subnet Mask
                (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // Default Gateway
                (byte) 0xDD, // IP Capabilities
                (byte) 0xEE // IP assignment method
        });
        this.ipCurrentConfig = IPCurrentConfigDIB.of(new byte[]{ //
                0x14, // Structure Length
                0x04, // Description Type Code
                0x11, 0x22, 0x33, 0x44, // Current IP Address
                0x55, 0x66, 0x77, (byte) 0x88, // Current Subnet Mask
                (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // Current Default Gateway
                (byte) 0xD1, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, // DHCP Server
                (byte) 0xDD, // IP Capabilities
                (byte) 0x00 // (reserved)
        });
        this.knxAddresses = KnxAddressesDIB.of(new byte[]{ //
                0x0A, // Structure Length
                0x05, // Description Type Code
                0x00, 0x00, // KNX Individual Address (0.0.0)
                0x12, 0x03, // Additional Individual Address 1 (1.2.3)
                0x78, 0x7F, // Additional Individual Address 2 (7.8.127)
                (byte) 0xFF, (byte) 0xFF, // Additional Individual Address 3 (15.15.255)
        });
        this.manufacturerData = ManufacturerDataDIB.of(new byte[]{ //
                0x13, // Structure Length
                (byte) 0xFE, // Description Type Code
                0x21, 0x22, // KNX Manufacturer ID
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, // Data
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47 // Data (continued)
        });
    }

    /**
     * Tests the
     * {@link DescriptionResponseBody#of(DeviceHardwareInformationDIB, SupportedDeviceFamiliesDIB)}
     * and {@link DescriptionResponseBody#of(byte[])} methods.
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
    public void validCase() {
        byte[] dibBytes = Bytes.concat(this.deviceHardwareInformation.getRawData(), this.supportedDeviceFamilies.getRawData());

        // create
        final var body = DescriptionResponseBody.of(this.deviceHardwareInformation, this.supportedDeviceFamilies);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.DESCRIPTION_RESPONSE);
        assertThat(body.getDeviceInformation()).isEqualTo(this.deviceHardwareInformation);
        assertThat(body.getSupportedDeviceFamilies()).isEqualTo(this.supportedDeviceFamilies);
        assertThat(body.getIPConfig()).isNull();
        assertThat(body.getIPCurrentConfig()).isNull();
        assertThat(body.getKnxAddresses()).isNull();
        assertThat(body.getManufacturerData()).isNull();

        // create by bytes
        final var bodyByBytes = DescriptionResponseBody.of(dibBytes);

        // compare raw data of 'create' and 'create by bytes'
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format(
                "DescriptionResponseBody{deviceHardwareInformation=%s, supportedDeviceFamilies=%s, ipConfig=null, "
                        + "ipCurrentConfig=null, knxAddresses=null, manufacturerData=null, rawData=%s}",
                this.deviceHardwareInformation.toString(false), this.supportedDeviceFamilies.toString(false),
                ByteFormatter.formatHexAsString(dibBytes)));
    }

    /**
     * Tests the {@link DescriptionResponseBody#of(byte[])} method with following DIBs:
     * <ul>
     * <li>DeviceHardwareInformationDIB</li>
     * <li>SupportedDeviceFamiliesDIB</li>
     * <li>IPConfigDIB</li>
     * <li>IPCurrentConfigDIB</li>
     * <li>KnxAddressesDIB</li>
     * <li>ManufacturerDataDIB</li>
     * </ul>
     */
    @Test
    public void validCaseAllDIBs() {
        final var dibBytes = Bytes.concat(this.deviceHardwareInformation.getRawData(), this.supportedDeviceFamilies.getRawData(),
                this.ipConfig.getRawData(), this.ipCurrentConfig.getRawData(), this.knxAddresses.getRawData(),
                this.manufacturerData.getRawData());

        // create
        final var body = DescriptionResponseBody.of(dibBytes);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.DESCRIPTION_RESPONSE);
        assertThat(body.getDeviceInformation()).isEqualTo(this.deviceHardwareInformation);
        assertThat(body.getSupportedDeviceFamilies()).isEqualTo(this.supportedDeviceFamilies);
        assertThat(body.getIPConfig()).isEqualTo(this.ipConfig);
        assertThat(body.getIPCurrentConfig()).isEqualTo(this.ipCurrentConfig);
        assertThat(body.getKnxAddresses()).isEqualTo(this.knxAddresses);
        assertThat(body.getManufacturerData()).isEqualTo(this.manufacturerData);

        // toString
        assertThat(body).hasToString(String.format(
                "DescriptionResponseBody{deviceHardwareInformation=%s, supportedDeviceFamilies=%s, ipConfig=%s, "
                        + "ipCurrentConfig=%s, knxAddresses=%s, manufacturerData=%s, rawData=%s}",
                this.deviceHardwareInformation.toString(false), this.supportedDeviceFamilies.toString(false), this.ipConfig.toString(false),
                this.ipCurrentConfig.toString(false), this.knxAddresses.toString(false), this.manufacturerData.toString(false),
                ByteFormatter.formatHexAsString(dibBytes)));

    }

    /**
     * Tests {@link DescriptionResponseBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> DescriptionResponseBody.of(null, this.supportedDeviceFamilies)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("deviceHardwareInformation");
        assertThatThrownBy(() -> DescriptionResponseBody.of(this.deviceHardwareInformation, null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("supportedDeviceFamilies");

        // invalid raw data length
        assertThatThrownBy(() -> DescriptionResponseBody.of(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");

        // missing device information DIB
        assertThatThrownBy(() -> DescriptionResponseBody.of(new byte[0])).isInstanceOf(KnxException.class)
                .hasMessage("Could not find device hardware information DIB array.");

        // missing supported device families DIB
        assertThatThrownBy(() -> DescriptionResponseBody.of(this.deviceHardwareInformation.getRawData())).isInstanceOf(KnxException.class)
                .hasMessage("Could not find supported device families DIB array.");

        // endless loop because of wrong DIB info
        assertThatThrownBy(() -> DescriptionResponseBody.of(Bytes.toByteArray("34010200100000000083497f01ece000170ccc1be08008da4d4454204b4e5820495020526f7574657200000000000000000000000000"))).isInstanceOf(KnxException.class)
                .hasMessageContaining("This would result into an endless loop");
    }
}
