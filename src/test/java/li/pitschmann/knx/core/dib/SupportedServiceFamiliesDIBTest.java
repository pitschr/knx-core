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

package li.pitschmann.knx.core.dib;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link SupportedServiceFamiliesDIB}
 *
 * @author PITSCHR
 */
final class SupportedServiceFamiliesDIBTest {
    /**
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
    @DisplayName("Test #of(byte[]) with: Core, Device management, Tunneling and Routing")
    void testOf_ServiceFamilyVersions() {
        final var bytes = new byte[]{
                0x0A,       // Structure Length
                0x02,       // Description Type Code
                0x02, 0x01, // Service Family ID + Version #1
                0x03, 0x02, // Service Family ID + Version #2
                0x04, 0x01, // Service Family ID + Version #1
                0x05, 0x03  // Service Family ID + Version #3
        };
        final var dibByBytes = SupportedServiceFamiliesDIB.of(bytes);

        // verify service type family & version
        final var serviceFamilyVersions = dibByBytes.getServiceFamilies();
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

        assertThat(dibByBytes.toByteArray()).containsExactly(bytes);

        assertThat(dibByBytes).hasToString(
                String.format("SupportedServiceFamiliesDIB{serviceFamilies=%s}", serviceFamilyVersions)
        );

    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_of_Bytes() {
        // null
        assertThatThrownBy(() -> SupportedServiceFamiliesDIB.of(null))
                .isInstanceOf(NullPointerException.class);

        // specific for supported service families DIB
        assertThatThrownBy(() -> SupportedServiceFamiliesDIB.of(new byte[]{0x03, 0x00, 0x00}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Length must be divisible by 2, but was: 3");

        // incorrect size of bytes
        assertThatThrownBy(() -> SupportedServiceFamiliesDIB.of(new byte[]{0x01}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [2..254] but was: 1");
        assertThatThrownBy(() -> SupportedServiceFamiliesDIB.of(new byte[256]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [2..254] but was: 256");
    }

    @Test
    @DisplayName("Test #hasServiceTypeFamily(ServiceTypeFamily)")
    void testServiceTypeFamily() {
        final var dib = SupportedServiceFamiliesDIB.of(
                new byte[]{ //
                        0x06,       // Structure Length
                        0x02,       // Description Type Code
                        0x02, 0x01, // Service Family ID + Version #1
                        0x04, 0x01, // Service Family ID + Version #1
                }
        );

        assertThat(dib.hasServiceTypeFamily(ServiceTypeFamily.CORE)).isTrue();
        assertThat(dib.hasServiceTypeFamily(ServiceTypeFamily.DEVICE_MANAGEMENT)).isFalse();
        assertThat(dib.hasServiceTypeFamily(ServiceTypeFamily.TUNNELING)).isTrue();
        assertThat(dib.hasServiceTypeFamily(ServiceTypeFamily.ROUTING)).isFalse();
        assertThat(dib.hasServiceTypeFamily(ServiceTypeFamily.OBJECT_SERVER)).isFalse();
        assertThat(dib.hasServiceTypeFamily(ServiceTypeFamily.REMOTE_LOGGING)).isFalse();
        assertThat(dib.hasServiceTypeFamily(ServiceTypeFamily.REMOTE_CONFIGURATION_AND_DIAGNOSIS)).isFalse();
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(SupportedServiceFamiliesDIB.class).verify();
    }

}
