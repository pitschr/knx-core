/*
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

package li.pitschmann.knx.core.communication.task;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.cemi.APCI;
import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.cemi.MessageCode;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link RoutingIndicationTask}
 *
 * @author PITSCHR
 */
public class RoutingIndicationTaskTest {

    /**
     * Tests the {@link RoutingIndicationTask#onNext(Body)}
     */
    @Test
    @DisplayName("Test 'onNext(Body)' method")
    public void testOnNext() {
        final var task = createTask();

        final var cemi = mock(CEMI.class);
        final var correctBody = mock(RoutingIndicationBody.class);
        when(correctBody.getCEMI()).thenReturn(cemi);
        when(cemi.getSourceAddress()).thenReturn(IndividualAddress.of(1, 2, 3));
        when(cemi.getDestinationAddress()).thenReturn(GroupAddress.of(4, 5, 6));

        // correct body #1 - Indication + Response
        when(cemi.getMessageCode()).thenReturn(MessageCode.L_DATA_IND);
        when(cemi.getAPCI()).thenReturn(APCI.GROUP_VALUE_RESPONSE);
        task.onNext(correctBody);

        // correct body #2 - Indication + Write
        when(cemi.getMessageCode()).thenReturn(MessageCode.L_DATA_IND);
        when(cemi.getAPCI()).thenReturn(APCI.GROUP_VALUE_WRITE);
        task.onNext(correctBody);

        // correct body #3 - Connection + Write
        when(cemi.getMessageCode()).thenReturn(MessageCode.L_DATA_CON);
        when(cemi.getAPCI()).thenReturn(APCI.GROUP_VALUE_WRITE);
        task.onNext(correctBody);
    }

    /**
     * Tests the {@link TunnelingRequestTask#onNext(Body)} with unexpected class type, message code and APCI type
     */
    @Test
    @DisplayName("Error: Test 'onNext(Body)' method with unexpected data")
    public void testOnNextError() {
        final var task = createTask();

        // wrong body
        final var wrongBody = mock(Body.class);
        task.onNext(wrongBody);

        // wrong wrong message code
        final var bodyWithWrongMessageCode = mock(RoutingIndicationBody.class);
        final var cemiMock = mock(CEMI.class);
        when(cemiMock.getMessageCode()).thenReturn(MessageCode.L_DATA_REQ);
        when(bodyWithWrongMessageCode.getCEMI()).thenReturn(cemiMock);
        task.onNext(bodyWithWrongMessageCode);

        // wrong APCI for message indication
        final var bodyWithWrongApciCode = mock(RoutingIndicationBody.class);
        final var cemiMock2 = mock(CEMI.class);
        when(cemiMock2.getMessageCode()).thenReturn(MessageCode.L_DATA_IND);
        when(cemiMock2.getAPCI()).thenReturn(APCI.INDIVIDUAL_ADDRESS_WRITE);
        when(bodyWithWrongApciCode.getCEMI()).thenReturn(cemiMock2);
        task.onNext(bodyWithWrongApciCode);
    }

    /**
     * Test the {@link RoutingIndicationTask#onError(Throwable)}
     * <p>
     * Calling this method should not throw a {@link Throwable}. It is used for logging purposes only.
     */
    @Test
    @DisplayName("Test 'onError(Throwable)' method")
    public void testOnError() {
        // should be OK
        createTask().onError(new Throwable());
    }

    /**
     * Test the {@link RoutingIndicationTask#onComplete()}
     * <p>
     * Calling this method should not throw a {@link Throwable}. It is used for logging purposes only.
     */
    @Test
    @DisplayName("Test 'onComplete()' method")
    public void testOnComplete() {
        // should be OK
        createTask().onComplete();
    }

    /**
     * Helper for creating a {@link RoutingIndicationTask}
     *
     * @return returns a newly instance of {@link RoutingIndicationTask}
     */
    private RoutingIndicationTask createTask() {
        final var internalClientMock = TestHelpers.mockInternalKnxClient();
        final var subscription = mock(Flow.Subscription.class);

        final var task = new RoutingIndicationTask(internalClientMock);
        task.onSubscribe(subscription);
        return task;
    }
}
