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
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.knx.link.communication.KnxEventData;
import li.pitschmann.knx.link.communication.KnxEventPool;
import li.pitschmann.knx.link.exceptions.KnxBodyNotReceivedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.Flow;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test for {@link ConnectResponseTask}
 *
 * @author PITSCHR
 */
public class ConnectResponseTaskTest {

    /**
     * Tests the {@link ConnectResponseTask#onNext(Body)}
     * <p/>
     * Providing an unexpected {@link Body} (others than {@link ConnectResponseBody}) will
     * throw {@link KnxBodyNotReceivedException}.
     */
    @Test
    @DisplayName("Test 'onNext(Body)' method")
    public void testOnNext() {
        final var task = createTask();

        // correct body
        final var correctBody = Mockito.mock(ConnectResponseBody.class);
        task.onNext(correctBody);

        // wrong body
        final var wrongBody = Mockito.mock(Body.class);
        assertThatThrownBy(() -> task.onNext(wrongBody)).isInstanceOf(KnxBodyNotReceivedException.class);
    }

    /**
     * Test the {@link ConnectResponseTask#onError(Throwable)}
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
     * Test the {@link ConnectResponseTask#onComplete()}
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
     * Helper for creating a {@link ConnectResponseTask}
     *
     * @return returns a newly instance of {@link ConnectResponseTask}
     */
    private ConnectResponseTask createTask() {
        final var internalClient = Mockito.mock(InternalKnxClient.class);
        final var eventPool = Mockito.mock(KnxEventPool.class);
        final var eventData = Mockito.mock(KnxEventData.class);
        final var subscription = Mockito.mock(Flow.Subscription.class);

        Mockito.doReturn(eventData).when(eventPool).connectEvent();
        Mockito.when(internalClient.getEventPool()).thenReturn(eventPool);

        final var task = new ConnectResponseTask(internalClient);
        task.onSubscribe(subscription);

        return task;
    }
}
