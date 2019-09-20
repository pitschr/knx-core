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
import li.pitschmann.knx.link.Constants;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.body.RoutingIndicationBody;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.body.cemi.MessageCode;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;
import li.pitschmann.knx.link.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base KNX client implementation
 *
 * @author PITSCHR
 */
public class BaseKnxClient implements KnxClient {
    private final AtomicInteger sequence = new AtomicInteger();
    private final InternalKnxClient internalClient;

    /**
     * Starts KNX client with given configuration
     *
     * @param config
     */
    protected BaseKnxClient(final @Nonnull Configuration config) {
        internalClient = new InternalKnxClient(config);

        // notifies all plug-ins about initialization
        internalClient.notifyPlugins(this, config.getAllPlugins(), Plugin::onInitialization);
    }

    /**
     * Sends a WRITE request to {@link GroupAddress} with value of {@link DataPointValue} <strong>asynchronously</strong>.
     *
     * <strong>Note:</strong> the response is an acknowledge that request has been accepted by KNX Net/IP device. The
     * requested KNX device will send a {@link TunnelingRequestBody} with {@link MessageCode#L_DATA_CON} if write was
     * successful. It is possible only when communication and write flags are set on KNX device. A
     * {@link MessageCode#L_DATA_IND} is sent by the KNX device additionally when a transmit flag was set too.
     *
     * @param address
     * @param dataPointValue
     * @return A {@link CompletableFuture} containing {@link TunnelingAckBody} from KNX Net/IP device
     */
    @Nonnull
    public CompletableFuture<TunnelingAckBody> writeRequest(final @Nonnull GroupAddress address, final @Nonnull DataPointValue<?> dataPointValue) {
        final var cemi = CEMI.useDefaultForGroupValueWrite(address, dataPointValue);
        return this.internalClient.send(TunnelingRequestBody.of(this.internalClient.getChannelId(), this.getNextSequence(), cemi), Constants.Timeouts.DATA_REQUEST_TIMEOUT);
    }

    public void writeRouting(final @Nonnull GroupAddress address, final @Nonnull DataPointValue<?> dataPointValue) {
        final var cemi = CEMI.useDefault(MessageCode.L_DATA_IND, address, APCI.GROUP_VALUE_WRITE, dataPointValue.toByteArray());
        this.internalClient.send(RoutingIndicationBody.of(cemi));
    }

    /**
     * Sends a WRITE request to {@link GroupAddress} with {@code apciData} <strong>asynchronously</strong>.
     *
     * <strong>Note:</strong> the response is an acknowledge that request has been accepted by KNX Net/IP device. The
     * requested KNX device will send a {@link TunnelingRequestBody} with {@link MessageCode#L_DATA_CON} if write was
     * successful. It is possible only when communication and write flags are set on KNX device. A
     * {@link MessageCode#L_DATA_IND} is sent by the KNX device additionally when a transmit flag was set too.
     *
     * @param address
     * @param apciData
     * @return A {@link CompletableFuture} containing {@link TunnelingAckBody} from KNX Net/IP device
     */
    @Nonnull
    public CompletableFuture<TunnelingAckBody> writeRequest(final @Nonnull GroupAddress address, final @Nullable byte[] apciData) {
        final var cemi = CEMI.useDefaultForGroupValueWrite(address, apciData);
        return this.internalClient.send(TunnelingRequestBody.of(this.internalClient.getChannelId(), this.getNextSequence(), cemi), Constants.Timeouts.DATA_REQUEST_TIMEOUT);
    }

    /**
     * Sends a READ request to {@link GroupAddress} <strong>asynchronously</strong>
     *
     * <strong>Note:</strong> the response is an acknowledge that request has been accepted by KNX Net/IP device. The
     * requested KNX device will send a {@link TunnelingRequestBody} with {@link MessageCode#L_DATA_CON} and
     * {@link MessageCode#L_DATA_IND} if read was successful. It is possible only when communication and read flags are
     * set on KNX device.
     *
     * @param address
     * @return A {@link CompletableFuture} containing {@link TunnelingAckBody} from KNX Net/IP device
     */
    @Nonnull
    public CompletableFuture<TunnelingAckBody> readRequest(final @Nonnull GroupAddress address) {
        final var cemi = CEMI.useDefaultForGroupValueRead(address);
        return this.internalClient.send(TunnelingRequestBody.of(this.internalClient.getChannelId(), this.getNextSequence(), cemi), Constants.Timeouts.DATA_REQUEST_TIMEOUT);
    }

    /**
     * Returns the next sequence (1-octet) between 0x00 and 0xFF. After 0xFF it should start with 0x00.
     *
     * @return next sequence number
     */
    private int getNextSequence() {
        return this.sequence.getAndUpdate(v -> (v + 1) % 256);
    }

    @Nonnull
    protected InternalKnxClient getInternalClient() {
        return this.internalClient;
    }

    @Nonnull
    @Override
    public Configuration getConfig() {
        return this.internalClient.getConfig();
    }

    @Override
    public boolean isClosed() {
        return this.internalClient.isClosed();
    }

    @Nonnull
    @Override
    public KnxStatistic getStatistic() {
        return this.internalClient.getStatistic().asUnmodifiable();
    }

    @Nonnull
    @Override
    public KnxStatusPool getStatusPool() {
        return this.internalClient.getStatusPool();
    }

    @Override
    public void close() {
        this.internalClient.close();
    }

    @Override
    public void send(final @Nonnull Body body) {
        this.internalClient.send(body);
    }

    @Nonnull
    @Override
    public <T extends ResponseBody> CompletableFuture<T> send(final @Nonnull RequestBody requestBody, long timeout) {
        return this.internalClient.send(requestBody, timeout);
    }
}
