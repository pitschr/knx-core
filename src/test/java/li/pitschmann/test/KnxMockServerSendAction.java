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

package li.pitschmann.test;

import com.google.common.base.*;
import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.utils.*;

import javax.annotation.*;

/**
 * Send Action behavior for the KNX Mock Server
 *
 * @author PITSCHR
 */
public final class KnxMockServerSendAction implements KnxMockServerAction {
    private final Body body;

    private KnxMockServerSendAction(final byte[] knxPacket) {
        Body body;
        try {
            body = BodyFactory.valueOf(knxPacket);
        } catch (final Throwable t) {
            body = new CorruptedBody(knxPacket);
        }
        this.body = body;
    }

    private KnxMockServerSendAction(final byte[] knxPacket, final WrongChannelBody.ChannelType channelType) {
        this.body = new WrongChannelBody(knxPacket, channelType);
    }

    /**
     * Reads the {@code knxCommand} and returns an instance of {@link KnxMockServerSendAction}
     *
     * @param knxCommand
     * @return an instance of {@link KnxMockServerSendAction}
     */
    static KnxMockServerSendAction of(final String knxCommand) {
        if (knxCommand.toUpperCase().startsWith("CHANNEL=")) {
            // get channel type
            final String channelTypeAsString = knxCommand.substring(8, knxCommand.indexOf("{")).toUpperCase();
            final KnxMockServerSendAction.WrongChannelBody.ChannelType channelType = KnxMockServerSendAction.WrongChannelBody.ChannelType.valueOf(channelTypeAsString);
            // get packet as byte
            final String innerCommand = knxCommand.substring(knxCommand.indexOf("{") + 1, knxCommand.lastIndexOf("}"));
            return new KnxMockServerSendAction(Bytes.toByteArray(innerCommand), channelType);
        } else {
            return new KnxMockServerSendAction(Bytes.toByteArray(knxCommand));
        }
    }

    public Body getBody() {
        return body;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("body", this.body).toString();
    }

    /**
     * Corrupted Body
     * <p/>
     * This is used when body could not be identified correctly because it is corrupted.
     * With KNX mock server we are simulating bad KNX packets.
     *
     * @author PITSCHR
     */
    public static final class CorruptedBody extends ErroneousBody {
        private CorruptedBody(final byte[] bytes) {
            super(bytes);
        }
    }

    /**
     * Wrong Channel Body
     * <p/>
     * This is used when given bytes should be sent to a wrong channel.
     * With KNX mock server we are simulating wrong channel communications.
     */
    public static final class WrongChannelBody extends ErroneousBody {
        private final ChannelType channelType;

        private WrongChannelBody(final byte[] bytes, final ChannelType channelType) {
            super(bytes);
            this.channelType = channelType;
        }

        public ChannelType getChannelType() {
            return channelType;
        }

        @Override
        public String toString(boolean inclRawData) {
            return MoreObjects.toStringHelper(this) //
                    .add("channelType", channelType) //
                    .add("rawData", this.getRawDataAsHexString()) //
                    .toString();
        }

        public enum ChannelType {
            CONTROL, DATA
        }
    }

    /**
     * Erroneous Body
     * <p/>
     * With KNX mock server we are simulating bad / corrupt scenarios.
     */
    public static abstract class ErroneousBody extends AbstractMultiRawData implements Body {
        private ErroneousBody(final byte[] bytes) {
            super(bytes);
        }

        @Nonnull
        @Override
        public ServiceType getServiceType() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void validate(byte[] rawData) throws KnxException {
            if (rawData == null) {
                throw new KnxNullPointerException("rawData");
            }
        }

        @Override
        public String toString(boolean inclRawData) {
            return MoreObjects.toStringHelper(this).add("rawData", this.getRawDataAsHexString()).toString();
        }
    }
}
