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

package li.pitschmann.knx.link.communication;

import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.body.*;

import java.util.concurrent.*;

/**
 * Interface for all KNX Clients.
 *
 * @author PITSCHR
 */
public interface KnxClient extends AutoCloseable {
    /**
     * Disconnects the communication by KNX client itself. This method is called by {@link AutoCloseable}.
     */
    @Override
    void close();

    /**
     * Returns the {@link Configuration} used by KNX client
     *
     * @return an immutable {@link Configuration}
     */
    Configuration getConfig();

    /**
     * Returns a copied {@link KnxStatistic} snapshot about the KNX communication
     *
     * @return an immutable {@link KnxStatistic}
     */
    KnxStatistic getStatistic();

    /**
     * Returns {@code true} if the close has already been requested
     *
     * @return {@code true} if closed
     */
    boolean isClosed();

    /**
     * Sends any {@link Body} packet immediately to the appropriate channel.
     * <p>
     * The appropriate channel will be chosen by {@link ControlChannelRelated} and {@link DataChannelRelated} marker
     * interfaces.
     *
     * @param body body to be sent
     */
    void send(final Body body);

    /**
     * Send {@link RequestBody} packet <strong>asynchronously</strong> to the appropriate channel. It returns a {@link Future} for further processing.
     * <p>
     * The appropriate channel will be chosen by {@link ControlChannelRelated} and {@link DataChannelRelated} marker
     * interfaces.
     *
     * @param requestBody
     * @param msTimeout   timeout in milliseconds waiting until the expected response body is fetched
     * @return a {@link Future} representing pending completion of the task containing either an instance of {@link ResponseBody},
     * or {@code null} if no response was received because of e.g. timeout
     */
    <T extends ResponseBody> Future<T> send(final RequestBody requestBody, final long msTimeout);
}
