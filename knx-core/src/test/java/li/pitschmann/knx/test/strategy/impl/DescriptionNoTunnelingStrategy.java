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

package li.pitschmann.knx.test.strategy.impl;

import li.pitschmann.knx.link.body.dib.SupportedDeviceFamiliesDIB;
import li.pitschmann.knx.test.MockServer;
import li.pitschmann.knx.test.strategy.DescriptionStrategy;

/**
 * No Tunneling Service Family for {@link DescriptionStrategy}
 */
public class DescriptionNoTunnelingStrategy extends DefaultDescriptionStrategy {
    private static SupportedDeviceFamiliesDIB DEFAULT_SUPPORTED_DEVICE_FAMILIES;

    static {
        DEFAULT_SUPPORTED_DEVICE_FAMILIES = SupportedDeviceFamiliesDIB.valueOf(new byte[]{ //
                0x08, // Structure Length
                0x02, // Description Type Code
                0x02, 0x01, // Service Family ID (Core) + Version #1
                0x03, 0x02, // Service Family ID (Device Management)+ Version #2
                0x05, 0x03 // Service Family ID (Routing) + Version #4
        });
    }

    /**
     * Returns the supported device families without TUNNELING feature.
     *
     * @param mockServer
     * @return DIB supported device families
     */
    protected SupportedDeviceFamiliesDIB getSupportedDeviceFamilies(final MockServer mockServer) {
        return DEFAULT_SUPPORTED_DEVICE_FAMILIES;
    }
}
