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
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.body.cemi.MessageCode;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;
import li.pitschmann.knx.link.plugin.Plugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
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
    protected BaseKnxClient(final Configuration config) {
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
     * @return A {@link Future} containing {@link TunnelingAckBody} from KNX Net/IP device
     */
    public Future<TunnelingAckBody> writeRequest(final GroupAddress address, final DataPointValue<?> dataPointValue) {
        final var cemi = CEMI.useDefaultForGroupValueWrite(address, dataPointValue);
        return this.internalClient.send(TunnelingRequestBody.create(this.internalClient.getChannelId(), this.getNextSequence(), cemi), Constants.Timeouts.DATA_REQUEST_TIMEOUT);
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
     * @return A {@link Future} containing {@link TunnelingAckBody} from KNX Net/IP device
     */
    public Future<TunnelingAckBody> writeRequest(final GroupAddress address, final byte[] apciData) {
        final var cemi = CEMI.useDefaultForGroupValueWrite(address, apciData);
        return this.internalClient.send(TunnelingRequestBody.create(this.internalClient.getChannelId(), this.getNextSequence(), cemi), Constants.Timeouts.DATA_REQUEST_TIMEOUT);
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
     * @return A {@link Future} containing {@link TunnelingAckBody} from KNX Net/IP device
     */
    public Future<TunnelingAckBody> readRequest(final GroupAddress address) {
        final var cemi = CEMI.useDefaultForGroupValueRead(address);
        return this.internalClient.send(TunnelingRequestBody.create(this.internalClient.getChannelId(), this.getNextSequence(), cemi), Constants.Timeouts.DATA_REQUEST_TIMEOUT);
    }

    /**
     * Returns the next sequence (1-octet) between 0x00 and 0xFF. After 0xFF it should start with 0x00.
     *
     * @return next sequence number
     */
    private int getNextSequence() {
        return this.sequence.getAndUpdate(v -> (v + 1) % 256);
    }

    protected InternalKnxClient getInternalClient() {
        return this.internalClient;
    }

    @Override
    public Configuration getConfig() {
        return this.internalClient.getConfig();
    }

    @Override
    public boolean isClosed() {
        return this.internalClient.isClosed();
    }

    @Override
    public KnxStatistic getStatistic() {
        return this.internalClient.getStatistic().asUnmodifiable();
    }

    @Override
    public KnxStatusPool getStatusPool() {
        return this.internalClient.getStatusPool();
    }

    @Override
    public void close() {
        this.internalClient.close();
    }

    @Override
    public void send(Body body) {
        this.internalClient.send(body);
    }

    @Override
    public <T extends ResponseBody> CompletableFuture<T> send(RequestBody requestBody, long timeout) {
        return this.internalClient.send(requestBody, timeout);
    }
}
