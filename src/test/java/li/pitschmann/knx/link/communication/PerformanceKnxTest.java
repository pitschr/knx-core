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

import com.google.common.collect.*;
import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.test.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test for sending {@link DescriptionRequestBody} / receiving {@link DescriptionResponseBody} via
 * {@link KnxClient}
 *
 * @author PITSCHR
 */
public class PerformanceKnxTest {
    /**
     * How many times the TUNNELLING_REQUEST and TUNNELLING_ACK packets should be
     * sent between KNX Net/IP router and clients.
     * <p>
     * When doing a high number (100000 times) - then ensure that you set
     * the minimum log level at 'INFO'. Logging with DEBUG or lower will slow down
     * the system due I/O writing. Alter the log level in /test/resources/logback.xml
     * by setting the root level from to 'INFO'.
     */
    private static final int TIMES = 100;

    /**
     * Perform a happy path between {@link KnxClient} and the KNX Net/IP router with N packets.
     */
    @KnxTest({
            // On first request send DescriptionResponseBody
            KnxBody.DESCRIPTION_RESPONSE,
            // wait for next packet (will be: ConnectRequestBody)
            "WAIT=NEXT",
            // send ConnectResponseBody
            KnxBody.CONNECT_RESPONSE,
            // wait for next packet (will be: ConnetionStateRequestBody)
            "WAIT=NEXT",
            // ConnectionStateResponseBody
            KnxBody.CONNECTION_STATE_RESPONSE,
            // send TunnellingRequestBody N-times
            "REPEAT=" + TIMES + "{061004200015040700002e00bce010ff0a96010081}",
            // wait for packet with type 'DisconnectRequestBody'
            "WAIT=DISCONNECT_REQUEST",
            // send DisconnectResponseBody
            KnxBody.DISCONNECT_RESPONSE
    })
    @DisplayName("Test Performance (sending " + TIMES + "x Tunnelling requests/acks)")
    public void testSuccessPerformance(final KnxMockServer mockServer) {

        // Adjust JUnit specific configuration
        final var config = mockServer.newConfigBuilder() //
                // Use default setting (for unit testing it is set 1 seconds - instead of 10 seconds)
                .setting("timeout.request.connectionstate", String.valueOf(Constants.Timeouts.CONNECTIONSTATE_REQUEST_TIMEOUT))
                // Use default setting (for unit testing it is set 6 seconds - instead of 60 seconds)
                .setting("interval.connectionstate", String.valueOf(Constants.Interval.CONNECTIONSTATE))
                .build();

        try (final KnxClient client = new DefaultKnxClient(config)) {
            // after N-times tunnelling acknowledge sent by client a disconnect will be initiated
            mockServer.waitForReceivedServiceType(ServiceType.TUNNELING_ACK, TIMES);
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }

        // wait until all packets are sent/received
        mockServer.waitForCompletion();

        // assert packets
        List<Class<? extends Body>> expectedClasses = Lists.newLinkedList();
        expectedClasses.add(DescriptionRequestBody.class);
        expectedClasses.add(ConnectRequestBody.class);
        expectedClasses.add(ConnectionStateRequestBody.class);
        for (int i = 0; i < TIMES; i++) {
            expectedClasses.add(TunnellingAckBody.class);
        }
        expectedClasses.add(DisconnectRequestBody.class);
        mockServer.assertReceivedPackets(expectedClasses);
    }
}
