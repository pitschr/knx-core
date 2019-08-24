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

package li.pitschmann.knx.daemon.v1.gson;

import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.SearchRequestBody;
import li.pitschmann.knx.link.body.SearchResponseBody;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.communication.KnxStatistic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test {@link KnxStatisticJsonSerializer}
 */
public class KnxStatisticJsonTest {

    /**
     * Test serialization of empty {@link KnxStatistic} to a string representation
     */
    @Test
    @DisplayName("Serialize empty KnxStatistic")
    public void testSerialize() {
        // serialize empty knx statistic to JSON String
        final var knxStatistic = mockKnxStatistic(0);

        // @formatter:off
        final var expectedJson = "" +
                "{" +
                    "\"inbound\":{" +
                        "\"total\":{\"packets\":0,\"bytes\":0}," +
                        "\"search\":{\"request\":0,\"response\":0}," +
                        "\"description\":{\"request\":0,\"response\":0}," +
                        "\"connect\":{\"request\":0,\"response\":0}," +
                        "\"connection_state\":{\"request\":0,\"response\":0}," +
                        "\"disconnect\":{\"request\":0,\"response\":0}," +
                        "\"tunneling\":{\"request\":0,\"response\":0}" +
                    "}," +
                    "\"outbound\":{" +
                        "\"total\":{\"packets\":0,\"bytes\":0}," +
                        "\"search\":{\"request\":0,\"response\":0}," +
                        "\"description\":{\"request\":0,\"response\":0}," +
                        "\"connect\":{\"request\":0,\"response\":0}," +
                        "\"connection_state\":{\"request\":0,\"response\":0}," +
                        "\"disconnect\":{\"request\":0,\"response\":0}," +
                        "\"tunneling\":{\"request\":0,\"response\":0}" +
                    "}," +
                    "\"error\":{" +
                        "\"total\":{\"packets\":0,\"rate\":0.0}" +
                    "}" +
                "}";
        // @formatter:on
        final var json = KnxStatisticJsonSerializer.INSTANCE.serialize(knxStatistic, null, null).toString();

        // verify
        assertThat(json).isEqualTo(expectedJson);
    }

    /**
     * Test serialization of non-empty {@link KnxStatistic} to a string representation
     */
    @Test
    @DisplayName("Serialize non-empty KnxStatistic")
    public void testSerializeNonEmpty() {
        // serialize empty knx statistic to JSON String
        final var knxStatistic = mockKnxStatistic(13);

        // @formatter:off
        final var expectedJson = "" +
                "{" +
                    "\"inbound\":{" +
                        "\"total\":{\"packets\":923,\"bytes\":1027}," +
                        "\"search\":{\"request\":13,\"response\":39}," +
                        "\"description\":{\"request\":65,\"response\":91}," +
                        "\"connect\":{\"request\":117,\"response\":143}," +
                        "\"connection_state\":{\"request\":169,\"response\":195}," +
                        "\"disconnect\":{\"request\":221,\"response\":247}," +
                        "\"tunneling\":{\"request\":273,\"response\":299}" +
                    "}," +
                    "\"outbound\":{" +
                        "\"total\":{\"packets\":949,\"bytes\":1079}," +
                        "\"search\":{\"request\":26,\"response\":52}," +
                        "\"description\":{\"request\":78,\"response\":104}," +
                        "\"connect\":{\"request\":130,\"response\":156}," +
                        "\"connection_state\":{\"request\":182,\"response\":208}," +
                        "\"disconnect\":{\"request\":234,\"response\":260}," +
                        "\"tunneling\":{\"request\":286,\"response\":312}" +
                    "}," +
                    "\"error\":{" +
                        "\"total\":{\"packets\":1157,\"rate\":12.61}" +
                    "}" +
                "}";
        // @formatter:on
        final var json = KnxStatisticJsonSerializer.INSTANCE.serialize(knxStatistic, null, null).toString();

        // verify
        assertThat(json).isEqualTo(expectedJson);
    }

    /**
     * Creates a mocked {@link KnxStatistic} whereas the number of packets/bytes and errors
     * are faked with {@code multiplier}
     *
     * @param multiplier
     * @return mocked {@link KnxStatistic}
     */
    private final KnxStatistic mockKnxStatistic(final long multiplier) {
        final var knxStatistic = mock(KnxStatistic.class);

        // number of packets per body class
        final var bodies = Arrays.asList(
                SearchRequestBody.class, SearchResponseBody.class, // 1*multiplier, 2*multiplier
                DescriptionRequestBody.class, DescriptionResponseBody.class, // 3*multiplier, ...
                ConnectRequestBody.class, ConnectResponseBody.class, //
                ConnectionStateRequestBody.class, ConnectionStateResponseBody.class, //
                DisconnectRequestBody.class, DisconnectResponseBody.class, //
                TunnelingRequestBody.class, TunnelingAckBody.class //
        );
        long i = 0;
        for (var body : bodies) {
            when(knxStatistic.getNumberOfBodyReceived(body)).thenReturn(++i * multiplier);
            when(knxStatistic.getNumberOfBodySent(body)).thenReturn(++i * multiplier);
        }

        // total packets
        when(knxStatistic.getNumberOfBodyReceived()).thenReturn(71 * multiplier);
        when(knxStatistic.getNumberOfBodySent()).thenReturn(73 * multiplier);
        // total bytes
        when(knxStatistic.getNumberOfBytesReceived()).thenReturn(79 * multiplier);
        when(knxStatistic.getNumberOfBytesSent()).thenReturn(83 * multiplier);
        // errors
        when(knxStatistic.getNumberOfErrors()).thenReturn(89 * multiplier);
        when(knxStatistic.getErrorRate()).thenReturn((97 * multiplier) / 100d);

        return knxStatistic;
    }
}
