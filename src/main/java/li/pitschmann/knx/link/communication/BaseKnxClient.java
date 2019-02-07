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
import li.pitschmann.knx.link.body.address.*;
import li.pitschmann.knx.link.body.cemi.*;
import li.pitschmann.knx.link.datapoint.value.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Base KNX client implementation
 *
 * @author PITSCHR
 */
public class BaseKnxClient implements KnxClient {
    private final AtomicInteger sequence = new AtomicInteger();
    private final InternalKnxClient clientInternal;

    /**
     * Starts KNX client with given configuration
     *
     * @param config
     */
    public BaseKnxClient(final Configuration config) {
        clientInternal = new InternalKnxClient(config);

        // start services for KNX communication
        clientInternal.start();
    }

    /**
     * Sends a WRITE request to {@link GroupAddress} with value of {@link DataPointValue} <strong>asynchronously</strong>.
     *
     * <strong>Note:</strong> the response is an acknowledge that request has been accepted by KNX Net/IP router. The
     * requested KNX device will send a {@link TunnellingRequestBody} with {@link MessageCode#L_DATA_CON} if write was
     * successful. It is possible only when communication and write flags are set on KNX device. A
     * {@link MessageCode#L_DATA_IND} is sent by the KNX device additionally when a transmit flag was set too.
     *
     * @param address
     * @param dataPointValue
     * @return A {@link Future} containing {@link TunnellingAckBody} from KNX Net/IP router
     */
    public Future<TunnellingAckBody> writeRequestAsync(final GroupAddress address, final DataPointValue<?> dataPointValue) {
        final CEMI cemi = CEMI.useDefaultForGroupValueWrite(address, dataPointValue);
        return this.clientInternal.send(TunnellingRequestBody.create(this.clientInternal.getChannelId(), this.getNextSequence(), cemi), Constants.Timeouts.DATA_REQUEST_TIMEOUT);
    }

    /**
     * Sends a WRITE request to {@link GroupAddress} with {@code apciData} byte array <strong>asynchronously</strong>.
     *
     * <strong>Note:</strong> the response is an acknowledge that request has been accepted by KNX Net/IP router. The
     * requested KNX device will send a {@link TunnellingRequestBody} with {@link MessageCode#L_DATA_CON} if write was
     * successful. It is possible only when communication and write flags are set on KNX device. A
     * {@link MessageCode#L_DATA_IND} is sent by the KNX device additionally when a transmit flag was set too.
     *
     * @param address
     * @param apciData
     * @return A {@link Future} containing {@link TunnellingAckBody} from KNX Net/IP router
     */
    public Future<TunnellingAckBody> writeRequestAsync(final GroupAddress address, final byte[] apciData) {
        final CEMI cemi = CEMI.useDefaultForGroupValueWrite(address, apciData);
        return this.clientInternal.send(TunnellingRequestBody.create(this.clientInternal.getChannelId(), this.getNextSequence(), cemi), Constants.Timeouts.DATA_REQUEST_TIMEOUT);
    }

    /**
     * Sends a READ request to {@link GroupAddress} <strong>asynchronously</strong>
     *
     * <strong>Note:</strong> the response is an acknowledge that request has been accepted by KNX Net/IP router. The
     * requested KNX device will send a {@link TunnellingRequestBody} with {@link MessageCode#L_DATA_CON} and
     * {@link MessageCode#L_DATA_IND} if read was successful. It is possible only when communication and read flags are
     * set on KNX device.
     *
     * @param address
     * @return A {@link Future} containing {@link TunnellingAckBody} from KNX Net/IP router
     */
    public Future<TunnellingAckBody> readRequestAsync(final GroupAddress address) {
        final CEMI cemi = CEMI.useDefaultForGroupValueRead(address);
        return this.clientInternal.send(TunnellingRequestBody.create(this.clientInternal.getChannelId(), this.getNextSequence(), cemi), Constants.Timeouts.DATA_REQUEST_TIMEOUT);
    }

    /**
     * Returns the next sequence (1-octet) between 0x00 and 0xFF. After 0xFF it should start with 0x00.
     *
     * @return next sequence number
     */
    private int getNextSequence() {
        return this.sequence.getAndUpdate(v -> (v + 1) % 0xFF);
    }

    @Override
    public Configuration getConfig() {
        return this.clientInternal.getConfig();
    }

    @Override
    public boolean isClosed() {
        return this.clientInternal.isClosed();
    }

    @Override
    public KnxStatistic getStatistic() {
        return this.clientInternal.getStatistic();
    }

    public KnxStatusPool getStatusPool() {
        return this.clientInternal.getStatusPool();
    }

    @Override
    public void close() {
        this.clientInternal.close();
    }

    @Override
    public void send(Body body) {
        this.clientInternal.send(body);
    }

    @Override
    public <T extends ResponseBody> Future<T> send(RequestBody requestBody, long timeout) {
        return this.clientInternal.send(requestBody, timeout);
    }
}
