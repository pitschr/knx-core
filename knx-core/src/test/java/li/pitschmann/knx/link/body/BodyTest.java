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

import li.pitschmann.knx.link.header.Header;
import li.pitschmann.knx.test.KnxBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Body}
 */
public class BodyTest {

    /**
     * Tests the results of {@link Body#getRawData(boolean)}
     * <p>
     * When passing {@code true} then byte array of header and body is returned,
     * otherwise only the byte array of body is returned.
     */
    @Test
    @DisplayName("Test #getRawData(Boolean)")
    public void testGetRawData() {
        final var body = KnxBody.TUNNELING_ACK_BODY;

        // false -> should return the byte array of body only
        final var bodyBytes = body.getRawData();
        assertThat(body.getRawData(false)).containsExactly(bodyBytes);

        // true -> should return the byte array of header and body
        final var headerBytes = Header.create(body).getRawData();
        assertThat(body.getRawData(true)).startsWith(headerBytes);
        assertThat(body.getRawData(true)).endsWith(body.getRawData());
        assertThat(body.getRawData(true)).hasSize(headerBytes.length + bodyBytes.length);
    }
}
