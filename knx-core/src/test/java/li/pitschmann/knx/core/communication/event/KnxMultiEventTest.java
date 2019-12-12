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

package li.pitschmann.knx.core.communication.event;

import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.test.KnxBody;
import li.pitschmann.knx.core.utils.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the {@link KnxMultiEvent}
 *
 * @author PITSCHR
 */
public class KnxMultiEventTest {
    /**
     * Tests the initialization of {@link KnxMultiEvent}
     */
    @Test
    @DisplayName("Check of initialization values for multi event")
    public void testInit() {
        final var event = new KnxMultiEvent<>();

        assertThat(event.hasRequest()).isFalse();
        assertThat(event.getRequest()).isNull();
        assertThat(event.getRequestTime()).isNull();

        assertThat(event.hasResponse()).isFalse();
        assertThat(event.getResponse()).isNull();
        assertThat(event.getResponseTime()).isNull();
    }

    /**
     * Tests {@link KnxMultiEvent#getRequest()} and {@link KnxMultiEvent#getRequestTime()}
     */
    @Test
    @DisplayName("Check set of request for multi event")
    public void testRequest() {
        final var event = new KnxMultiEvent<>();

        final var instantBeforeRequest = Instant.now();
        event.setRequest(KnxBody.TUNNELING_REQUEST_BODY);
        final var instantAfterRequest = Instant.now();

        assertThat(event.hasRequest()).isTrue();
        assertThat(event.getRequest()).isNotNull();
        assertThat(event.getRequestTime()).isBetween(instantBeforeRequest, instantAfterRequest);

        assertThat(event.hasResponse()).isFalse();
        assertThat(event.getResponse()).isNull();
        assertThat(event.getResponseTime()).isNull();
    }

    /**
     * Tests {@link KnxMultiEvent#getResponse()} and {@link KnxMultiEvent#getResponseTime()}
     */
    @Test
    @DisplayName("Check two responses for KNX multi event")
    public void testResponse() {
        final var event = new KnxMultiEvent<TunnelingRequestBody, TunnelingAckBody>();

        // first response
        final var instantBeforeResponse = Instant.now();
        event.setResponse(KnxBody.TUNNELING_ACK_BODY);
        final var instantAfterResponse = Instant.now();

        Sleeper.milliseconds(100); // wait bit

        // second response
        final var instantBeforeResponse2 = Instant.now();
        event.setResponse(KnxBody.TUNNELING_ACK_BODY_2);
        final var instantAfterResponse2 = Instant.now();

        assertThat(event.hasRequest()).isFalse();
        assertThat(event.getRequest()).isNull();
        assertThat(event.getRequestTime()).isNull();
        assertThat(event.hasResponse()).isTrue();
        assertThat(event.getResponse()).isSameAs(KnxBody.TUNNELING_ACK_BODY); // first
        assertThat(event.getResponse(0)).isSameAs(KnxBody.TUNNELING_ACK_BODY);
        assertThat(event.getResponse(1)).isSameAs(KnxBody.TUNNELING_ACK_BODY_2);
        assertThat(event.getResponseTime()).isBetween(instantBeforeResponse, instantAfterResponse);
        assertThat(event.getResponseTime(0)).isBetween(instantBeforeResponse, instantAfterResponse);
        assertThat(event.getResponseTime(1)).isBetween(instantBeforeResponse2, instantAfterResponse2);

        // try to get the first ack (seq=27)
        assertThat(event.getResponse((res) -> res.getSequence() == 27)).isSameAs(KnxBody.TUNNELING_ACK_BODY);
        assertThat(event.getResponseTime((res) -> res.getSequence() == 27)).isBetween(instantBeforeResponse, instantAfterResponse);
        final var responseEvent1 = event.getResponseEvent((res) -> res.getSequence() == 27);
        assertThat(responseEvent1.getResponse()).isSameAs(event.getResponse(0));
        assertThat(responseEvent1.getResponseTime()).isSameAs(event.getResponseTime(0));

        // try to get the second ack (seq=11)
        assertThat(event.getResponse((res) -> res.getSequence() == 11)).isSameAs(KnxBody.TUNNELING_ACK_BODY_2);
        assertThat(event.getResponseTime((res) -> res.getSequence() == 11)).isBetween(instantBeforeResponse2, instantAfterResponse2);
        final var responseEvent2 = event.getResponseEvent((res) -> res.getSequence() == 11);
        assertThat(responseEvent2.getResponse()).isSameAs(event.getResponse(1));
        assertThat(responseEvent2.getResponseTime()).isSameAs(event.getResponseTime(1));

        // try to get an unknown ack
        assertThat(event.getResponse((res) -> false)).isNull();
        assertThat(event.getResponseTime((res) -> false)).isNull();
        assertThat(event.getResponseEvent((res) -> false)).isNull();
    }

    /**
     * Test {@link KnxMultiEvent#toString()}
     */
    @Test
    @DisplayName("Check toString() method for multi event")
    public void testToString() {
        final var instantFormatRegex = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d+Z";
        final var instantFormatPlaceholder = "0000-00-00T00:00:00.000000000Z";

        // initial test
        assertThat(new KnxMultiEvent<>()).hasToString("KnxMultiEvent{requestEvent=null, responseEvents=[]}");

        // test with request
        final var eventRequest = new KnxMultiEvent<>();
        eventRequest.setRequest(KnxBody.TUNNELING_REQUEST_BODY);
        assertThat(eventRequest.toString().replaceAll(instantFormatRegex, instantFormatPlaceholder)).isEqualTo(
                String.format("KnxMultiEvent{requestEvent=RequestEvent{requestTime=%s, request=%s}, responseEvents=[]}",
                        instantFormatPlaceholder,
                        KnxBody.TUNNELING_REQUEST_BODY));

        // test with response
        final var eventResponse = new KnxMultiEvent<>();
        eventResponse.setResponse(KnxBody.TUNNELING_ACK_BODY);
        assertThat(eventResponse.toString().replaceAll(instantFormatRegex, instantFormatPlaceholder)).isEqualTo(
                String.format("KnxMultiEvent{requestEvent=null, responseEvents=[ResponseEvent{responseTime=%s, response=%s}]}",
                        instantFormatPlaceholder,
                        KnxBody.TUNNELING_ACK_BODY));

        // test with one more response
        eventResponse.setResponse(KnxBody.TUNNELING_ACK_BODY_2);
        assertThat(eventResponse.toString().replaceAll(instantFormatRegex, instantFormatPlaceholder)).isEqualTo(
                String.format("KnxMultiEvent{requestEvent=null, responseEvents=[ResponseEvent{responseTime=%s, response=%s}, ResponseEvent{responseTime=%s, response=%s}]}",
                        instantFormatPlaceholder,
                        KnxBody.TUNNELING_ACK_BODY,
                        instantFormatPlaceholder,
                        KnxBody.TUNNELING_ACK_BODY_2));
    }
}
