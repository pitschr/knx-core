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
import li.pitschmann.test.KnxBody;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link KnxEventPool}
 *
 * @author PITSCHR
 */
public class KnxEventPoolTest {
    /**
     * Test events:
     * <ol>
     * <li>Description</li>
     * <li>Connect</li>
     * <li>Connection State</li>
     * <li>Disconnect</li>
     * <li>Tunnelling Request & Ack</li>
     * </ol>
     */
    @Test
    public void testEvents() {
        final var pool = new KnxEventPool();

        assertThat(pool.descriptionEvent()).isNotNull().isSameAs(pool.get(KnxBody.DESCRIPTION_REQUEST_BODY));
        assertThat(pool.connectEvent()).isNotNull().isSameAs(pool.get(KnxBody.CONNECT_REQUEST_BODY));
        assertThat(pool.connectionStateEvent()).isNotNull().isSameAs(pool.get(KnxBody.CONNECTION_STATE_REQUEST_BODY));
        assertThat(pool.disconnectEvent()).isNotNull().isSameAs(pool.get(KnxBody.DISCONNECT_REQUEST_BODY));

        assertThat(pool.get(KnxBody.TUNNELLING_REQUEST_BODY)).isNotNull().isSameAs(pool.get(KnxBody.TUNNELLING_ACK_BODY));
        assertThat(pool.get(KnxBody.TUNNELLING_ACK_BODY)).isNotNull().isSameAs(pool.get(KnxBody.TUNNELLING_REQUEST_BODY));
    }

    /**
     * Test {@link KnxEventPool#add(RequestBody)}
     */
    @Test
    public void testAdd() {
        final var pool = new KnxEventPool();
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
     * Test failures for {@link KnxEventPool#add(RequestBody)}
     */
    @Test
    public void testAddFailures() {
        assertThatThrownBy(() -> new KnxEventPool().add(null)).isInstanceOf(IllegalArgumentException.class);

        final var requestBody = mock(RequestBody.class);
        assertThatThrownBy(() -> new KnxEventPool().add(requestBody)).isInstanceOf(IllegalArgumentException.class);
    }
}
