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
import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

import static org.mockito.Mockito.mock;

/**
 * Test for {@link ConnectionStateResponseTask}
 *
 * @author PITSCHR
 */
public class ConnectionStateResponseTaskTest {

    /**
     * Tests the {@link ConnectionStateResponseTask#onNext(Body)}
     */
    @Test
    @DisplayName("Test 'onNext(Body)' method")
    public void testOnNext() {
        final var task = createTask();

        // correct body
        final var correctBody = mock(ConnectionStateResponseBody.class);
        task.onNext(correctBody);

        // wrong body - should not be an issue - simply ignored
        final var wrongBody = mock(Body.class);
        task.onNext(wrongBody);
    }

    /**
     * Test the {@link ConnectionStateResponseTask#onError(Throwable)}
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
     * Test the {@link ConnectionStateResponseTask#onComplete()}
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
     * Helper for creating a {@link ConnectionStateResponseTask}
     *
     * @return returns a newly instance of {@link ConnectionStateResponseTask}
     */
    private ConnectionStateResponseTask createTask() {
        final var internalClientMock = TestHelpers.mockInternalKnxClient();
        final var subscription = mock(Flow.Subscription.class);

        final var task = new ConnectionStateResponseTask(internalClientMock);
        task.onSubscribe(subscription);
        return task;
    }
}
