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

package li.pitschmann.knx.core.plugin.api.v1.controllers;

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
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentMatcher;
import ro.pippo.controller.Controller;
import ro.pippo.core.HttpConstants;

import static li.pitschmann.knx.core.plugin.api.TestUtils.asJson;
import static li.pitschmann.knx.core.plugin.api.TestUtils.readJsonFile;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link StatisticController}
 */
public class StatisticControllerTest {

    @ControllerTest(StatisticController.class)
    @DisplayName("OK: Test /statistic endpoint")
    public void testStatistic(final Controller controller) {
        final var statisticController = (StatisticController) controller;

        //
        // Mocking
        //
        final var statisticMock = createKnxStatisticMock();
        when(statisticController.getKnxClient().getStatistic()).thenReturn(statisticMock);

        //
        // Verification
        //

        final var response = statisticController.getStatistic();
        assertThat(controller.getResponse().getStatus()).isEqualTo(HttpConstants.StatusCode.OK);

        final var responseJson = asJson(response);
        assertThatJson(responseJson).isEqualTo(readJsonFile("/json/StatisticControllerTest-testStatistic.json"));
    }

    private KnxStatistic createKnxStatisticMock() {
        final var statisticMock = mock(KnxStatistic.class);
        when(statisticMock.getNumberOfBodyReceived()).thenReturn(10L);
        when(statisticMock.getNumberOfBytesReceived()).thenReturn(11L);
        when(statisticMock.getNumberOfBodySent()).thenReturn(12L);
        when(statisticMock.getNumberOfBytesSent()).thenReturn(13L);
        when(statisticMock.getNumberOfErrors()).thenReturn(14L);
        when(statisticMock.getErrorRate()).thenReturn(1.56d);

        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(SearchRequestBody.class)))).thenReturn(100L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(SearchResponseBody.class)))).thenReturn(101L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(DescriptionRequestBody.class)))).thenReturn(110L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(DescriptionResponseBody.class)))).thenReturn(111L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(ConnectRequestBody.class)))).thenReturn(120L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(ConnectResponseBody.class)))).thenReturn(121L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(ConnectionStateRequestBody.class)))).thenReturn(130L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(ConnectionStateResponseBody.class)))).thenReturn(131L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(DisconnectRequestBody.class)))).thenReturn(140L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(DisconnectResponseBody.class)))).thenReturn(141L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(TunnelingRequestBody.class)))).thenReturn(150L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(TunnelingAckBody.class)))).thenReturn(151L);
        when(statisticMock.getNumberOfBodyReceived(argThat(new ClassMatcher<>(RoutingIndicationBody.class)))).thenReturn(161L);

        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(SearchRequestBody.class)))).thenReturn(200L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(SearchResponseBody.class)))).thenReturn(201L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(DescriptionRequestBody.class)))).thenReturn(210L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(DescriptionResponseBody.class)))).thenReturn(211L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(ConnectRequestBody.class)))).thenReturn(220L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(ConnectResponseBody.class)))).thenReturn(221L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(ConnectionStateRequestBody.class)))).thenReturn(230L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(ConnectionStateResponseBody.class)))).thenReturn(231L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(DisconnectRequestBody.class)))).thenReturn(240L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(DisconnectResponseBody.class)))).thenReturn(241L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(TunnelingRequestBody.class)))).thenReturn(250L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(TunnelingAckBody.class)))).thenReturn(251L);
        when(statisticMock.getNumberOfBodySent(argThat(new ClassMatcher<>(RoutingIndicationBody.class)))).thenReturn(260L);

        return statisticMock;
    }

    public static class ClassMatcher<T> implements ArgumentMatcher<Class<T>> {
        private final Class<T> expectedClass;

        public ClassMatcher(final Class<T> expectedClass) {
            this.expectedClass = expectedClass;
        }

        @Override
        public boolean matches(final Class<T> obj) {
            return expectedClass == obj;
        }
    }
}
