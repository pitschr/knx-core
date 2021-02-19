/*
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

package li.pitschmann.knx.core.test;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.BodyFactory;
import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ControlChannelRelated;
import li.pitschmann.knx.core.body.DataChannelRelated;
import li.pitschmann.knx.core.body.DescriptionChannelRelated;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.MulticastChannelRelated;
import li.pitschmann.knx.core.header.Header;
import li.pitschmann.knx.core.test.body.MockResponseBody;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Closeables;
import li.pitschmann.knx.core.utils.Networker;
import li.pitschmann.knx.core.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mock Server Channel for UDP ({@link DatagramChannel}) communication
 */
public final class MockServerDatagramChannel implements MockServerChannel<DatagramChannel> {
    private final static Logger log = LoggerFactory.getLogger(MockServerDatagramChannel.class);
    private final DatagramChannel channel;
    private SocketAddress clientMulticastSocketAddress;
    private SocketAddress clientDescriptionSocketAddress;
    private SocketAddress clientControlSocketAddress;
    private SocketAddress clientDataSocketAddress;

    public MockServerDatagramChannel(final MockServerTest mockServerAnnotation) {
        // as mock server is used to test locally
        final var socketOptions = Collections.singletonMap(StandardSocketOptions.IP_MULTICAST_TTL, 0);
        this.channel = Networker.newDatagramChannel(0, 3000, null, socketOptions);
    }

    @Override
    public DatagramChannel getChannel() {
        return channel;
    }

    @Override
    public int getPort() {
        return channel.socket().getLocalPort();
    }

    @Override
    public Body read(final SelectionKey key) throws IOException {
        final var byteBuffer = ByteBuffer.allocate(0xFF);
        final var address = this.channel.receive(byteBuffer);
        final var body = BodyFactory.of(byteBuffer.array());

        // store the last address, if not available - simply cancel this method
        if (address != null) {
            key.attach(address);
        }

        // update client multicast address if multicast related packet is received
        if (body instanceof MulticastChannelRelated) {
            this.clientMulticastSocketAddress = address;
            log.debug("Multicast Address: {}", this.clientMulticastSocketAddress);
        }
        // update client description address if DescriptionRequestBody from client is received
        else if (body instanceof DescriptionRequestBody) {
            this.clientDescriptionSocketAddress = address;
            log.debug("Description Address: {}", this.clientDescriptionSocketAddress);
        }
        // update client control/data addresses if ConnectRequestBody from client is received
        else if (body instanceof ConnectRequestBody) {
            final var connectRequestBody = (ConnectRequestBody) body;
            // fetch the control and data ports
            final var clientControlHPAI = connectRequestBody.getControlEndpoint();
            final var clientDataHPAI = connectRequestBody.getDataEndpoint();

            // NAT?
            if (clientControlHPAI.getAddress().isAnyLocalAddress()) {
                // NAT is used
                this.clientControlSocketAddress = address;
                this.clientDataSocketAddress = address;
            } else {
                // NAT is not used
                this.clientControlSocketAddress = new InetSocketAddress(clientControlHPAI.getAddress(), clientControlHPAI.getPort());
                this.clientDataSocketAddress = new InetSocketAddress(clientDataHPAI.getAddress(), clientDataHPAI.getPort());
            }
            log.debug("Control Address: {}", this.clientControlSocketAddress);
            log.debug("Data Address   : {}", this.clientDataSocketAddress);
        }

        return body;
    }

    @Override
    public void send(final SelectionKey key, final Body body) throws IOException {
        // packet: header + body
        final ByteBuffer byteBuffer;
        if (body instanceof MockResponseBody) {
            // mock body contains complete byte array (header + body)
            byteBuffer = ByteBuffer.wrap(body.toByteArray());
        } else {
            // OK
            final var headerRawData = Header.of(body).toByteArray();
            final var bodyRawData = body.toByteArray();
            byteBuffer = ByteBuffer.wrap(Bytes.concat(headerRawData, bodyRawData));
        }

        // choose address based on body channel-relation
        final SocketAddress address;
        if (body instanceof MulticastChannelRelated) {
            address = this.clientMulticastSocketAddress;
        } else if (body instanceof DescriptionChannelRelated) {
            address = this.clientDescriptionSocketAddress;
        } else if (body instanceof ControlChannelRelated) {
            address = this.clientControlSocketAddress;
        } else if (body instanceof DataChannelRelated) {
            address = this.clientDataSocketAddress;
        } else {
            // otherwise just use the sender form previous received diagram
            address = (SocketAddress) key.attachment();
        }

        this.channel.send(byteBuffer, address);
        System.out.println("PITSCHR (" + System.currentTimeMillis() + "): Mock Server sent: " + sent.incrementAndGet());
        Sleeper.milliseconds(1);
    }

    private static final AtomicInteger sent = new AtomicInteger(0);

    @Override
    public void close() {
        Closeables.closeQuietly(this.channel);
    }
}
