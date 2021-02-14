/*
 * KNX Link - A library for KNX Net/IP communication
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

package li.pitschmann.knx.core.cemi;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link AdditionalInfo}
 *
 * @author PITSCHR
 */
final class AdditionalInfoTest {

    @Test
    @DisplayName("Test #empty()")
    void testEmpty() {
        // create
        final var emptyInfo = AdditionalInfo.empty();

        // byte array shall be empty
        assertThat(emptyInfo.toByteArray()).isEmpty();

        // toString
        assertThat(emptyInfo).hasToString("AdditionalInfo{bytes=}");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testOf() {
        // create by bytes
        final var additionalInfo = AdditionalInfo.of(new byte[]{
                0x04, // Structure Length of Additional Info
                0x01, 0x02, 0x03, 0x04 // data
        });

        // compare the byte array of 'create by bytes'
        assertThat(additionalInfo.toByteArray()).containsExactly(0x04, 0x01, 0x02, 0x03, 0x04);

        // toString
        assertThat(additionalInfo).hasToString("AdditionalInfo{bytes=0x04 01 02 03 04}");
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases() {
        // null
        assertThatThrownBy(() -> AdditionalInfo.of(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageStartingWith("Bytes is required.");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(AdditionalInfo.class).verify();
    }
}
