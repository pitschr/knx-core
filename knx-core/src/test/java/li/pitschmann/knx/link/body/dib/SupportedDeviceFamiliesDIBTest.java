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

import li.pitschmann.knx.link.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link SupportedDeviceFamiliesDIB}
 *
 * @author PITSCHR
 */
public final class SupportedDeviceFamiliesDIBTest {
    private static final byte[] BYTES = new byte[]{ //
            0x0a, // Structure Length
            0x02, // Description Type Code
            0x02, 0x01, // Service Family ID + Version #1
            0x03, 0x02, // Service Family ID + Version #2
            0x04, 0x01, // Service Family ID + Version #3
            0x05, 0x03 // Service Family ID + Version #4
    };

    /**
     * Tests {@link SupportedDeviceFamiliesDIB#valueOf(byte[])}
     *
     * <pre>
     * 	DIB: SUPP_SVC_FAMILIES
     * 	    Structure Length: 10 octets
     * 	    Description Type: SUPP_SVC_FAMILIES (0x02)
     * 	    Service ID: KNX/IP Core (0x02)
     * 	        Version: 1
     * 	    Service ID: KNX device Management (0x03)
     * 	        Version: 2
     * 	    Service ID: KNX/IP Tunneling (0x04)
     * 	        Version: 1
     * 	    Service ID: KNX/IP Routing (0x05)
     * 	        Version: 3
     * </pre>
     */
    @Test
    public void valueOf() {
        // valueOf
        final var supportedDevicesFamiliesByValueOf = SupportedDeviceFamiliesDIB.valueOf(BYTES);

        // compare
        assertThat(supportedDevicesFamiliesByValueOf.getLength()).isEqualTo(10);
        assertThat(supportedDevicesFamiliesByValueOf.getDescriptionType()).isEqualTo(DescriptionType.SUPPORTED_SERVICE_FAMILIES);

        // verify service type family & version
        final var serviceFamilyVersions = supportedDevicesFamiliesByValueOf.getServiceFamilies();
        assertThat(serviceFamilyVersions).hasSize(4);

        final var svcCore = serviceFamilyVersions.get(0);
        assertThat(svcCore.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(svcCore.getVersion()).isEqualTo(1);

        final var svcDeviceManagement = serviceFamilyVersions.get(1);
        assertThat(svcDeviceManagement.getFamily()).isEqualTo(ServiceTypeFamily.DEVICE_MANAGEMENT);
        assertThat(svcDeviceManagement.getVersion()).isEqualTo(2);

        final var svcTunneling = serviceFamilyVersions.get(2);
        assertThat(svcTunneling.getFamily()).isEqualTo(ServiceTypeFamily.TUNNELING);
        assertThat(svcTunneling.getVersion()).isEqualTo(1);

        final var svcRouting = serviceFamilyVersions.get(3);
        assertThat(svcRouting.getFamily()).isEqualTo(ServiceTypeFamily.ROUTING);
        assertThat(svcRouting.getVersion()).isEqualTo(3);
    }

    /**
     * Tests {@link SupportedDeviceFamiliesDIB} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null check
        assertThatThrownBy(() -> SupportedDeviceFamiliesDIB.valueOf(null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("rawData");

        // specific for supported device families DIB
        assertThatThrownBy(() -> SupportedDeviceFamiliesDIB.valueOf(new byte[]{0x03, 0x00, 0x00})).isInstanceOf(KnxIllegalArgumentException.class)
                .hasMessageContaining("divisible by two");

        // incorrect size of bytes
        assertThatThrownBy(() -> SupportedDeviceFamiliesDIB.valueOf(new byte[]{0x01})).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> SupportedDeviceFamiliesDIB.valueOf(Bytes.padRight(new byte[]{(byte) 0xFF}, (byte) 0x00, 255)))
                .isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("rawData");
    }

    /**
     * Test {@link SupportedDeviceFamiliesDIB#toString()} and {@link SupportedDeviceFamiliesDIB#toString(boolean)}
     */
    @Test
    public void testToString() {
        final var supportedDevicesFamiliesDIB = SupportedDeviceFamiliesDIB.valueOf(BYTES);

        final var familyVersions = new ArrayList<ServiceTypeFamilyVersion>();
        familyVersions.add(new ServiceTypeFamilyVersion(ServiceTypeFamily.CORE, 1));
        familyVersions.add(new ServiceTypeFamilyVersion(ServiceTypeFamily.DEVICE_MANAGEMENT, 2));
        familyVersions.add(new ServiceTypeFamilyVersion(ServiceTypeFamily.TUNNELING, 1));
        familyVersions.add(new ServiceTypeFamilyVersion(ServiceTypeFamily.ROUTING, 3));

        assertThat(supportedDevicesFamiliesDIB)
                .hasToString(String.format("SupportedDeviceFamiliesDIB{length=10 (0x0A), descriptionType=%s, serviceFamilies=%s, rawData=%s}",
                        DescriptionType.SUPPORTED_SERVICE_FAMILIES, familyVersions, ByteFormatter.formatHexAsString(BYTES)));
    }
}
