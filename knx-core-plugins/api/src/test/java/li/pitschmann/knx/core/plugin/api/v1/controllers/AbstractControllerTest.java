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

import io.javalin.http.util.ContextUtil;
import li.pitschmann.knx.core.communication.KnxClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link AbstractController}
 */
class AbstractControllerTest {

    @Test
    @DisplayName("Test the limitation of result using 'start' request parameter")
    void testStart() {
        final var testController = new TestController(mock(KnxClient.class));

        final var testList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        //
        // Verification
        //
        final var requestMock = mock(HttpServletRequest.class);
        final var context = ContextUtil.init(requestMock, mock(HttpServletResponse.class));

        // no "start" parameter -> defaults back to "0"
        final var startDefault = testController.limitAndGetAsList(context, testList);
        assertThat(startDefault).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        // return all elements
        when(requestMock.getQueryString()).thenReturn("start=0");
        final var startZero = testController.limitAndGetAsList(context, testList);
        assertThat(startZero).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        // return elements after 7th index
        when(requestMock.getQueryString()).thenReturn("start=7");
        final var startNonZero = testController.limitAndGetAsList(context, testList);
        assertThat(startNonZero).containsExactly(7, 8, 9);

        // return elements after 11th index (overflow)
        when(requestMock.getQueryString()).thenReturn("start=11");
        final var startOverflow = testController.limitAndGetAsList(context, testList);
        assertThat(startOverflow).isEmpty();

        // fail test with negative start
        when(requestMock.getQueryString()).thenReturn("start=-3");
        assertThatThrownBy(() -> testController.limitAndGetAsList(context, testList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start should be 0 or greater: -3");
    }

    @Test
    @DisplayName("Test the limitation of result using 'limit' request parameter")
    void testLimit() {
        final var testController = new TestController(mock(KnxClient.class));

        final var testList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        //
        // Verification
        //
        final var requestMock = mock(HttpServletRequest.class);
        final var context = ContextUtil.init(requestMock, mock(HttpServletResponse.class));

        // no "limit" parameter
        final var limitDefault = testController.limitAndGetAsList(context, testList);
        assertThat(limitDefault).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        // limit to 0 elements
        when(requestMock.getQueryString()).thenReturn("limit=0");
        final var limitZero = testController.limitAndGetAsList(context, testList);
        assertThat(limitZero).isEmpty();

        // limit to 6 elements
        when(requestMock.getQueryString()).thenReturn("limit=6");
        final var limitNonZero = testController.limitAndGetAsList(context, testList);
        assertThat(limitNonZero).containsExactly(0, 1, 2, 3, 4, 5);

        // limit to 11 elements (overflow)
        when(requestMock.getQueryString()).thenReturn("limit=11");
        final var limitOverflow = testController.limitAndGetAsList(context, testList);
        assertThat(limitOverflow).hasSize(10);

        // fail test with negative limit
        when(requestMock.getQueryString()).thenReturn("limit=-1");
        assertThatThrownBy(() -> testController.limitAndGetAsList(context, testList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Limit should be 0 or greater: -1");
    }

    @Test
    @DisplayName("Test the limitation of result using 'start' and 'limit' request parameters")
    void testStartAndLimit() {
        final var testController = new TestController(mock(KnxClient.class));

        final var testList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        //
        // Verification
        //
        final var requestMock = mock(HttpServletRequest.class);
        final var context = ContextUtil.init(requestMock, mock(HttpServletResponse.class));

        // start with 3rd index and limit to 2 elements
        when(requestMock.getQueryString()).thenReturn("start=3&limit=2");
        final var listLimit = testController.limitAndGetAsList(context, testList);
        assertThat(listLimit).containsExactly(3, 4);

        // start with 7th index and limit to 4 elements
        when(requestMock.getQueryString()).thenReturn("start=7&limit=4");
        final var listLimit2 = testController.limitAndGetAsList(context, testList);
        assertThat(listLimit2).containsExactly(7, 8, 9); // only 3 elements as list ends at 9
    }

    /**
     * Dummy Controller for testing purposes only
     */
    public static class TestController extends AbstractController {
        public TestController(final KnxClient knxClient) {
            super(knxClient);
        }
    }
}
