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

package li.pitschmann.knx.link.communication.task;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.body.TunnellingRequestBody;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.body.cemi.MessageCode;
import li.pitschmann.knx.link.communication.InternalKnxClient;
import li.pitschmann.utils.ByteFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Observes the {@link TunnellingRequestBody} which is received from KNX Net/IP Router on data channel.
 *
 * @author PITSCHR
 */
public final class TunnellingRequestTask implements Subscriber<Body> {
    private static final Logger LOG = LoggerFactory.getLogger(TunnellingRequestTask.class);
    private final InternalKnxClient client;

    public TunnellingRequestTask(InternalKnxClient client) {
        this.client = client;
    }

    @Override
    public void onNext(Body body) {
        // we are interested in tunneling request only
        if (body instanceof TunnellingRequestBody) {
            LOG.debug("Tunnelling Request received: {}", body);

            // acknowledge frame to be sent back
            final TunnellingRequestBody reqBody = (TunnellingRequestBody) body;
            final TunnellingAckBody ackBody = TunnellingAckBody.create(this.client.getChannelId(), reqBody.getSequence(), Status.E_NO_ERROR);

            // send acknowledge frame
            this.client.send(ackBody);

            // if it is indication, update the state of source address
            final CEMI cemi = reqBody.getCEMI();

            // Consider only:
            // 1) Indication + Group Value Response
            // 2) Indication + Group Value Write
            // 3) Consider Confirmation + Group Value Write
            // rest are ignored
            if (cemi.getMessageCode() == MessageCode.L_DATA_IND) {
                if (cemi.getApci() == APCI.GROUP_VALUE_WRITE) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("TunnellingRequest frame received from KNX after WRITE request from a remote KNX device: Source={}, Destination={}, Data={}", cemi.getSourceAddress().getAddress(), cemi.getDestinationAddress().getAddress(), ByteFormatter.formatHexAsString(cemi.getApciData()));
                    }
                    this.client.getStatusPool().updateStatus(cemi);
                } else if (cemi.getApci() == APCI.GROUP_VALUE_RESPONSE) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("TunnellingRequest frame received from KNX after READ request: Source={}, Destination={}, Data={}", cemi.getSourceAddress().getAddress(), cemi.getDestinationAddress().getAddress(), ByteFormatter.formatHexAsString(cemi.getApciData()));
                    }
                    this.client.getStatusPool().updateStatus(cemi);
                }
            } else if (cemi.getMessageCode() == MessageCode.L_DATA_CON && cemi.getApci() == APCI.GROUP_VALUE_WRITE) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("TunnellingRequest frame received from KNX after WRITE request from KNX client: Source={}, Destination={}, Data={}", cemi.getSourceAddress().getAddress(), cemi.getDestinationAddress().getAddress(), ByteFormatter.formatHexAsString(cemi.getApciData()));
                }
                this.client.getStatusPool().updateStatus(cemi);
            }
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        LOG.error("Error during Tunnelling Request Task class", throwable);
    }

    @Override
    public void onComplete() {
        // NO-OP
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }
}
