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

package li.pitschmann.knx.link.communication;

import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.test.KnxBody;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link InternalKnxEventPool}
 *
 * @author PITSCHR
 */
public class InternalKnxEventPoolTest {
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
    public void testEvents() {
        final var pool = new InternalKnxEventPool();

        assertThat(pool.searchEvent()).isNotNull().isSameAs(pool.get(KnxBody.SEARCH_REQUEST_BODY));
        assertThat(pool.descriptionEvent()).isNotNull().isSameAs(pool.get(KnxBody.DESCRIPTION_REQUEST_BODY));
        assertThat(pool.connectEvent()).isNotNull().isSameAs(pool.get(KnxBody.CONNECT_REQUEST_BODY));
        assertThat(pool.connectionStateEvent()).isNotNull().isSameAs(pool.get(KnxBody.CONNECTION_STATE_REQUEST_BODY));
        assertThat(pool.disconnectEvent()).isNotNull().isSameAs(pool.get(KnxBody.DISCONNECT_REQUEST_BODY));

        assertThat(pool.get(KnxBody.TUNNELING_REQUEST_BODY)).isNotNull().isSameAs(pool.get(KnxBody.TUNNELING_ACK_BODY));
        assertThat(pool.get(KnxBody.TUNNELING_ACK_BODY)).isNotNull().isSameAs(pool.get(KnxBody.TUNNELING_REQUEST_BODY));
    }

    /**
     * Test {@link InternalKnxEventPool#add(RequestBody)}
     */
    @Test
    public void testAdd() {
        final var pool = new InternalKnxEventPool();
        final var eventData = pool.descriptionEvent();

        assertThat(eventData.getRequest()).isNull();
        assertThat(eventData.getRequestTime()).isNull();

        final var instantBeforeAdd = Instant.now();
        pool.add(KnxBody.DESCRIPTION_REQUEST_BODY);
        final var instantAfterAdd = Instant.now();

        assertThat(eventData.getRequest()).isSameAs(KnxBody.DESCRIPTION_REQUEST_BODY);
        assertThat(eventData.getRequestTime()).isBetween(instantBeforeAdd, instantAfterAdd);
    }

    /**
     * Test failures for {@link InternalKnxEventPool#add(RequestBody)}
     */
    @Test
    public void testAddFailures() {
        assertThatThrownBy(() -> new InternalKnxEventPool().add(null)).isInstanceOf(IllegalArgumentException.class);

        final var requestBody = mock(RequestBody.class);
        assertThatThrownBy(() -> new InternalKnxEventPool().add(requestBody)).isInstanceOf(IllegalArgumentException.class);
    }
}
