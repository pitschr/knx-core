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

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.body.RoutingIndicationBody;
import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.body.cemi.MessageCode;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.link.config.ConfigConstants;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;
import li.pitschmann.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base KNX client implementation
 *
 * @author PITSCHR
 */
public class BaseKnxClient implements KnxClient {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final AtomicInteger sequence = new AtomicInteger();
    private final InternalKnxClient internalClient;

    /**
     * Starts KNX client with given config
     *
     * @param config
     */
    protected BaseKnxClient(final @Nonnull Config config) {
        internalClient = new InternalKnxClient(config);

        // notifies all plug-ins about initialization
        // here we want to pass the BaseKnxClient to plugin manager - not the InternalKnxClient!
        internalClient.getPluginManager().notifyInitialization(this);
    }

    @Override
    public boolean writeRequest(final @Nonnull GroupAddress address, final @Nonnull DataPointValue<?> dataPointValue) {
        return writeRequest(address, dataPointValue.toByteArray());
    }

    @Override
    public boolean writeRequest(final @Nonnull GroupAddress address, final @Nonnull byte[] apciData) {
        Preconditions.checkNonNull(address);
        Preconditions.checkNonNull(apciData);
        Preconditions.checkState(isRunning());

        if (getConfig().isRoutingEnabled()) {
            // routing request
            final var cemi = CEMI.useDefault(MessageCode.L_DATA_IND, address, APCI.GROUP_VALUE_WRITE, apciData);
            getInternalClient().send(RoutingIndicationBody.of(cemi));
            return true; // unconfirmed
        } else {
            // tunneling request
            try {
                final var cemi = CEMI.useDefault(MessageCode.L_DATA_REQ, address, APCI.GROUP_VALUE_WRITE, apciData);
                final var ackBody = getInternalClient().<TunnelingAckBody>send(TunnelingRequestBody.of(getInternalClient().getChannelId(), this.getNextSequence(), cemi), getConfig(ConfigConstants.Data.DATA_REQUEST_TIMEOUT)).get();
                return ackBody.getStatus() == Status.E_NO_ERROR;
            } catch (final ExecutionException ex) {
                log.warn("Exception during write request for tunneling", ex);
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    @Override
    public boolean readRequest(final @Nonnull GroupAddress address) {
        Preconditions.checkNonNull(address);
        Preconditions.checkState(isRunning());

        if (getConfig().isRoutingEnabled()) {
            final var cemi = CEMI.useDefault(MessageCode.L_DATA_IND, address, APCI.GROUP_VALUE_READ, (byte[]) null);
            getInternalClient().send(RoutingIndicationBody.of(cemi));
            return true; // unconfirmed
        } else {
            try {
                final var cemi = CEMI.useDefault(MessageCode.L_DATA_REQ, address, APCI.GROUP_VALUE_READ, (byte[]) null);
                final var ackBody = getInternalClient().<TunnelingAckBody>send(TunnelingRequestBody.of(getInternalClient().getChannelId(), this.getNextSequence(), cemi), getConfig(ConfigConstants.Data.DATA_REQUEST_TIMEOUT)).get();
                return ackBody.getStatus() == Status.E_NO_ERROR;
            } catch (final ExecutionException ex) {
                log.warn("Exception during read response for tunneling", ex);
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
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
    public Config getConfig() {
        return getInternalClient().getConfig();
    }

    @Override
    public boolean isRunning() {
        return getInternalClient().getState() == InternalKnxClient.State.STARTED;
    }

    @Nonnull
    @Override
    public KnxStatistic getStatistic() {
        return getInternalClient().getStatistic().asUnmodifiable();
    }

    @Nonnull
    @Override
    public KnxStatusPool getStatusPool() {
        return getInternalClient().getStatusPool();
    }

    @Override
    public void close() {
        getInternalClient().close();
    }

    @Override
    public void send(final @Nonnull Body body) {
        getInternalClient().send(body);
    }

    @Nonnull
    @Override
    public <T extends ResponseBody> CompletableFuture<T> send(final @Nonnull RequestBody requestBody, long timeout) {
        return getInternalClient().send(requestBody, timeout);
    }
}
