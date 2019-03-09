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

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.body.TunnellingRequestBody;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.test.KnxBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link KnxStatisticImpl}
 *
 * @author PITSCHR
 */
public class KnxStatisticTest {
    /**
     * Test at initialization for {@link KnxStatisticImpl}
     */
    @Test
    @DisplayName("Test at initialization of KNX statistic")
    public void testInitialization() {
        final var knxStatistic = new KnxStatisticImpl();

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
     * Test for {@link KnxStatisticImpl}
     */
    @Test
    @DisplayName("Check for KNX statistic")
    public void testKnxStatistic() {
        final var knxStatistic = new KnxStatisticImpl();

        // fill KNX statistic
        addIncomingBodies(knxStatistic);
        addOutgoingBodies(knxStatistic);
        addErrors(knxStatistic);

        // verify
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
    @DisplayName("Test unmodifiable KNX statistic")
    public void testUnmodifiableKnxStatistic() {
        final var statistic = new KnxStatisticImpl();

        // fill KNX statistic
        addIncomingBodies(statistic);
        addOutgoingBodies(statistic);
        addErrors(statistic);

        // assert if statistic is an instance of UnmodifiableKnxStatistic
        final var unmodifiableStatistic = statistic.asUnmodifiable();
        assertThat(unmodifiableStatistic.getClass().getSimpleName()).isEqualTo("UnmodifiableKnxStatistic");

        // assert received/sent bytes and errors
        assertThat(unmodifiableStatistic.getNumberOfBytesReceived()).isEqualTo(statistic.getNumberOfBytesReceived());
        assertThat(unmodifiableStatistic.getNumberOfBytesSent()).isEqualTo(statistic.getNumberOfBytesSent());
        assertThat(unmodifiableStatistic.getNumberOfErrors()).isEqualTo(statistic.getNumberOfErrors());
        assertThat(unmodifiableStatistic.getErrorRate()).isEqualTo(statistic.getErrorRate());

        // check received bodies
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived()).isEqualTo(statistic.getNumberOfBodyReceived());
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(DescriptionRequestBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(DescriptionRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(DescriptionResponseBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(DescriptionResponseBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(ConnectRequestBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(ConnectRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(ConnectResponseBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(ConnectResponseBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(ConnectionStateRequestBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(ConnectionStateRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(ConnectionStateResponseBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(ConnectionStateResponseBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(DisconnectRequestBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(DisconnectRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(DisconnectResponseBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(DisconnectResponseBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(TunnellingRequestBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(TunnellingRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodyReceived(TunnellingAckBody.class)).isEqualTo(statistic.getNumberOfBodyReceived(TunnellingAckBody.class));

        // check sent bodies
        assertThat(unmodifiableStatistic.getNumberOfBodySent()).isEqualTo(statistic.getNumberOfBodySent());
        assertThat(unmodifiableStatistic.getNumberOfBodySent(DescriptionRequestBody.class)).isEqualTo(statistic.getNumberOfBodySent(DescriptionRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodySent(DescriptionResponseBody.class)).isEqualTo(statistic.getNumberOfBodySent(DescriptionResponseBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodySent(ConnectRequestBody.class)).isEqualTo(statistic.getNumberOfBodySent(ConnectRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodySent(ConnectResponseBody.class)).isEqualTo(statistic.getNumberOfBodySent(ConnectResponseBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodySent(ConnectionStateRequestBody.class)).isEqualTo(statistic.getNumberOfBodySent(ConnectionStateRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodySent(ConnectionStateResponseBody.class)).isEqualTo(statistic.getNumberOfBodySent(ConnectionStateResponseBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodySent(DisconnectRequestBody.class)).isEqualTo(statistic.getNumberOfBodySent(DisconnectRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodySent(DisconnectResponseBody.class)).isEqualTo(statistic.getNumberOfBodySent(DisconnectResponseBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodySent(TunnellingRequestBody.class)).isEqualTo(statistic.getNumberOfBodySent(TunnellingRequestBody.class));
        assertThat(unmodifiableStatistic.getNumberOfBodySent(TunnellingAckBody.class)).isEqualTo(statistic.getNumberOfBodySent(TunnellingAckBody.class));
    }

    /**
     * Test {@link KnxStatistic#getErrorRate()}
     */
    @Test
    @DisplayName("Test the error rate")
    public void testErrorRate() {
        final var knxStatistic = spy(KnxStatistic.class);

        // error rate = 0% (no communication yet)
        when(knxStatistic.getNumberOfBodyReceived()).thenReturn(0L);
        when(knxStatistic.getNumberOfBodySent()).thenReturn(0L);
        when(knxStatistic.getNumberOfErrors()).thenReturn(0L);
        assertThat(knxStatistic.getErrorRate()).isZero();

        // error rate = 0% (with communication)
        when(knxStatistic.getNumberOfBodyReceived()).thenReturn(1L);
        when(knxStatistic.getNumberOfBodySent()).thenReturn(0L);
        when(knxStatistic.getNumberOfErrors()).thenReturn(0L);
        assertThat(knxStatistic.getErrorRate()).isZero();

        // error rate = 50% (4 received, 2 sent = 6 packets; 3 error packets)
        when(knxStatistic.getNumberOfBodyReceived()).thenReturn(4L);
        when(knxStatistic.getNumberOfBodySent()).thenReturn(2L);
        when(knxStatistic.getNumberOfErrors()).thenReturn(3L);
        assertThat(knxStatistic.getErrorRate()).isEqualTo(50d);
    }

    /**
     * Adds 55 <strong>incoming</strong> bodies to {@link KnxStatisticImpl}
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
    private void addIncomingBodies(final KnxStatisticImpl knxStatistic) {
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
     * Adds 155 <strong>outgoing</strong> bodies to {@link KnxStatisticImpl}
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
    private void addOutgoingBodies(final KnxStatisticImpl knxStatistic) {
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
        for (var i = 0; i < occurrences; i++) {
            list.add(body);
        }
        return list;
    }

    /**
     * Adds 40 <strong>errors</strong> to {@link KnxStatisticImpl}
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
    private void addErrors(final KnxStatisticImpl knxStatistic) {
        for (var i = 0; i < 8; i++) {
            knxStatistic.onError(new IllegalArgumentException());
        }
        for (var i = 0; i < 8; i++) {
            knxStatistic.onError(new IOException());
        }
        for (var i = 0; i < 8; i++) {
            knxStatistic.onError(new KnxException(null));
        }
        for (var i = 0; i < 8; i++) {
            knxStatistic.onError(new NullPointerException());
        }
        for (var i = 0; i < 8; i++) {
            knxStatistic.onError(new Throwable());
        }
    }
}
