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

package li.pitschmann.knx.link.communication.task;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.body.cemi.MessageCode;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.KnxStatusPoolImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link TunnelingRequestTask}
 *
 * @author PITSCHR
 */
public class TunnelingRequestTaskTest {

    /**
     * Tests the {@link TunnelingRequestTask#onNext(Body)}
     */
    @Test
    @DisplayName("Test 'onNext(Body)' method")
    public void testOnNext() {
        final var task = createTask();


        final var cemi = mock(CEMI.class);
        final var correctBody = mock(TunnelingRequestBody.class);
        when(correctBody.getCEMI()).thenReturn(cemi);
        when(cemi.getSourceAddress()).thenReturn(IndividualAddress.of(1, 2, 3));
        when(cemi.getDestinationAddress()).thenReturn(GroupAddress.of(4, 5, 6));

        // correct body #1 - Indication + Response
        when(cemi.getMessageCode()).thenReturn(MessageCode.L_DATA_IND);
        when(cemi.getApci()).thenReturn(APCI.GROUP_VALUE_RESPONSE);
        task.onNext(correctBody);

        // correct body #2 - Indication + Write
        when(cemi.getMessageCode()).thenReturn(MessageCode.L_DATA_IND);
        when(cemi.getApci()).thenReturn(APCI.GROUP_VALUE_WRITE);
        task.onNext(correctBody);

        // correct body #3 - Connection + Write
        when(cemi.getMessageCode()).thenReturn(MessageCode.L_DATA_CON);
        when(cemi.getApci()).thenReturn(APCI.GROUP_VALUE_WRITE);
        task.onNext(correctBody);

        // wrong body - should not be an issue - simply ignored
        final var wrongBody = mock(Body.class);
        task.onNext(wrongBody);
    }

    /**
     * Test the {@link TunnelingRequestTask#onError(Throwable)}
     * <p/>
     * Calling this method should not throw a {@link Throwable}. It is used for logging purposes only.
     */
    @Test
    @DisplayName("Test 'onError(Throwable)' method")
    public void testOnError() {
        // should be OK
        createTask().onError(new Throwable());
    }

    /**
     * Test the {@link TunnelingRequestTask#onComplete()}
     * <p/>
     * Calling this method should not throw a {@link Throwable}. It is used for logging purposes only.
     */
    @Test
    @DisplayName("Test 'onComplete()' method")
    public void testOnComplete() {
        // should be OK
        createTask().onComplete();
    }

    /**
     * Helper for creating a {@link TunnelingRequestTask}
     *
     * @return returns a newly instance of {@link TunnelingRequestTask}
     */
    private TunnelingRequestTask createTask() {
        final var internalClient = mock(InternalKnxClient.class);
        final var statusPool = mock(KnxStatusPoolImpl.class);
        final var subscription = mock(Flow.Subscription.class);

        when(internalClient.getStatusPool()).thenReturn(statusPool);

        final var task = new TunnelingRequestTask(internalClient);
        task.onSubscribe(subscription);
        return task;
    }
}