/*
 * Copyright (C) 2021 Pitschmann Christoph
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

package li.pitschmann.knx.core.communication;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.ControlChannelRelated;
import li.pitschmann.knx.core.body.DataChannelRelated;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.config.Config;
import li.pitschmann.knx.core.config.ConfigValue;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;

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
     * Returns the {@link Config} used by KNX client
     *
     * @return an immutable {@link Config}
     */
    Config getConfig();

    /**
     * Handy method to get the value of given {@link ConfigValue} via {@link #getConfig()}
     *
     * @param configValue configuration value
     * @param <T>         type of configuration (e.g. Boolean)
     * @return the config value
     */
    default <T> T getConfig(final ConfigValue<T> configValue) {
        return getConfig().getValue(configValue);
    }

    /**
     * Returns a copied {@link KnxStatistic} snapshot about the KNX communication
     *
     * @return an immutable {@link KnxStatistic}
     */
    KnxStatistic getStatistic();

    /**
     * Returns a copied {@link KnxStatusPool} snapshot about current status of KNX devices
     *
     * @return an immutable {@link KnxStatusPool}
     */
    KnxStatusPool getStatusPool();

    /**
     * Returns {@code true} if the KNX Client is actively communicating with the KNX Net/IP device.
     *
     * @return {@code true} if the client is running
     */
    boolean isRunning();

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
     * Send {@link RequestBody} packet <strong>asynchronously</strong> to the appropriate channel.
     * It returns a {@link Future} for further processing.
     *
     * @param requestBody request body to be sent to KNX Net/IP device
     * @param msTimeout   timeout in milliseconds waiting until the expected response body is fetched
     * @param <T>         type of response body
     * @return a {@link CompletableFuture} representing pending completion of the task containing either
     * an instance of {@link ResponseBody}, or {@code null} if no response was received because of e.g. timeout
     */

    <T extends ResponseBody> CompletableFuture<T> send(final RequestBody requestBody, final long msTimeout);

    /**
     * Sends a WRITE request to {@link GroupAddress} with value of {@link DataPointValue} asynchronously.
     *
     * @param address        the recipient which is an KNX group address
     * @param dataPointValue value to be sent to KNX group address
     * @return a {@link CompletableFuture}, if the write request was successful it will contain a {@code true}, otherwise {@code false}
     */
    CompletableFuture<Boolean> writeRequest(final GroupAddress address, final DataPointValue dataPointValue);

    /**
     * Sends a WRITE request to {@link GroupAddress} with a value of {@link DataPointValue} and wait
     * for acknowledge packet from KNX Net/IP device. This method is <strong>blocking</strong>, for
     * non-blocking operation use {@link #writeRequest(GroupAddress, DataPointValue)}. Using {@code timeout}
     * we can define the maximum time we want to wait for.
     *
     * @param address        the recipient which is an KNX group address
     * @param dataPointValue value to be sent to KNX group address
     * @param timeout        timeout in milliseconds
     * @return {@code true} if the acknowledge for write request was successful within expected time frame, otherwise {@code false}
     */
    boolean writeRequest(final GroupAddress address, final DataPointValue dataPointValue, final long timeout);

    /**
     * Sends a READ request to {@link GroupAddress} asynchronously.
     *
     * @param address this is the KNX group address we want to send the read request
     * @return a {@link CompletableFuture}, if the read request was successful it will contain a {@code true}, otherwise {@code false}
     */
    CompletableFuture<Boolean> readRequest(final GroupAddress address);

    /**
     * Sends a READ request to {@link GroupAddress} and wait for acknowledge frame.
     * This method is <strong>blocking</strong>, for non-blocking operation use
     * {@link #readRequest(GroupAddress)}. Using {@code timeout} we can define the
     * maximum time we want to wait for.
     *
     * @param address this is the KNX group address we want to send the read request
     * @param timeout timeout in milliseconds
     * @return {@code true} if the acknowledge for read request was successful within expected time frame, otherwise {@code false}
     */
    boolean readRequest(final GroupAddress address, final long timeout);
}
