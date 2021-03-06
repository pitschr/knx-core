/*
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

import li.pitschmann.knx.core.utils.ByteFormatter;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link IPCurrentConfigDIB}
 *
 * @author PITSCHR
 */
final class IPCurrentConfigDIBTest {

    @Test
    @DisplayName("Test #of(byte[])")
    void testOf_Bytes() {
        final var bytes = new byte[]{ //
                0x14,                                               // Structure Length
                0x04,                                               // Description Type Code
                0x11, 0x22, 0x33, 0x44,                             // Current IP Address
                0x55, 0x66, 0x77, (byte) 0x88,                      // Current Subnet Mask
                (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // Current Default Gateway
                (byte) 0xD1, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, // DHCP Server
                (byte) 0xDD,                                        // IP Capabilities
                (byte) 0x00                                         // (reserved)
        };
        final var dib = IPCurrentConfigDIB.of(bytes);

        // compare
        assertThat(dib.toByteArray()).containsExactly(bytes);
        assertThat(dib).hasToString(
                String.format("IPCurrentConfigDIB{bytes=%s}", ByteFormatter.formatHexAsString(bytes))
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_of_Bytes() {
        // null
        assertThatThrownBy(() -> IPCurrentConfigDIB.of(null))
                .isInstanceOf(NullPointerException.class);

        // incorrect size of bytes
        assertThatThrownBy(() -> IPCurrentConfigDIB.of(new byte[]{0x03, 0x02, 0x01}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected '20' but was: 3");

    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(IPCurrentConfigDIB.class).verify();
    }

}
