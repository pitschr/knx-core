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

package li.pitschmann.knx.core.test;

import li.pitschmann.knx.core.body.Body;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * Interface for KNX Mock Server Channel (UDP, TCP)
 */
public interface MockServerChannel<T extends SelectableChannel> extends AutoCloseable {

    /**
     * Returns the current channel
     *
     * @return channel
     */
    T getChannel();

    /**
     * Returns the current port where KNX mock server is listening
     *
     * @return port
     */
    int getPort();

    /**
     * Reads {@link Body} from {@link SelectionKey}
     *
     * @param key contains channel for incoming traffic
     * @return An instance of {@link Body}
     * @throws IOException If an I/O error occurs
     */
    Body read(SelectionKey key) throws IOException;

    /**
     * Sends {@link Body} to given {@link SelectionKey}
     *
     * @param key  contains channel for outgoing traffic
     * @param body body to be sent
     * @throws IOException If an I/O error occurs
     */
    void send(SelectionKey key, Body body) throws IOException;

    @Override
    void close() throws IOException;
}
