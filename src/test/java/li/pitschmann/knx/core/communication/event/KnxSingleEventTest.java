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

package li.pitschmann.knx.core.communication.event;

import li.pitschmann.knx.core.test.KnxBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the {@link KnxSingleEvent}
 *
 * @author PITSCHR
 */
class KnxSingleEventTest {

    @Test
    @DisplayName("Check of initialization values for single event")
    void testInit() {
        final var event = new KnxSingleEvent<>();

        assertThat(event.hasRequest()).isFalse();
        assertThat(event.getRequest()).isNull();
        assertThat(event.getRequestTime()).isNull();

        assertThat(event.hasResponse()).isFalse();
        assertThat(event.getResponse()).isNull();
        assertThat(event.getResponseTime()).isNull();
    }

    @Test
    @DisplayName("Check set of request and request time for single event")
    void testRequest() {
        final var event = new KnxSingleEvent<>();

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

    @Test
    @DisplayName("Check set of response and response time for single event and override it")
    void testResponse() {
        final var event = new KnxSingleEvent<>();

        final var instantBeforeResponse = Instant.now();
        event.setResponse(KnxBody.TUNNELING_ACK_BODY);
        final var instantAfterResponse = Instant.now();

        assertThat(event.hasResponse()).isTrue();
        assertThat(event.getResponse()).isEqualTo(KnxBody.TUNNELING_ACK_BODY);
        assertThat(event.getResponseTime()).isBetween(instantBeforeResponse, instantAfterResponse);

        // set another response (expected: previous response will be overwritten)
        final var instantBeforeResponse2 = Instant.now();
        event.setResponse(KnxBody.TUNNELING_ACK_BODY_2);
        final var instantAfterResponse2 = Instant.now();

        assertThat(event.hasRequest()).isFalse();
        assertThat(event.getRequest()).isNull();
        assertThat(event.getRequestTime()).isNull();

        assertThat(event.hasResponse()).isTrue(); // no change
        assertThat(event.getResponse()).isEqualTo(KnxBody.TUNNELING_ACK_BODY_2); // changed
        assertThat(event.getResponseTime()).isBetween(instantBeforeResponse2, instantAfterResponse2); // changed
    }

    @Test
    @DisplayName("Check the #clearResponse() for single event")
    void testClearResponse() {
        final var event = new KnxSingleEvent<>();

        event.setResponse(KnxBody.TUNNELING_ACK_BODY);

        // response is present
        assertThat(event.hasResponse()).isTrue();
        assertThat(event.getResponse()).isEqualTo(KnxBody.TUNNELING_ACK_BODY);
        assertThat(event.getResponseTime()).isNotNull();

        // clear the response
        event.clearResponse();

        // response is not present anymore
        assertThat(event.hasResponse()).isFalse();
        assertThat(event.getResponse()).isNull();
        assertThat(event.getResponseTime()).isNull();
    }

    @Test
    @DisplayName("Check toString() method for single event")
    void testToString() {
        final var instantFormatRegex = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d+Z";
        final var instantFormatPlaceholder = "0000-00-00T00:00:00.000000000Z";

        // initial test
        assertThat(new KnxSingleEvent<>()).hasToString("KnxSingleEvent{requestEvent=null, responseEvent=null}");

        // test with request
        final var eventRequest = new KnxSingleEvent<>();
        eventRequest.setRequest(KnxBody.TUNNELING_REQUEST_BODY);
        assertThat(eventRequest.toString().replaceAll(instantFormatRegex, instantFormatPlaceholder)).isEqualTo(
                String.format("KnxSingleEvent{requestEvent=RequestEvent{requestTime=%s, request=%s}, responseEvent=null}",
                        instantFormatPlaceholder,
                        KnxBody.TUNNELING_REQUEST_BODY));

        // test with response
        final var eventResponse = new KnxSingleEvent<>();
        eventResponse.setResponse(KnxBody.TUNNELING_ACK_BODY);
        assertThat(eventResponse.toString().replaceAll(instantFormatRegex, instantFormatPlaceholder)).isEqualTo(
                String.format("KnxSingleEvent{requestEvent=null, responseEvent=ResponseEvent{responseTime=%s, response=%s}}",
                        instantFormatPlaceholder,
                        KnxBody.TUNNELING_ACK_BODY));
    }
}
