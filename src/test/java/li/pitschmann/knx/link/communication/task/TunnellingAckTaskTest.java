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
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.KnxEventData;
import li.pitschmann.knx.link.communication.KnxEventPool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.Flow;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Test for {@link TunnellingAckTask}
 *
 * @author PITSCHR
 */
public class TunnellingAckTaskTest {
    /**
     * Tests the {@link TunnellingAckTask#onNext(Body)}
     */
    @Test
    @DisplayName("Test 'onNext(Body)' method")
    public void testOnNext() {
        final var task = createTask();

        // correct body #1 - not acknowledged yet
        final var correctBody = Mockito.mock(TunnellingAckBody.class);
        when(correctBody.getSequence()).thenReturn(0);
        task.onNext(correctBody);

        // correct body #2 - already acknowledged
        final var correctBodyAlreadyAcknowledged = Mockito.mock(TunnellingAckBody.class);
        when(correctBodyAlreadyAcknowledged.getSequence()).thenReturn(1);
        task.onNext(correctBodyAlreadyAcknowledged);

        // wrong body - should not be an issue - simply ignored
        final var wrongBody = Mockito.mock(Body.class);
        task.onNext(wrongBody);
    }

    /**
     * Test the {@link TunnellingAckTask#onError(Throwable)}
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
     * Test the {@link TunnellingAckTask#onComplete()}
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
     * Helper for creating a {@link TunnellingAckTask}
     *
     * @return returns a newly instance of {@link TunnellingAckTask}
     */
    private TunnellingAckTask createTask() {
        final var internalClient = Mockito.mock(InternalKnxClient.class);
        final var eventPool = Mockito.mock(KnxEventPool.class);
        final var subscription = Mockito.mock(Flow.Subscription.class);

        // 0 = (normal) not acknowledged
        final var eventData = Mockito.mock(KnxEventData.class);
        doReturn(eventData).when(eventPool).get(Mockito.any(TunnellingAckBody.class));

        // 1 = already acknowledged
        final var eventDataAcknowledgedAlready = Mockito.mock(KnxEventData.class);
        when(eventDataAcknowledgedAlready.hasResponse()).thenReturn(true);
        doReturn(eventDataAcknowledgedAlready).when(eventPool).get((TunnellingAckBody) Mockito.argThat(t -> ((TunnellingAckBody) t).getSequence() == 1));

        when(internalClient.getEventPool()).thenReturn(eventPool);

        final var task = new TunnellingAckTask(internalClient);
        task.onSubscribe(subscription);
        return task;
    }
}
