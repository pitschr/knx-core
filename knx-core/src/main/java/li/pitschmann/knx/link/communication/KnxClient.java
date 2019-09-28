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

import li.pitschmann.knx.link.Configuration;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ControlChannelRelated;
import li.pitschmann.knx.link.body.DataChannelRelated;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

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
    @Nonnull
    KnxStatistic getStatistic();

    /**
     * Returns a copied {@link KnxStatusPool} snapshot about current status of KNX devices
     *
     * @return an immutable {@link KnxStatusPool}
     */
    @Nonnull
    KnxStatusPool getStatusPool();

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
    void send(final @Nonnull Body body);

    /**
     * Send {@link RequestBody} packet <strong>asynchronously</strong> to the appropriate channel.
     * It returns a {@link Future} for further processing.
     *
     * @param requestBody
     * @param msTimeout   timeout in milliseconds waiting until the expected response body is fetched
     * @return a {@link CompletableFuture} representing pending completion of the task containing either
     * an instance of {@link ResponseBody}, or {@code null} if no response was received because of e.g. timeout
     */
    @Nonnull
    <T extends ResponseBody> CompletableFuture<T> send(final @Nonnull RequestBody requestBody, final long msTimeout);

    /**
     * Sends a WRITE request to {@link GroupAddress} with value of {@link DataPointValue} <strong>asynchronously</strong>.
     *
     * @param address
     * @param dataPointValue
     * @return {@code true} if the write request was successful, otherwise {@code false}
     */
    boolean writeRequest(final @Nonnull GroupAddress address, final @Nonnull DataPointValue<?> dataPointValue);

    /**
     * Sends a WRITE request to {@link GroupAddress} with {@code apciData} <strong>asynchronously</strong>.
     *
     * @param address
     * @param apciData
     * @return {@code true} if the write request was successful, otherwise {@code false}
     */
    boolean writeRequest(final @Nonnull GroupAddress address, final @Nonnull byte[] apciData);

    /**
     * Sends a READ request to {@link GroupAddress} <strong>asynchronously</strong>
     *
     * @param address
     * @return {@code true} if the write request was successful, otherwise {@code false}
     */
    boolean readRequest(final @Nonnull GroupAddress address);
}
