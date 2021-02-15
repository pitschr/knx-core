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

/**
 * Verifies {@link ServiceTypeFamily} enum class
 *
 * @author PITSCHR
 */
final class ServiceTypeFamilyVersionTest {

    @Test
    @DisplayName("Test #of(ServiceTypeFamily, int)")
    void testOf() {
        final var serviceTypeFamilyVersion = new ServiceTypeFamilyVersion(ServiceTypeFamily.ROUTING, 13);

        assertThat(serviceTypeFamilyVersion.getFamily()).isSameAs(ServiceTypeFamily.ROUTING);
        assertThat(serviceTypeFamilyVersion.getVersion()).isEqualTo(13);
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        final var serviceTypeFamilyVersion = new ServiceTypeFamilyVersion(ServiceTypeFamily.TUNNELING, 17);

        assertThat(serviceTypeFamilyVersion).hasToString(
                "ServiceTypeFamilyVersion{family=TUNNELING, version=17}"
        );
    }


    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ServiceTypeFamilyVersion.class).verify();
    }
}
