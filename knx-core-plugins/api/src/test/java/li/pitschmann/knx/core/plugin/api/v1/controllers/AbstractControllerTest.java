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

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.api.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import ro.pippo.controller.Controller;
import ro.pippo.core.ParameterValue;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link AbstractController}
 */
public class AbstractControllerTest {

    /**
     * Test the 'start' parameter for {@link AbstractController#limitAndGetAsList(Collection)} method
     */
    @ControllerTest(value = TestController.class)
    @DisplayName("Test the limitation of result using 'start' request parameter")
    public void testStart(final Controller controller) {
        final var testController = (TestController) controller;

        final var testList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        //
        // Verification
        //

        // return all elements
        final var listAll = testController.limitAndGetAsList(testList);
        assertThat(listAll).hasSize(10);

        when(controller.getRequest().getParameter("start")).thenReturn(new ParameterValue("0"));
        final var listAll2 = testController.limitAndGetAsList(testList);
        assertThat(listAll2).hasSize(10);

        // return elements after 7th index
        when(controller.getRequest().getParameter("start")).thenReturn(new ParameterValue("7"));
        final var listStart = testController.limitAndGetAsList(testList);
        assertThat(listStart).containsExactly(7, 8, 9);

        // return elements after 11th index (overflow)
        when(controller.getRequest().getParameter("start")).thenReturn(new ParameterValue("11"));
        final var listStartOverflow = testController.limitAndGetAsList(testList);
        assertThat(listStartOverflow).isEmpty();

        // fail test with negative start
        when(controller.getRequest().getParameter("start")).thenReturn(new ParameterValue("-1"));
        assertThatThrownBy(() -> testController.limitAndGetAsList(testList)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start should be 0 or greater: -1");
    }

    /**
     * Test the 'limit' parameter for {@link AbstractController#limitAndGetAsList(Collection)} method
     */
    @ControllerTest(value = TestController.class)
    @DisplayName("Test the limitation of result using 'limit' request parameter")
    public void testLimit(final Controller controller) {
        final var testController = (TestController) controller;

        final var testList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        //
        // Verification
        //

        // limit to 0 elements
        when(controller.getRequest().getParameter("limit")).thenReturn(new ParameterValue("0"));
        final var listAll2 = testController.limitAndGetAsList(testList);
        assertThat(listAll2).isEmpty();

        // limit to 6 elements
        when(controller.getRequest().getParameter("limit")).thenReturn(new ParameterValue("6"));
        final var listLimit = testController.limitAndGetAsList(testList);
        assertThat(listLimit).containsExactly(0, 1, 2, 3, 4, 5);

        // limit to 11 elements (overflow)
        when(controller.getRequest().getParameter("limit")).thenReturn(new ParameterValue("11"));
        final var listLimitOverflow = testController.limitAndGetAsList(testList);
        assertThat(listLimitOverflow).hasSize(10);

        // fail test with negative limit
        when(controller.getRequest().getParameter("limit")).thenReturn(new ParameterValue("-1"));
        assertThatThrownBy(() -> testController.limitAndGetAsList(testList)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Limit should be 0 or greater: -1");

    }

    /**
     * Test the 'start' and 'limit' parameter for {@link AbstractController#limitAndGetAsList(Collection)} method
     */
    @ControllerTest(value = TestController.class)
    @DisplayName("Test the limitation of result using 'start' and 'limit' request parameters")
    public void testStartAndLimit(final Controller controller) {
        final var testController = (TestController) controller;

        final var testList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        //
        // Verification
        //

        // start with 3rd index and limit to 2 elements
        when(controller.getRequest().getParameter("start")).thenReturn(new ParameterValue("3"));
        when(controller.getRequest().getParameter("limit")).thenReturn(new ParameterValue("2"));
        final var listLimit = testController.limitAndGetAsList(testList);
        assertThat(listLimit).containsExactly(3, 4);

        // start with 7th index and limit to 4 elements
        when(controller.getRequest().getParameter("start")).thenReturn(new ParameterValue("7"));
        when(controller.getRequest().getParameter("limit")).thenReturn(new ParameterValue("4"));
        final var listLimit2 = testController.limitAndGetAsList(testList);
        assertThat(listLimit2).containsExactly(7, 8, 9); // only 3 elements
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
