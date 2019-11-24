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

package li.pitschmann.knx.core.communication.task;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.body.cemi.APCI;
import li.pitschmann.knx.core.body.cemi.MessageCode;
import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Observes the {@link RoutingIndicationTask} which is sent to or
 * received from KNX Net/IP device on multicast channel
 *
 * @author PITSCHR
 */
public final class RoutingIndicationTask implements Subscriber<Body> {
    private static final Logger log = LoggerFactory.getLogger(RoutingIndicationTask.class);
    private final InternalKnxClient client;

    public RoutingIndicationTask(final @Nonnull InternalKnxClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public void onNext(final @Nullable Body body) {
        // we are interested in routing indication only
        if (body instanceof RoutingIndicationBody) {
            log.debug("Routing Indication received: {}", body);

            // Consider only:
            // 1) Indication + Group Value Write
            // 2) Indication + Group Value Response
            // rest are ignored
            final var reqBody = (RoutingIndicationBody) body;
            final var cemi = reqBody.getCEMI();
            final var messageCode = cemi.getMessageCode();
            final var apci = cemi.getApci();
            if (cemi.getMessageCode() == MessageCode.L_DATA_IND) {
                if (cemi.getApci() == APCI.GROUP_VALUE_WRITE) {
                    if (log.isDebugEnabled()) {
                        log.debug("RoutingIndication frame received from KNX after WRITE request from a remote KNX device: Source={}, Destination={}, Data={}", cemi.getSourceAddress().getAddress(), cemi.getDestinationAddress().getAddress(), ByteFormatter.formatHexAsString(cemi.getApciData()));
                    }
                    this.client.getStatusPool().updateStatus(cemi);
                } else if (cemi.getApci() == APCI.GROUP_VALUE_RESPONSE) {
                    if (log.isDebugEnabled()) {
                        log.debug("RoutingIndication frame received from KNX after READ request: Source={}, Destination={}, Data={}", cemi.getSourceAddress().getAddress(), cemi.getDestinationAddress().getAddress(), ByteFormatter.formatHexAsString(cemi.getApciData()));
                    }
                    this.client.getStatusPool().updateStatus(cemi);
                } else {
                    log.debug("RoutingIndication frame received but ignored: with MessageCode={}, APCI={}", messageCode, apci);
                }
            } else {
                log.debug("RoutingIndication frame received but ignored: MessageCode={}", messageCode);
            }
        }
    }

    @Override
    public void onError(final @Nullable Throwable throwable) {
        log.error("Error during Routing Indication Task class", throwable);
    }

    @Override
    public void onComplete() {
        // NO-OP
    }

    @Override
    public void onSubscribe(final @Nonnull Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }
}
