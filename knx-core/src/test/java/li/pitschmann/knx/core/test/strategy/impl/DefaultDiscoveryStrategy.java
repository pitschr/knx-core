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

package li.pitschmann.knx.core.test.strategy.impl;

import li.pitschmann.knx.core.body.SearchResponseBody;
import li.pitschmann.knx.core.dib.DeviceHardwareInformationDIB;
import li.pitschmann.knx.core.dib.SupportedDeviceFamiliesDIB;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.net.HostProtocol;
import li.pitschmann.knx.core.test.MockRequest;
import li.pitschmann.knx.core.test.MockResponse;
import li.pitschmann.knx.core.test.MockServer;
import li.pitschmann.knx.core.test.strategy.DiscoveryStrategy;
import li.pitschmann.knx.core.utils.Networker;

/**
 * Default implementation for {@link DiscoveryStrategy}
 * <p>
 * {@inheritDoc}
 */
public class DefaultDiscoveryStrategy implements DiscoveryStrategy {
    private static DeviceHardwareInformationDIB DEFAULT_DEVICE_HARDWARE_INFORMATION_DIB;
    private static SupportedDeviceFamiliesDIB DEFAULT_SUPPORTED_DEVICE_FAMILIES;

    static {
        DEFAULT_DEVICE_HARDWARE_INFORMATION_DIB = DeviceHardwareInformationDIB.of(new byte[]{ //
                0x36, // Structure Length
                0x01, // Description Type Code
                0x02, // KNX medium
                0x00, // Device Status
                0x10, 0x00, // KNX Individual Address
                0x12, 0x34, // Project-Installation identifier
                0x00, (byte) 0x88, (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // KNX device Serial Number
                (byte) 0xe0, 0x00, 0x17, 0x0c, // KNX device routing multicast address
                0x01, 0x02, 0x03, 0x04, 0x05, (byte) 0xAA, // KNX device MAC address
                0x4d, 0x6f, 0x63, 0x6b, 0x20, 0x53, 0x65, 0x72, // Device Friendly Name (=Mock Server)
                0x76, 0x65, 0x72, 0x00, 0x00, 0x00, 0x00, 0x00, // Device Friendly Name (continued)
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // Device Friendly Name (continued)
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // Device Friendly Name (continued)
        });
        DEFAULT_SUPPORTED_DEVICE_FAMILIES = SupportedDeviceFamiliesDIB.of(new byte[]{ //
                0x0a, // Structure Length
                0x02, // Description Type Code
                0x02, 0x01, // Service Family ID (Core) + Version #1
                0x03, 0x02, // Service Family ID (Device Management)+ Version #2
                0x04, 0x01, // Service Family ID (Tunneling) + Version #3
                0x05, 0x03 // Service Family ID (Routing) + Version #4
        });
    }

    /**
     * Returns the device hardware information DIB. This method can be overridden.
     *
     * @param mockServer
     * @return DIB about device hardware information
     */
    protected DeviceHardwareInformationDIB getDeviceHardwareInformation(final MockServer mockServer) {
        return DEFAULT_DEVICE_HARDWARE_INFORMATION_DIB;
    }

    /**
     * Returns the supported device families (e.g. TUNNELING, ROUTING, ...).
     * This method can be overridden.
     *
     * @param mockServer
     * @return DIB supported device families
     */
    protected SupportedDeviceFamiliesDIB getSupportedDeviceFamilies(final MockServer mockServer) {
        return DEFAULT_SUPPORTED_DEVICE_FAMILIES;
    }

    @Override
    public MockResponse createResponse(final MockServer mockServer, final MockRequest request) {
        // use 'localhost' instead of channel as the address would be 0.0.0.0 otherwise
        final var hpai = HPAI.of(HostProtocol.IPV4_UDP, Networker.getLocalHost(), mockServer.getHPAI().getPort());
        final var deviceHardwareInformation = getDeviceHardwareInformation(mockServer);
        final var supportedDeviceFamilies = getSupportedDeviceFamilies(mockServer);

        return new MockResponse(SearchResponseBody.of(hpai, deviceHardwareInformation, supportedDeviceFamilies));
    }
}
