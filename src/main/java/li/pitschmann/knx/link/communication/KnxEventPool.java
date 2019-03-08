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

import com.google.common.collect.Maps;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.ResponseBody;
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.body.TunnellingRequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * KNX event pool for communication.
 * <p/>
 * This class is also indented to find the appropriate request and response bodies.
 *
 * @author PITSCHR
 */
public final class KnxEventPool {
    private static final Logger LOG = LoggerFactory.getLogger(KnxEventPool.class);
    private static final int DEFAULT_TUNNELLING_REQUEST_CAPACITY = 0xFF; // 1 byte only according to KNX specification
    private final Map<Integer, KnxEventData<TunnellingRequestBody, TunnellingAckBody>> tunnellingMap;
    private final KnxEventData<DescriptionRequestBody, DescriptionResponseBody> descriptionEvent = new KnxEventData<>();
    private final KnxEventData<ConnectRequestBody, ConnectResponseBody> connectEvent = new KnxEventData<>();
    private final KnxEventData<ConnectionStateRequestBody, ConnectionStateResponseBody> connectionStateEvent = new KnxEventData<>();
    private final KnxEventData<DisconnectRequestBody, DisconnectResponseBody> disconnectEvent = new KnxEventData<>();

    public KnxEventPool() {
        // initialize tunnelling map and fill with default KnxEventData entries (we will need it anyway)
        tunnellingMap = Maps.newHashMapWithExpectedSize(DEFAULT_TUNNELLING_REQUEST_CAPACITY);
        for (var i = 0; i < DEFAULT_TUNNELLING_REQUEST_CAPACITY; i++) {
            tunnellingMap.put(i, new KnxEventData<>());
        }
    }

    /**
     * Returns {@link KnxEventData} for description request and response
     *
     * @return {@link KnxEventData}
     */
    public KnxEventData<DescriptionRequestBody, DescriptionResponseBody> descriptionEvent() {
        return this.descriptionEvent;
    }

    /**
     * Returns {@link KnxEventData} for connect request and response
     *
     * @return {@link KnxEventData}
     */
    public KnxEventData<ConnectRequestBody, ConnectResponseBody> connectEvent() {
        return this.connectEvent;
    }

    /**
     * Returns {@link KnxEventData} for connection state request and response
     *
     * @return {@link KnxEventData}
     */
    public KnxEventData<ConnectionStateRequestBody, ConnectionStateResponseBody> connectionStateEvent() {
        return this.connectionStateEvent;
    }

    /**
     * Returns {@link KnxEventData} for disconnect request and response
     *
     * @return {@link KnxEventData}
     */
    public KnxEventData<DisconnectRequestBody, DisconnectResponseBody> disconnectEvent() {
        return this.disconnectEvent;
    }

    /**
     * Adds {@link RequestBody} to the event pool
     *
     * @param request
     */
    public void add(final RequestBody request) {
        get(request).setRequest(request);
    }

    /**
     * Returns the {@link KnxEventData} for given {@link RequestBody} from event pool
     *
     * @param request
     * @return {@link KnxEventData} or {@code IllegalArgumentException} if not supported
     */
    @SuppressWarnings("unchecked")
    public @Nonnull
    <REQUEST extends RequestBody, RESPONSE extends ResponseBody> KnxEventData<REQUEST, RESPONSE> get(final REQUEST request) {
        if (request instanceof TunnellingRequestBody) {
            return (KnxEventData<REQUEST, RESPONSE>) this.tunnellingMap.get(((TunnellingRequestBody) request).getSequence());
        } else if (request instanceof ConnectionStateRequestBody) {
            return (KnxEventData<REQUEST, RESPONSE>) this.connectionStateEvent;
        } else if (request instanceof DisconnectRequestBody) {
            return (KnxEventData<REQUEST, RESPONSE>) this.disconnectEvent;
        } else if (request instanceof ConnectRequestBody) {
            return (KnxEventData<REQUEST, RESPONSE>) this.connectEvent;
        } else if (request instanceof DescriptionRequestBody) {
            return (KnxEventData<REQUEST, RESPONSE>) this.descriptionEvent;
        }
        LOG.error("Request body is not supported for 'get(RequestBody)' method: {}", request);
        throw new IllegalArgumentException("Request body is not supported.");
    }

    /**
     * Returns the {@link KnxEventData} for given {@link TunnellingAckBody} from event pool
     *
     * @param acknowledge
     * @return {@link KnxEventData}
     */
    public @Nonnull
    KnxEventData<TunnellingRequestBody, TunnellingAckBody> get(final TunnellingAckBody acknowledge) {
        return this.tunnellingMap.get(acknowledge.getSequence());
    }
}
