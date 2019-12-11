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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.AbstractMultiRawData;
import li.pitschmann.knx.core.ChannelIdAware;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Body for Disconnect Request
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Communication Channel ID      | reserved                      |
 * | (1 octet)                     | (1 octet)                     |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Control endpoint (8 octets)                                   |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class DisconnectRequestBody extends AbstractMultiRawData implements RequestBody, ChannelIdAware, ControlChannelRelated {
    /**
     * Structure Length for {@link DisconnectRequestBody}
     * <p>
     * 2 bytes for this body<br>
     * 8 bytes for HPAI<br>
     */
    private static final int STRUCTURE_LENGTH = 10;
    private final int channelId;
    private final HPAI controlEndpoint;

    private DisconnectRequestBody(final byte[] bytes) {
        super(bytes);

        this.channelId = Bytes.toUnsignedInt(bytes[0]);
        // bytes[1] -> reserved
        this.controlEndpoint = HPAI.of(Arrays.copyOfRange(bytes, 2, bytes.length));
    }

    /**
     * Builds a new {@link DisconnectRequestBody} instance
     *
     * @param bytes complete byte array for {@link DisconnectRequestBody}
     * @return a new immutable {@link DisconnectRequestBody}
     */
    public static DisconnectRequestBody of(final byte[] bytes) {
        return new DisconnectRequestBody(bytes);
    }

    /**
     * Creates a new {@link DisconnectRequestBody} instance
     *
     * @param channelId
     * @param controlEndpoint
     * @return a new immutable {@link DisconnectRequestBody}
     */
    public static DisconnectRequestBody of(final int channelId, final HPAI controlEndpoint) {
        // validate
        if (controlEndpoint == null) {
            throw new KnxNullPointerException("controlEndpoint");
        } else if (channelId < 0 || channelId > 0xFF) {
            throw new KnxNumberOutOfRangeException("channelId", 0, 0xFF, channelId);
        }

        final var hpaiAsBytes = controlEndpoint.getRawData();

        // create bytes
        final var bytes = new byte[2 + hpaiAsBytes.length];
        bytes[0] = (byte) channelId;
        bytes[1] = 0x00; // reserved
        System.arraycopy(hpaiAsBytes, 0, bytes, 2, hpaiAsBytes.length);

        return of(bytes);
    }

    @Override
    protected void validate(final byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length != STRUCTURE_LENGTH) {
            // 2 bytes for this body
            // 8 bytes for HPAI
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_LENGTH, STRUCTURE_LENGTH, rawData.length, rawData);
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DISCONNECT_REQUEST;
    }

    @Override
    public int getChannelId() {
        return this.channelId;
    }

    public HPAI getControlEndpoint() {
        return this.controlEndpoint;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("channelId", this.channelId + " (" + ByteFormatter.formatHex(this.channelId) + ")")
                .add("controlEndpoint", this.controlEndpoint.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
