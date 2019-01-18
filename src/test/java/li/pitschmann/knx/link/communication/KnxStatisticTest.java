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

import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.test.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests the {@link DefaultKnxStatistic}
 *
 * @author PITSCHR
 */
public class KnxStatisticTest {
    /**
     * Test at initialization for {@link DefaultKnxStatistic}
     */
    @Test
    @DisplayName("Test at initialization of KNX statistic")
    public void testInitialization() {
        final var knxStatistic = new DefaultKnxStatistic();

        // expected is that all are zero at initialization
        assertThat(knxStatistic.getNumberOfBytesReceived()).isZero();
        assertThat(knxStatistic.getNumberOfBodyReceived()).isZero();
        assertThat(knxStatistic.getNumberOfBodyReceived(DescriptionResponseBody.class)).isZero();

        assertThat(knxStatistic.getNumberOfBytesSent()).isZero();
        assertThat(knxStatistic.getNumberOfBodySent()).isZero();
        assertThat(knxStatistic.getNumberOfBodySent(DescriptionRequestBody.class)).isZero();

        assertThat(knxStatistic.getNumberOfErrors()).isZero();
    }

    /**
     * Test for {@link DefaultKnxStatistic}
     */
    @Test
    @DisplayName("Check for KNX statistic")
    public void testKnxStatistic() {
        final var knxStatistic = new DefaultKnxStatistic();

        // fill KNX statistic
        addIncomingBodies(knxStatistic);
        addOutgoingBodies(knxStatistic);
        addErrors(knxStatistic);

        // pre-check
        assertThat(knxStatistic.getNumberOfBodyReceived()).isEqualTo(55);
        assertThat(knxStatistic.getNumberOfBodyReceived(DescriptionRequestBody.class)).isEqualTo(1);
        assertThat(knxStatistic.getNumberOfBodyReceived(DescriptionResponseBody.class)).isEqualTo(2);
        assertThat(knxStatistic.getNumberOfBodyReceived(ConnectRequestBody.class)).isEqualTo(3);
        assertThat(knxStatistic.getNumberOfBodyReceived(ConnectResponseBody.class)).isEqualTo(4);
        assertThat(knxStatistic.getNumberOfBodyReceived(ConnectionStateRequestBody.class)).isEqualTo(5);
        assertThat(knxStatistic.getNumberOfBodyReceived(ConnectionStateResponseBody.class)).isEqualTo(6);
        assertThat(knxStatistic.getNumberOfBodyReceived(DisconnectRequestBody.class)).isEqualTo(7);
        assertThat(knxStatistic.getNumberOfBodyReceived(DisconnectResponseBody.class)).isEqualTo(8);
        assertThat(knxStatistic.getNumberOfBodyReceived(TunnellingRequestBody.class)).isEqualTo(9);
        assertThat(knxStatistic.getNumberOfBodyReceived(TunnellingAckBody.class)).isEqualTo(10);

        assertThat(knxStatistic.getNumberOfBodySent()).isEqualTo(255);
        assertThat(knxStatistic.getNumberOfBodySent(DescriptionRequestBody.class)).isEqualTo(21);
        assertThat(knxStatistic.getNumberOfBodySent(DescriptionResponseBody.class)).isEqualTo(22);
        assertThat(knxStatistic.getNumberOfBodySent(ConnectRequestBody.class)).isEqualTo(23);
        assertThat(knxStatistic.getNumberOfBodySent(ConnectResponseBody.class)).isEqualTo(24);
        assertThat(knxStatistic.getNumberOfBodySent(ConnectionStateRequestBody.class)).isEqualTo(25);
        assertThat(knxStatistic.getNumberOfBodySent(ConnectionStateResponseBody.class)).isEqualTo(26);
        assertThat(knxStatistic.getNumberOfBodySent(DisconnectRequestBody.class)).isEqualTo(27);
        assertThat(knxStatistic.getNumberOfBodySent(DisconnectResponseBody.class)).isEqualTo(28);
        assertThat(knxStatistic.getNumberOfBodySent(TunnellingRequestBody.class)).isEqualTo(29);
        assertThat(knxStatistic.getNumberOfBodySent(TunnellingAckBody.class)).isEqualTo(30);

        assertThat(knxStatistic.getNumberOfErrors()).isEqualTo(40);
        assertThat(knxStatistic.getErrorRate()).isEqualTo(40d * 100 / (55 + 255));
    }

    /**
     * Test for unmodifiable {@link KnxStatistic}
     */
    @Test
    @DisplayName("Check of unmodifiable KNX statistic")
    public void testUnmodifiableKnxStatistic() {
        final var knxStatistic = new DefaultKnxStatistic();

        // fill KNX statistic
        addIncomingBodies(knxStatistic);
        addOutgoingBodies(knxStatistic);
        addErrors(knxStatistic);

        // assert if statistic is an instance of UnmodifiableKnxStatistic
        final var knxStatisticUnmodifiable = knxStatistic.getUnmodifiableStatistic();
        assertThat(knxStatisticUnmodifiable.getClass().getSimpleName()).isEqualTo("UnmodifiableKnxStatistic");

        // assert received/sent bytes and errors
        assertThat(knxStatisticUnmodifiable.getNumberOfBytesReceived()).isEqualTo(knxStatistic.getNumberOfBytesReceived());
        assertThat(knxStatisticUnmodifiable.getNumberOfBytesSent()).isEqualTo(knxStatistic.getNumberOfBytesSent());
        assertThat(knxStatisticUnmodifiable.getNumberOfErrors()).isEqualTo(knxStatistic.getNumberOfErrors());
        assertThat(knxStatisticUnmodifiable.getErrorRate()).isEqualTo(knxStatistic.getErrorRate());

        // check received bodies
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived()).isEqualTo(knxStatistic.getNumberOfBodyReceived());
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(DescriptionRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(DescriptionRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(DescriptionResponseBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(DescriptionResponseBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(ConnectRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(ConnectRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(ConnectResponseBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(ConnectResponseBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(ConnectionStateRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(ConnectionStateRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(ConnectionStateResponseBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(ConnectionStateResponseBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(DisconnectRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(DisconnectRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(DisconnectResponseBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(DisconnectResponseBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(TunnellingRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(TunnellingRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodyReceived(TunnellingAckBody.class)).isEqualTo(knxStatistic.getNumberOfBodyReceived(TunnellingAckBody.class));

        // check sent bodies
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent()).isEqualTo(knxStatistic.getNumberOfBodySent());
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(DescriptionRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(DescriptionRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(DescriptionResponseBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(DescriptionResponseBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(ConnectRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(ConnectRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(ConnectResponseBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(ConnectResponseBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(ConnectionStateRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(ConnectionStateRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(ConnectionStateResponseBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(ConnectionStateResponseBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(DisconnectRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(DisconnectRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(DisconnectResponseBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(DisconnectResponseBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(TunnellingRequestBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(TunnellingRequestBody.class));
        assertThat(knxStatisticUnmodifiable.getNumberOfBodySent(TunnellingAckBody.class)).isEqualTo(knxStatistic.getNumberOfBodySent(TunnellingAckBody.class));
    }

    /**
     * Test {@link KnxStatistic#getErrorRate()}
     */
    @Test
    @DisplayName("Test the error rate")
    public void testErrorRate() {
        var knxStatistic = Mockito.spy(KnxStatistic.class);

        // error rate = 0% (no communication yet)
        Mockito.when(knxStatistic.getNumberOfBodyReceived()).thenReturn(0L);
        Mockito.when(knxStatistic.getNumberOfBodySent()).thenReturn(0L);
        Mockito.when(knxStatistic.getNumberOfErrors()).thenReturn(0L);
        assertThat(knxStatistic.getErrorRate()).isZero();

        // error rate = 0% (with communication)
        Mockito.when(knxStatistic.getNumberOfBodyReceived()).thenReturn(1L);
        Mockito.when(knxStatistic.getNumberOfBodySent()).thenReturn(0L);
        Mockito.when(knxStatistic.getNumberOfErrors()).thenReturn(0L);
        assertThat(knxStatistic.getErrorRate()).isZero();

        // error rate = 50% (4 received, 2 sent = 6 packets; 3 error packets)
        Mockito.when(knxStatistic.getNumberOfBodyReceived()).thenReturn(4L);
        Mockito.when(knxStatistic.getNumberOfBodySent()).thenReturn(2L);
        Mockito.when(knxStatistic.getNumberOfErrors()).thenReturn(3L);
        assertThat(knxStatistic.getErrorRate()).isEqualTo(50d);
    }

    /**
     * Adds 55 <strong>incoming</strong> bodies to {@link DefaultKnxStatistic}
     * <p/>
     * <ul>
     * <li>1x {@link DescriptionRequestBody}</li>
     * <li>2x {@link DescriptionResponseBody}</li>
     * <li>3x {@link ConnectRequestBody}</li>
     * <li>4x {@link ConnectResponseBody}</li>
     * <li>5x {@link ConnectionStateRequestBody}</li>
     * <li>6x {@link ConnectionStateResponseBody}</li>
     * <li>7x {@link DisconnectRequestBody}</li>
     * <li>8x {@link DisconnectResponseBody}</li>
     * <li>9x {@link TunnellingRequestBody}</li>
     * <li>10x {@link TunnellingAckBody}</li>
     * </ul>
     *
     * @param knxStatistic
     */
    private void addIncomingBodies(final DefaultKnxStatistic knxStatistic) {
        final var bodies = new LinkedList<Body>();

        bodies.addAll(generateBodyList(KnxBody.DESCRIPTION_REQUEST_BODY, 1));
        bodies.addAll(generateBodyList(KnxBody.DESCRIPTION_RESPONSE_BODY, 2));
        bodies.addAll(generateBodyList(KnxBody.CONNECT_REQUEST_BODY, 3));
        bodies.addAll(generateBodyList(KnxBody.CONNECT_RESPONSE_BODY, 4));
        bodies.addAll(generateBodyList(KnxBody.CONNECTION_STATE_REQUEST_BODY, 5));
        bodies.addAll(generateBodyList(KnxBody.CONNECTION_STATE_RESPONSE_BODY, 6));
        bodies.addAll(generateBodyList(KnxBody.DISCONNECT_REQUEST_BODY, 7));
        bodies.addAll(generateBodyList(KnxBody.DISCONNECT_RESPONSE_BODY, 8));
        bodies.addAll(generateBodyList(KnxBody.TUNNELLING_REQUEST_BODY, 9));
        bodies.addAll(generateBodyList(KnxBody.TUNNELLING_ACK_BODY, 10));

        bodies.stream().forEach(knxStatistic::onIncomingBody);
    }

    /**
     * Adds 155 <strong>outgoing</strong> bodies to {@link DefaultKnxStatistic}
     * <p/>
     * <ul>
     * <li>21x {@link DescriptionRequestBody}</li>
     * <li>22x {@link DescriptionResponseBody}</li>
     * <li>23x {@link ConnectRequestBody}</li>
     * <li>24x {@link ConnectResponseBody}</li>
     * <li>25x {@link ConnectionStateRequestBody}</li>
     * <li>26x {@link ConnectionStateResponseBody}</li>
     * <li>27x {@link DisconnectRequestBody}</li>
     * <li>28x {@link DisconnectResponseBody}</li>
     * <li>29x {@link TunnellingRequestBody}</li>
     * <li>30x {@link TunnellingAckBody}</li>
     * </ul>
     *
     * @param knxStatistic
     */
    private void addOutgoingBodies(final DefaultKnxStatistic knxStatistic) {
        final var bodies = new LinkedList<Body>();

        bodies.addAll(generateBodyList(KnxBody.DESCRIPTION_REQUEST_BODY, 21));
        bodies.addAll(generateBodyList(KnxBody.DESCRIPTION_RESPONSE_BODY, 22));
        bodies.addAll(generateBodyList(KnxBody.CONNECT_REQUEST_BODY, 23));
        bodies.addAll(generateBodyList(KnxBody.CONNECT_RESPONSE_BODY, 24));
        bodies.addAll(generateBodyList(KnxBody.CONNECTION_STATE_REQUEST_BODY, 25));
        bodies.addAll(generateBodyList(KnxBody.CONNECTION_STATE_RESPONSE_BODY, 26));
        bodies.addAll(generateBodyList(KnxBody.DISCONNECT_REQUEST_BODY, 27));
        bodies.addAll(generateBodyList(KnxBody.DISCONNECT_RESPONSE_BODY, 28));
        bodies.addAll(generateBodyList(KnxBody.TUNNELLING_REQUEST_BODY, 29));
        bodies.addAll(generateBodyList(KnxBody.TUNNELLING_ACK_BODY, 30));

        bodies.stream().forEach(knxStatistic::onOutgoingBody);
    }

    private List<Body> generateBodyList(final Body body, final int occurrences) {
        final var list = new ArrayList<Body>(occurrences);
        for (int i = 0; i < occurrences; i++) {
            list.add(body);
        }
        return list;
    }

    /**
     * Adds 40 <strong>errors</strong> to {@link DefaultKnxStatistic}
     * <p/>
     * <ul>
     * <li>8x {@link IllegalArgumentException}</li>
     * <li>8x {@link IOException}</li>
     * <li>8x {@link KnxException}</li>
     * <li>8x {@link NullPointerException}</li>
     * <li>8x {@link Throwable}</li>
     * </ul>
     *
     * @param knxStatistic
     */
    private void addErrors(final DefaultKnxStatistic knxStatistic) {
        for (int i = 0; i < 8; i++) {
            knxStatistic.onError(new IllegalArgumentException());
        }
        for (int i = 0; i < 8; i++) {
            knxStatistic.onError(new IOException());
        }
        for (int i = 0; i < 8; i++) {
            knxStatistic.onError(new KnxException(null));
        }
        for (int i = 0; i < 8; i++) {
            knxStatistic.onError(new NullPointerException());
        }
        for (int i = 0; i < 8; i++) {
            knxStatistic.onError(new Throwable());
        }
    }
}
