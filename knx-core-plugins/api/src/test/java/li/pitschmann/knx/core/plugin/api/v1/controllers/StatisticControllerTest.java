/*
 * KNX Link - A library for KNX Net/IP communication
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

package li.pitschmann.knx.core.plugin.api.v1.controllers;

import io.javalin.plugin.json.JavalinJson;
import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectResponseBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DescriptionResponseBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.body.SearchRequestBody;
import li.pitschmann.knx.core.body.SearchResponseBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.communication.KnxStatistic;
import li.pitschmann.knx.core.plugin.api.ControllerTest;
import li.pitschmann.knx.core.plugin.api.TestUtils;
import li.pitschmann.knx.core.plugin.api.v1.gson.ApiGsonEngine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import javax.servlet.http.HttpServletResponse;

import static li.pitschmann.knx.core.plugin.api.TestUtils.readJsonFile;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link StatisticController}
 */
public class StatisticControllerTest {

    @BeforeAll
    static void setUp() {
        final var gson = ApiGsonEngine.INSTANCE.getGson();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
    }

    @ControllerTest(StatisticController.class)
    @DisplayName("OK: Test /statistic endpoint")
    public void testStatistic(final StatisticController controller) {
        final var contextSpy = TestUtils.contextSpy();

        // Return mocked statistic
        final var statisticMock = createKnxStatisticMock();
        when(controller.getKnxClient().getStatistic()).thenReturn(statisticMock);

        // Execution
        controller.getStatistic(contextSpy);

        // Verification
        verify(contextSpy).status(HttpServletResponse.SC_OK);
        verify(contextSpy).result(readJsonFile("/json/StatisticControllerTest-testStatistic.json"));
    }

    private KnxStatistic createKnxStatisticMock() {
        final var statisticMock = mock(KnxStatistic.class);
        when(statisticMock.getNumberOfBodyReceived()).thenReturn(10L);
        when(statisticMock.getNumberOfBytesReceived()).thenReturn(11L);
        when(statisticMock.getNumberOfBodySent()).thenReturn(12L);
        when(statisticMock.getNumberOfBytesSent()).thenReturn(13L);
        when(statisticMock.getNumberOfErrors()).thenReturn(14L);
        when(statisticMock.getErrorRate()).thenReturn(1.56d);

        when(statisticMock.getNumberOfBodyReceived(eq(SearchRequestBody.class))).thenReturn(100L);
        when(statisticMock.getNumberOfBodyReceived(eq(SearchResponseBody.class))).thenReturn(101L);
        when(statisticMock.getNumberOfBodyReceived(eq(DescriptionRequestBody.class))).thenReturn(110L);
        when(statisticMock.getNumberOfBodyReceived(eq(DescriptionResponseBody.class))).thenReturn(111L);
        when(statisticMock.getNumberOfBodyReceived(eq(ConnectRequestBody.class))).thenReturn(120L);
        when(statisticMock.getNumberOfBodyReceived(eq(ConnectResponseBody.class))).thenReturn(121L);
        when(statisticMock.getNumberOfBodyReceived(eq(ConnectionStateRequestBody.class))).thenReturn(130L);
        when(statisticMock.getNumberOfBodyReceived(eq(ConnectionStateResponseBody.class))).thenReturn(131L);
        when(statisticMock.getNumberOfBodyReceived(eq(DisconnectRequestBody.class))).thenReturn(140L);
        when(statisticMock.getNumberOfBodyReceived(eq(DisconnectResponseBody.class))).thenReturn(141L);
        when(statisticMock.getNumberOfBodyReceived(eq(TunnelingRequestBody.class))).thenReturn(150L);
        when(statisticMock.getNumberOfBodyReceived(eq(TunnelingAckBody.class))).thenReturn(151L);
        when(statisticMock.getNumberOfBodyReceived(eq(RoutingIndicationBody.class))).thenReturn(161L);

        when(statisticMock.getNumberOfBodySent(eq(SearchRequestBody.class))).thenReturn(200L);
        when(statisticMock.getNumberOfBodySent(eq(SearchResponseBody.class))).thenReturn(201L);
        when(statisticMock.getNumberOfBodySent(eq(DescriptionRequestBody.class))).thenReturn(210L);
        when(statisticMock.getNumberOfBodySent(eq(DescriptionResponseBody.class))).thenReturn(211L);
        when(statisticMock.getNumberOfBodySent(eq(ConnectRequestBody.class))).thenReturn(220L);
        when(statisticMock.getNumberOfBodySent(eq(ConnectResponseBody.class))).thenReturn(221L);
        when(statisticMock.getNumberOfBodySent(eq(ConnectionStateRequestBody.class))).thenReturn(230L);
        when(statisticMock.getNumberOfBodySent(eq(ConnectionStateResponseBody.class))).thenReturn(231L);
        when(statisticMock.getNumberOfBodySent(eq(DisconnectRequestBody.class))).thenReturn(240L);
        when(statisticMock.getNumberOfBodySent(eq(DisconnectResponseBody.class))).thenReturn(241L);
        when(statisticMock.getNumberOfBodySent(eq(TunnelingRequestBody.class))).thenReturn(250L);
        when(statisticMock.getNumberOfBodySent(eq(TunnelingAckBody.class))).thenReturn(251L);
        when(statisticMock.getNumberOfBodySent(eq(RoutingIndicationBody.class))).thenReturn(260L);

        return statisticMock;
    }
}
