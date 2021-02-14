/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
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

package li.pitschmann.knx.core.net.tunnel;

import li.pitschmann.knx.core.MultiRawDataAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.net.ConnectionType;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Tunneling Connection Request Information (CRI)
 * <p>
 * The Connection Request Information structure (CRI) shall be the
 * additional information needed for different types of communication
 * channels to fulfill a connection request.
 * <p>
 * As this structure shall contain two substructures including host
 * protocol independent data as well as host protocol dependent information,
 * the specific definition of the CRI can be found in the description of
 * the connection type with consultancy of the host protocol dependent
 * parts of the KNXnet/IP specification.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length              | TUNNEL_CONNECTION             |
 * | (1 octet = 04h)               | (1 octet = 04h)               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | KNX Layer Type                | reserved                      |
 * | (1 octet)                     | (1 octet)                     |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * See: KNX Specification, Core, 7.5.2
 *
 * @author PITSCHR
 */
public final class ConnectionRequestInfo implements MultiRawDataAware {
    /**
     * Fixed length for {@link ConnectionRequestInfo}
     */
    public static final int STRUCTURE_LENGTH = 0x04;
    private static final ConnectionRequestInfo DEFAULT = of(ConnectionType.TUNNEL_CONNECTION, LayerType.TUNNEL_LINKLAYER);

    private final int length;
    private final ConnectionType connectionType;
    private final LayerType layerType;

    private ConnectionRequestInfo(final byte[] bytes) {
        this(
                // bytes[0] => Structure length
                Byte.toUnsignedInt(bytes[0]),
                // bytes[1] => Connection Type
                ConnectionType.valueOf(Byte.toUnsignedInt(bytes[1])),
                // bytes[2] => KNX Layer Type
                LayerType.valueOf(Byte.toUnsignedInt(bytes[2]))
                // bytes[3] (not-used / reserved)
        );
    }

    private ConnectionRequestInfo(final int length,
                                  final ConnectionType connectionType,
                                  final LayerType layerType) {
        Preconditions.checkArgument(length == STRUCTURE_LENGTH);
        Preconditions.checkNonNull(connectionType, "Connection Type is required.");
        Preconditions.checkNonNull(layerType, "Layer Type is required.");

        this.length = length;
        this.connectionType = connectionType;
        this.layerType = layerType;
    }

    /**
     * Builds a new {@link ConnectionRequestInfo} instance
     *
     * @param bytes complete byte array for {@link ConnectionRequestInfo}
     * @return a new immutable {@link ConnectionRequestInfo}
     */
    public static ConnectionRequestInfo of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new ConnectionRequestInfo(bytes);
    }

    /**
     * Uses the default {@link ConnectionRequestInfo} instance with {@link ConnectionType#TUNNEL_CONNECTION}
     * and {@link LayerType#TUNNEL_LINKLAYER} pre-defined
     *
     * @return re-usable immutable default {@link ConnectionRequestInfo}
     */
    public static ConnectionRequestInfo useDefault() {
        return DEFAULT;
    }

    /**
     * Returns an instance of {@link ConnectionRequestInfo}
     *
     * @param connectionType the connection type
     * @param layerType      the layer type
     * @return a new immutable {@link ConnectionRequestInfo}
     */
    public static ConnectionRequestInfo of(final ConnectionType connectionType, final LayerType layerType) {
        return new ConnectionRequestInfo(STRUCTURE_LENGTH, connectionType, layerType);
    }

    public int getLength() {
        return length;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public LayerType getLayerType() {
        return layerType;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{
                STRUCTURE_LENGTH,               // Structure Length
                connectionType.getCodeAsByte(), // Connection Type
                layerType.getCodeAsByte(),      // Layer Type
                0x00                            // reserved
        };
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("length", length)
                .add("connectionType", connectionType.name())
                .add("layerType", layerType.name())
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ConnectionRequestInfo) {
            final var other = (ConnectionRequestInfo) obj;
            return this.length == other.length //
                    && Objects.equals(this.connectionType, other.connectionType) //
                    && Objects.equals(this.layerType, other.layerType); //
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, connectionType, layerType);
    }
}
