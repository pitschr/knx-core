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

import li.pitschmann.knx.core.header.ServiceType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link BytesBody}
 *
 * @author PITSCHR
 */
class MockBodyTest {

    @Test
    @DisplayName("Test valid cases for #of(ServiceType, byte[])")
    public void validCases() {
        final var body = BytesBody.of(ServiceType.SEARCH_RESPONSE, new byte[]{0x44, 0x55, 0x66});

        assertThat(body.getServiceType()).isEqualTo(ServiceType.SEARCH_RESPONSE);
        assertThat(body.toByteArray()).containsExactly(0x44, 0x55, 0x66);

        // test toString
        assertThat(body).hasToString(
                String.format("BytesBody{serviceType=%s, bytes=0x44 55 66}", ServiceType.SEARCH_RESPONSE)
        );
    }

    @Test
    @DisplayName("Test invalid cases for #of(ServiceType, byte[])")
    void invalidCases() {
        // null
        assertThatThrownBy(() -> BytesBody.of(null, new byte[0]))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Service type is required.");
        assertThatThrownBy(() -> BytesBody.of(ServiceType.CONNECTION_STATE_RESPONSE, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Bytes is required.");

        // length of byte array is out of range
        assertThatThrownBy(() -> BytesBody.of(ServiceType.CONNECT_REQUEST, new byte[251]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Length of bytes may not exceed '250' but was: 251");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(BytesBody.class).verify();
    }

}
