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
import li.pitschmann.knx.core.body.Status;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.body.cemi.APCI;
import li.pitschmann.knx.core.body.cemi.MessageCode;
import li.pitschmann.knx.core.communication.InternalKnxClient;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Observes the {@link TunnelingRequestBody} which is received from KNX Net/IP device on data channel.
 *
 * @author PITSCHR
 */
public final class TunnelingRequestTask implements Subscriber<Body> {
    private static final Logger log = LoggerFactory.getLogger(TunnelingRequestTask.class);
    private final InternalKnxClient client;

    public TunnelingRequestTask(final InternalKnxClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public void onNext(final @Nullable Body body) {
        // we are interested in tunneling request only
        if (body instanceof TunnelingRequestBody) {
            log.debug("Tunneling Request received: {}", body);

            // acknowledge frame to be sent back
            final var reqBody = (TunnelingRequestBody) body;
            final var ackBody = TunnelingAckBody.of(this.client.getChannelId(), reqBody.getSequence(), Status.E_NO_ERROR);

            // send acknowledge frame
            this.client.send(ackBody);

            // Consider only:
            // 1) Indication + Group Value Write
            // 2) Indication + Group Value Response
            // 3) Confirmation + Group Value Write
            // rest are ignored
            final var cemi = reqBody.getCEMI();
            final var messageCode = cemi.getMessageCode();
            final var apci = cemi.getApci();
            if (messageCode == MessageCode.L_DATA_IND) {
                if (apci == APCI.GROUP_VALUE_WRITE) {
                    if (log.isDebugEnabled()) {
                        log.debug("TunnelingRequest frame received from KNX after WRITE request from a remote KNX device: Source={}, Destination={}, Data={}", cemi.getSourceAddress().getAddress(), cemi.getDestinationAddress().getAddress(), ByteFormatter.formatHexAsString(cemi.getApciData()));
                    }
                    this.client.getStatusPool().updateStatus(cemi);
                } else if (apci == APCI.GROUP_VALUE_RESPONSE) {
                    if (log.isDebugEnabled()) {
                        log.debug("TunnelingRequest frame received from KNX after READ request: Source={}, Destination={}, Data={}", cemi.getSourceAddress().getAddress(), cemi.getDestinationAddress().getAddress(), ByteFormatter.formatHexAsString(cemi.getApciData()));
                    }
                    this.client.getStatusPool().updateStatus(cemi);
                } else {
                    log.debug("TunnelingRequest frame received but ignored: with MessageCode={}, APCI={}", messageCode, apci);
                }
            } else if (messageCode == MessageCode.L_DATA_CON && apci == APCI.GROUP_VALUE_WRITE) {
                if (log.isDebugEnabled()) {
                    log.debug("TunnelingRequest frame received from KNX after WRITE request from KNX client: Source={}, Destination={}, Data={}", cemi.getSourceAddress().getAddress(), cemi.getDestinationAddress().getAddress(), ByteFormatter.formatHexAsString(cemi.getApciData()));
                }
                this.client.getStatusPool().updateStatus(cemi);
            } else {
                log.debug("TunnelingRequest frame received but ignored: with MessageCode={}, APCI={}", messageCode, apci);
            }
        }
    }

    @Override
    public void onError(final @Nullable Throwable throwable) {
        log.error("Error during Tunneling Request Task class", throwable);
    }

    @Override
    public void onComplete() {
        // NO-OP
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }
}
