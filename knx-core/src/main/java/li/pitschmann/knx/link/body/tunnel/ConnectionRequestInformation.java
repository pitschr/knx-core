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

package li.pitschmann.knx.link.body.tunnel;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.AbstractMultiRawData;
import li.pitschmann.knx.link.body.hpai.ConnectionType;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;

/**
 * Tunneling Connection Request Information (CRI)
 * <p>
 * The Connection Request Information structure (CRI) shall be the additional information needed for different types of
 * communication channels to fulfill a connection request.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length              | TUNNEL_CONNECTION             |
 * | (1 octet = 04h)               | (1 octet = 04h)               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | KNX Layer                     | reserved                      |
 * | (1 octet)                     | (1 octet)                     |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class ConnectionRequestInformation extends AbstractMultiRawData {
    private final int length;
    private final ConnectionType connectionType;
    private final LayerType layerType;

    private ConnectionRequestInformation(final byte[] criRawData) {
        super(criRawData);

        this.length = Bytes.toUnsignedInt(criRawData[0]);
        this.connectionType = ConnectionType.valueOf(Bytes.toUnsignedInt(criRawData[1]));
        this.layerType = LayerType.valueOf(Bytes.toUnsignedInt(criRawData[2]));
        // criRawData[3] = reserved
    }

    /**
     * Builds a new {@link ConnectionRequestInformation} instance
     *
     * @param bytes complete byte array for {@link ConnectionRequestInformation}
     * @return immutable {@link ConnectionRequestInformation}
     */
    public static ConnectionRequestInformation valueOf(final byte[] bytes) {
        return new ConnectionRequestInformation(bytes);
    }

    /**
     * Creates a new {@link ConnectionRequestInformation} instance
     *
     * @return immutable {@link ConnectionRequestInformation}
     */
    public static ConnectionRequestInformation create() {
        // hardcoded
        // 4 bytes (1 byte for length, 1 byte for connection type, 1 byte for layer type and 1 byte for reserved)
        final var length = 4;

        // create bytes
        final var bytes = new byte[length];
        bytes[0] = (byte) length;
        bytes[1] = ConnectionType.TUNNEL_CONNECTION.getCodeAsByte();
        bytes[2] = LayerType.TUNNEL_LINKLAYER.getCodeAsByte();
        bytes[3] = 0x00;

        return valueOf(bytes);
    }

    @Override
    protected void validate(final byte[] criRawData) {
        if (criRawData == null) {
            throw new KnxNullPointerException("criRawData");
        } else if (criRawData.length != 4) {
            throw new KnxNumberOutOfRangeException("criRawData", 4, 4, criRawData.length, criRawData);
        } else if (Bytes.toUnsignedInt(criRawData[0]) != 4) {
            throw new KnxNumberOutOfRangeException("criRawData[0]", 4, 4, Bytes.toUnsignedInt(criRawData[0]), criRawData);
        }
    }

    public int getLength() {
        return this.length;
    }

    public ConnectionType getConnectionType() {
        return this.connectionType;
    }

    public LayerType getLayerType() {
        return this.layerType;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("length", this.length + " (" + ByteFormatter.formatHex(this.length) + ")")
                .add("connectionType", this.connectionType)
                .add("layerType", this.layerType);
        // @formatter:off
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
