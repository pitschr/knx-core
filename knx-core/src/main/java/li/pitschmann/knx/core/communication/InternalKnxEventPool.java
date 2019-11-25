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

package li.pitschmann.knx.core.communication;

import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectResponseBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DescriptionResponseBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.ResponseBody;
import li.pitschmann.knx.core.body.SearchRequestBody;
import li.pitschmann.knx.core.body.SearchResponseBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.communication.event.KnxEvent;
import li.pitschmann.knx.core.communication.event.KnxMultiEvent;
import li.pitschmann.knx.core.communication.event.KnxSingleEvent;
import li.pitschmann.knx.core.utils.Maps;
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
public final class InternalKnxEventPool {
    private static final Logger log = LoggerFactory.getLogger(InternalKnxEventPool.class);
    private static final int DEFAULT_TUNNELING_REQUEST_CAPACITY = 0xFF + 1; // 1 byte only according to KNX specification
    private final Map<Integer, KnxSingleEvent<TunnelingRequestBody, TunnelingAckBody>> tunnelingMap;
    private final KnxMultiEvent<SearchRequestBody, SearchResponseBody> searchEvent = new KnxMultiEvent<>();
    private final KnxSingleEvent<DescriptionRequestBody, DescriptionResponseBody> descriptionEvent = new KnxSingleEvent<>();
    private final KnxSingleEvent<ConnectRequestBody, ConnectResponseBody> connectEvent = new KnxSingleEvent<>();
    private final KnxSingleEvent<ConnectionStateRequestBody, ConnectionStateResponseBody> connectionStateEvent = new KnxSingleEvent<>();
    private final KnxSingleEvent<DisconnectRequestBody, DisconnectResponseBody> disconnectEvent = new KnxSingleEvent<>();

    /**
     * KNX event pool (package protected)
     */
    InternalKnxEventPool() {
        // initialize tunneling map and fill with default KnxSingleEvent entries (we will need it anyway)
        tunnelingMap = Maps.newHashMap(DEFAULT_TUNNELING_REQUEST_CAPACITY);
        for (var i = 0; i < DEFAULT_TUNNELING_REQUEST_CAPACITY; i++) {
            tunnelingMap.put(i, new KnxSingleEvent<>());
        }
        log.trace("Internal KNX Event Pool object created.");
    }

    /**
     * Returns list of {@link KnxMultiEvent} for single search request and multiple responses
     *
     * @return {@link KnxMultiEvent}
     */
    @Nonnull
    public KnxMultiEvent<SearchRequestBody, SearchResponseBody> searchEvent() {
        return this.searchEvent;
    }

    /**
     * Returns {@link KnxSingleEvent} for description request and response
     *
     * @return {@link KnxSingleEvent}
     */
    @Nonnull
    public KnxSingleEvent<DescriptionRequestBody, DescriptionResponseBody> descriptionEvent() {
        return this.descriptionEvent;
    }

    /**
     * Returns {@link KnxSingleEvent} for connect request and response
     *
     * @return {@link KnxSingleEvent}
     */
    @Nonnull
    public KnxSingleEvent<ConnectRequestBody, ConnectResponseBody> connectEvent() {
        return this.connectEvent;
    }

    /**
     * Returns {@link KnxSingleEvent} for connection state request and response
     *
     * @return {@link KnxSingleEvent}
     */
    @Nonnull
    public KnxSingleEvent<ConnectionStateRequestBody, ConnectionStateResponseBody> connectionStateEvent() {
        return this.connectionStateEvent;
    }

    /**
     * Returns {@link KnxSingleEvent} for disconnect request and response
     *
     * @return {@link KnxSingleEvent}
     */
    @Nonnull
    public KnxSingleEvent<DisconnectRequestBody, DisconnectResponseBody> disconnectEvent() {
        return this.disconnectEvent;
    }

    /**
     * Adds {@link RequestBody} to the event pool
     *
     * @param request
     */
    public void add(final @Nonnull RequestBody request) {
        get(request).setRequest(request);
    }

    /**
     * Returns the {@link KnxSingleEvent} for given {@link RequestBody} from event pool
     *
     * @param request
     * @return {@link KnxSingleEvent} or {@code IllegalArgumentException} if not supported
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public <REQUEST extends RequestBody, RESPONSE extends ResponseBody> KnxEvent<REQUEST, RESPONSE> get(final @Nonnull REQUEST request) {
        if (request instanceof TunnelingRequestBody) {
            return (KnxSingleEvent<REQUEST, RESPONSE>) this.tunnelingMap.get(((TunnelingRequestBody) request).getSequence());
        } else if (request instanceof ConnectionStateRequestBody) {
            return (KnxSingleEvent<REQUEST, RESPONSE>) this.connectionStateEvent;
        } else if (request instanceof DisconnectRequestBody) {
            return (KnxSingleEvent<REQUEST, RESPONSE>) this.disconnectEvent;
        } else if (request instanceof ConnectRequestBody) {
            return (KnxSingleEvent<REQUEST, RESPONSE>) this.connectEvent;
        } else if (request instanceof DescriptionRequestBody) {
            return (KnxSingleEvent<REQUEST, RESPONSE>) this.descriptionEvent;
        } else if (request instanceof SearchRequestBody) {
            return (KnxMultiEvent<REQUEST, RESPONSE>) this.searchEvent;
        }
        log.error("Request body is not supported for 'get(RequestBody)' method: {}", request);
        throw new IllegalArgumentException("Request body is not supported.");
    }

    /**
     * Returns the {@link KnxSingleEvent} for given {@link TunnelingAckBody} from event pool
     *
     * @param acknowledge
     * @return {@link KnxSingleEvent}
     */
    @Nonnull
    public KnxSingleEvent<TunnelingRequestBody, TunnelingAckBody> get(final @Nonnull TunnelingAckBody acknowledge) {
        return this.tunnelingMap.get(acknowledge.getSequence());
    }
}
