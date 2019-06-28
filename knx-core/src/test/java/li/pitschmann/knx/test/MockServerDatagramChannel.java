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

package li.pitschmann.knx.test;

import com.google.common.primitives.Bytes;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.BodyFactory;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ControlChannelRelated;
import li.pitschmann.knx.link.body.DataChannelRelated;
import li.pitschmann.knx.link.body.DescriptionChannelRelated;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.communication.ChannelFactory;
import li.pitschmann.knx.link.header.Header;
import li.pitschmann.utils.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

/**
 * Mock Server Channel for UDP ({@link DatagramChannel}) communication
 */
public final class MockServerDatagramChannel implements MockServerChannel<DatagramChannel> {
    private final static Logger logger = LoggerFactory.getLogger(MockServerDatagramChannel.class);
    private final DatagramChannel channel;
    private SocketAddress clientDescriptionSocketAddress;
    private SocketAddress clientControlSocketAddress;
    private SocketAddress clientDataSocketAddress;
    private boolean useNAT;

    public MockServerDatagramChannel() {
        this.channel = ChannelFactory.newDatagramChannel(3000);
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
        final var body = BodyFactory.valueOf(byteBuffer.array());

        // store the last address, if not available - simply cancel this method
        if (address != null) {
            key.attach(address);
        }

        // update client description address if DescriptionRequestBody from client is received
        if (body instanceof DescriptionRequestBody) {
            this.clientDescriptionSocketAddress = address;
            logger.debug("Description Address: {}", this.clientDescriptionSocketAddress);
        }
        // update client control/data addresses if ConnectRequestBody from client is received
        else if (body instanceof ConnectRequestBody) {
            final var connectRequestBody = (ConnectRequestBody) body;
            // fetch the control and data ports
            final var clientControlHPAI = connectRequestBody.getControlEndpoint();
            this.clientControlSocketAddress = new InetSocketAddress(clientControlHPAI.getAddress(), clientControlHPAI.getPort());
            logger.debug("Control Address: {}", this.clientControlSocketAddress);

            final var clientDataHPAI = connectRequestBody.getDataEndpoint();
            this.clientDataSocketAddress = new InetSocketAddress(clientDataHPAI.getAddress(), clientDataHPAI.getPort());
            logger.debug("Data Address   : {}", this.clientDataSocketAddress);

            // if address is '0.0.0.0' assuming it is using NAT
            this.useNAT = false; // disabled for now! clientControlHPAI.getRemoteControlAddress().isAnyLocalAddress();
        }

        return body;
    }

    @Override
    public void send(final SelectionKey key, final Body body) throws IOException {
        // packet: header + body
        final ByteBuffer byteBuffer;
        if (body instanceof BytesBody) {
            // byte body
            byteBuffer = ByteBuffer.wrap(body.getRawData());
        } else {
            // OK
            final var headerRawData = Header.create(body).getRawData();
            final var bodyRawData = body.getRawData();
            byteBuffer = ByteBuffer.wrap(Bytes.concat(headerRawData, bodyRawData));
        }

        // choose address based on body channel-relation
        final SocketAddress address;
        if (body instanceof DescriptionChannelRelated) {
            address = this.clientDescriptionSocketAddress;
        } else if (!useNAT && body instanceof ControlChannelRelated) {
            address = this.clientControlSocketAddress;
        } else if (!useNAT && body instanceof DataChannelRelated) {
            address = this.clientDataSocketAddress;
        } else {
            // otherwise just use the sender form previous received diagram
            address = (SocketAddress) key.attachment();
        }

        this.channel.send(byteBuffer, address);
    }


    @Override
    public void close() throws Exception {
        Closeables.closeQuietly(this.channel);
    }
}
