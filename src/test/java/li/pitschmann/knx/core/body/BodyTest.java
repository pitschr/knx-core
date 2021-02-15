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

import li.pitschmann.knx.core.header.Header;
import li.pitschmann.knx.core.test.KnxBody;
import li.pitschmann.knx.core.utils.Bytes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Body}
 */
class BodyTest {

    @Test
    @DisplayName("Test #toByteArray(boolean)")
    void testToByteArray_with_boolean() {
        final var body = KnxBody.TUNNELING_ACK_BODY;

        // false -> should return the byte array of body only
        final var bodyBytes = body.toByteArray();
        assertThat(body.toByteArray(false)).containsExactly(bodyBytes);

        // true -> should return the byte array of header and body
        final var headerBytes = Header.of(body).toByteArray();
        assertThat(body.toByteArray(true)).startsWith(headerBytes);
        assertThat(body.toByteArray(true)).endsWith(body.toByteArray());
        assertThat(body.toByteArray(true)).hasSize(headerBytes.length + bodyBytes.length);
        assertThat(body.toByteArray(true)).containsExactly(Bytes.concat(headerBytes, bodyBytes));
    }
}
