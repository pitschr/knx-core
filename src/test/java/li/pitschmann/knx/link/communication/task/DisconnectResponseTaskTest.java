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
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.KnxEventData;
import li.pitschmann.knx.link.communication.KnxEventPool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.Flow;

/**
 * Test for {@link DisconnectResponseTask}
 *
 * @author PITSCHR
 */
public class DisconnectResponseTaskTest {

    /**
     * Tests the {@link DisconnectResponseTask#onNext(Body)}
     */
    @Test
    @DisplayName("Test 'onNext(Body)' method")
    public void testOnNext() {
        final var task = createTask();

        // correct body
        final var correctBody = Mockito.mock(DisconnectResponseBody.class);
        task.onNext(correctBody);

        // wrong body - should not be an issue - simply ignored
        final var wrongBody = Mockito.mock(Body.class);
        task.onNext(wrongBody);
    }

    /**
     * Test the {@link DisconnectResponseTask#onError(Throwable)}
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
     * Test the {@link DisconnectResponseTask#onComplete()}
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
     * Helper for creating a {@link DisconnectResponseTask}
     *
     * @return returns a newly instance of {@link DisconnectResponseTask}
     */
    private DisconnectResponseTask createTask() {
        final var internalClient = Mockito.mock(InternalKnxClient.class);
        final var eventPool = Mockito.mock(KnxEventPool.class);
        final var eventData = Mockito.mock(KnxEventData.class);
        final var subscription = Mockito.mock(Flow.Subscription.class);

        Mockito.doReturn(eventData).when(eventPool).disconnectEvent();
        Mockito.when(internalClient.getEventPool()).thenReturn(eventPool);

        final var task = new DisconnectResponseTask(internalClient);
        task.onSubscribe(subscription);
        return task;
    }
}
