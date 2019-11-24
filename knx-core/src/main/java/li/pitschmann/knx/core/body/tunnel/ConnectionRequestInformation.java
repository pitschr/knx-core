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

package li.pitschmann.knx.core.body.tunnel;

import li.pitschmann.knx.core.AbstractMultiRawData;
import li.pitschmann.knx.core.body.hpai.ConnectionType;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nonnull;

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
    private static final ConnectionRequestInformation DEFAULT = of(ConnectionType.TUNNEL_CONNECTION, LayerType.TUNNEL_LINKLAYER);

    private final int length;
    private final ConnectionType connectionType;
    private final LayerType layerType;

    private ConnectionRequestInformation(final @Nonnull byte[] criRawData) {
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
     * @return a new immutable {@link ConnectionRequestInformation}
     */
    @Nonnull
    public static ConnectionRequestInformation of(final @Nonnull byte[] bytes) {
        return new ConnectionRequestInformation(bytes);
    }

    /**
     * Uses the default {@link ConnectionRequestInformation} instance with {@link ConnectionType#TUNNEL_CONNECTION}
     * and {@link LayerType#TUNNEL_LINKLAYER} pre-defined
     *
     * @return re-usable immutable default {@link ConnectionRequestInformation}
     */
    @Nonnull
    public static ConnectionRequestInformation useDefault() {
        return DEFAULT;
    }

    /**
     * Returns an instance of {@link ConnectionRequestInformation}
     *
     * @param connectionType
     * @param layerType
     * @return a new immutable {@link ConnectionRequestInformation}
     */
    @Nonnull
    public static ConnectionRequestInformation of(final @Nonnull ConnectionType connectionType, final @Nonnull LayerType layerType) {
        // validate
        if (connectionType == null) {
            throw new KnxNullPointerException("connectionType");
        } else if (layerType == null) {
            throw new KnxNullPointerException("layerType");
        }

        // hardcoded
        // 4 bytes (1 byte for length, 1 byte for connection type, 1 byte for layer type and 1 byte for reserved)
        final var length = 4;

        // create bytes
        final var bytes = new byte[length];
        bytes[0] = (byte) length;
        bytes[1] = connectionType.getCodeAsByte();
        bytes[2] = layerType.getCodeAsByte();
        bytes[3] = 0x00;

        return of(bytes);
    }

    @Override
    protected void validate(final @Nonnull byte[] criRawData) {
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

    @Nonnull
    public ConnectionType getConnectionType() {
        return this.connectionType;
    }

    @Nonnull
    public LayerType getLayerType() {
        return this.layerType;
    }

    @Nonnull
    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
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
