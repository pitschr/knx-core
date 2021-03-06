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

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.communication.event.KnxSingleEvent;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link TunnelingAckTask}
 *
 * @author PITSCHR
 */
public class TunnelingAckTaskTest {
    /**
     * Tests the {@link TunnelingAckTask#onNext(Body)}
     */
    @Test
    @DisplayName("Test 'onNext(Body)' method")
    public void testOnNext() {
        final var task = createTask();

        // correct body #1 - not acknowledged yet
        final var correctBody = mock(TunnelingAckBody.class);
        when(correctBody.getSequence()).thenReturn(0);
        task.onNext(correctBody);

        // correct body #2 - already acknowledged
        final var correctBodyAlreadyAcknowledged = mock(TunnelingAckBody.class);
        when(correctBodyAlreadyAcknowledged.getSequence()).thenReturn(1);
        task.onNext(correctBodyAlreadyAcknowledged);

        // wrong body - should not be an issue - simply ignored
        final var wrongBody = mock(Body.class);
        task.onNext(wrongBody);
    }

    /**
     * Test the {@link TunnelingAckTask#onError(Throwable)}
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
     * Test the {@link TunnelingAckTask#onComplete()}
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
     * Helper for creating a {@link TunnelingAckTask}
     *
     * @return returns a newly instance of {@link TunnelingAckTask}
     */
    private TunnelingAckTask createTask() {
        final var internalClientMock = TestHelpers.mockInternalKnxClient();
        final var subscription = mock(Flow.Subscription.class);

        // 1 = already acknowledged
        final var eventPool = internalClientMock.getEventPool();
        final var eventDataAcknowledgedAlready = mock(KnxSingleEvent.class);
        when(eventDataAcknowledgedAlready.hasResponse()).thenReturn(true);
        doReturn(eventDataAcknowledgedAlready).when(eventPool).get((TunnelingAckBody) argThat(t -> ((TunnelingAckBody) t).getSequence() == 1));

        final var task = new TunnelingAckTask(internalClientMock);
        task.onSubscribe(subscription);
        return task;
    }
}
