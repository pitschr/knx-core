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

import li.pitschmann.knx.test.KnxBody;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the {@link KnxEventData}
 *
 * @author PITSCHR
 */
public class KnxEventDataTest {
    /**
     * Tests the initialization of {@link KnxEventData}
     */
    @Test
    public void testInit() {
        final var eventData = new KnxEventData<>();

        assertThat(eventData.hasRequest()).isFalse();
        assertThat(eventData.getRequest()).isNull();
        assertThat(eventData.getRequestTime()).isNull();

        assertThat(eventData.hasResponse()).isFalse();
        assertThat(eventData.getResponse()).isNull();
        assertThat(eventData.getResponseTime()).isNull();
    }

    /**
     * Tests {@link KnxEventData#getRequest()} and {@link KnxEventData#getRequestTime()}
     */
    @Test
    public void testRequest() {
        final var eventData = new KnxEventData<>();

        final var instantBeforeRequest = Instant.now();
        eventData.setRequest(KnxBody.TUNNELING_REQUEST_BODY);
        final var instantAfterRequest = Instant.now();

        assertThat(eventData.hasRequest()).isTrue();
        assertThat(eventData.getRequest()).isNotNull();
        assertThat(eventData.getRequestTime()).isBetween(instantBeforeRequest, instantAfterRequest);

        assertThat(eventData.hasResponse()).isFalse();
        assertThat(eventData.getResponse()).isNull();
        assertThat(eventData.getResponseTime()).isNull();
    }

    /**
     * Tests {@link KnxEventData#getResponse()} and {@link KnxEventData#getResponseTime()}
     */
    @Test
    public void testResponse() {
        final var eventData = new KnxEventData<>();

        final var instantBeforeResponse = Instant.now();
        eventData.setResponse(KnxBody.TUNNELING_ACK_BODY);
        final var instantAfterResponse = Instant.now();

        assertThat(eventData.hasRequest()).isFalse();
        assertThat(eventData.getRequest()).isNull();
        assertThat(eventData.getRequestTime()).isNull();

        assertThat(eventData.hasResponse()).isTrue();
        assertThat(eventData.getResponse()).isNotNull();
        assertThat(eventData.getResponseTime()).isBetween(instantBeforeResponse, instantAfterResponse);
    }

    /**
     * Test {@link KnxEventData#toString()}
     */
    @Test
    public void testToString() {
        // initial test
        assertThat(new KnxEventData<>()).hasToString("KnxEventData{requestTime=null, request=null, responseTime=null, response=null}");

        // test with request
        final var eventRequest = new KnxEventData<>();
        eventRequest.setRequest(KnxBody.TUNNELING_REQUEST_BODY);
        assertThat(eventRequest).hasToString(String.format("KnxEventData{requestTime=%s, request=%s, responseTime=null, response=null}", eventRequest.getRequestTime(), KnxBody.TUNNELING_REQUEST_BODY.toString()));

        // test with response
        final var eventResponse = new KnxEventData<>();
        eventResponse.setResponse(KnxBody.TUNNELING_ACK_BODY);
    }
}
