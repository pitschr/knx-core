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

package li.pitschmann.knx.core.communication;

import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.test.KnxBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link InternalKnxEventPool}
 *
 * @author PITSCHR
 */
class InternalKnxEventPoolTest {
    /**
     * Test events:
     * <ol>
     * <li>Description</li>
     * <li>Connect</li>
     * <li>Connection State</li>
     * <li>Disconnect</li>
     * <li>Tunneling Request & Ack</li>
     * </ol>
     */
    @Test
    @DisplayName("Test all supported events")
    void testEvents() {
        final var pool = new InternalKnxEventPool();

        assertThat(pool.searchEvent()).isNotNull().isSameAs(pool.get(KnxBody.SEARCH_REQUEST_BODY));
        assertThat(pool.descriptionEvent()).isNotNull().isSameAs(pool.get(KnxBody.DESCRIPTION_REQUEST_BODY));
        assertThat(pool.connectEvent()).isNotNull().isSameAs(pool.get(KnxBody.CONNECT_REQUEST_BODY));
        assertThat(pool.connectionStateEvent()).isNotNull().isSameAs(pool.get(KnxBody.CONNECTION_STATE_REQUEST_BODY));
        assertThat(pool.disconnectEvent()).isNotNull().isSameAs(pool.get(KnxBody.DISCONNECT_REQUEST_BODY));

        assertThat(pool.get(KnxBody.TUNNELING_REQUEST_BODY)).isNotNull().isSameAs(pool.get(KnxBody.TUNNELING_ACK_BODY));
        assertThat(pool.get(KnxBody.TUNNELING_ACK_BODY)).isNotNull().isSameAs(pool.get(KnxBody.TUNNELING_REQUEST_BODY));
    }

    @Test
    @DisplayName("Unknown request type for #get(RequestBody)")
    void testUnknownEvent() {
        final var pool = new InternalKnxEventPool();
        final var requestMock = mock(RequestBody.class);
        when(requestMock.toString()).thenReturn("DummyRequestBody");

        assertThatThrownBy(() -> pool.get(requestMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request body is not supported: DummyRequestBody");
    }
}
