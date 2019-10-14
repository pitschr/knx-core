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
import li.pitschmann.knx.link.body.SearchResponseBody;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.InternalKnxEventPool;
import li.pitschmann.knx.link.communication.event.KnxMultiEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link SearchResponseTask}
 *
 * @author PITSCHR
 */
public class SearchResponseTaskTest {

    /**
     * Tests the {@link SearchResponseTask#onNext(Body)}
     */
    @Test
    @DisplayName("Test 'onNext(Body)' method")
    public void testOnNext() {
        final var task = createTask();

        // correct body
        final var correctBody = mock(SearchResponseBody.class);
        task.onNext(correctBody);

        // wrong body - should not be an issue - simply ignored
        final var wrongBody = mock(Body.class);
        task.onNext(wrongBody);
    }

    /**
     * Test the {@link SearchResponseTask#onError(Throwable)}
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
     * Test the {@link SearchResponseTask#onComplete()}
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
     * Helper for creating a {@link SearchResponseTask}
     *
     * @return returns a newly instance of {@link SearchResponseTask}
     */
    private SearchResponseTask createTask() {
        final var internalClient = mock(InternalKnxClient.class);
        final var eventPool = mock(InternalKnxEventPool.class);
        final var eventData = mock(KnxMultiEvent.class);
        final var subscription = mock(Flow.Subscription.class);

        doReturn(eventData).when(eventPool).searchEvent();
        when(internalClient.getEventPool()).thenReturn(eventPool);

        final var task = new SearchResponseTask(internalClient);
        task.onSubscribe(subscription);
        return task;
    }
}
