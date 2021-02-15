/*
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
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.ConnectionType;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Tunneling Connection Response Data (CRD)
 * <p>
 * The Connection Request Data Block structure (CRD) shall be the
 * data block returned with the {@link ServiceType#CONNECT_RESPONSE}
 * frame.
 * <p>
 * As this structure shall contain two substructures including host
 * protocol independent data as well as host protocol dependent
 * information, the specific definition of the CRD can be found in
 * the description of the connection type with consultancy of the
 * host protocol dependent parts of the KNXnet/IP specification.
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length              | TUNNEL_CONNECTION             |
 * | (1 octet = 04h)               | (1 octet = 04h)               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | KNX Individual Address                                        |
 * | (2 Octets)                                                    |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * See: KNX Specification, Core, 7.5.3
 *
 * @author PITSCHR
 */
public final class ConnectionResponseData implements MultiRawDataAware {
    /**
     * Fixed length for {@link ConnectionResponseData}
     */
    public static final int STRUCTURE_LENGTH = 0x04;

    private final int length;
    private final ConnectionType connectionType;
    private final IndividualAddress address;

    private ConnectionResponseData(final byte[] bytes) {
        this(
                // bytes[0] => Structure Length
                Byte.toUnsignedInt(bytes[0]),
                // bytes[1] => Connection Type
                ConnectionType.valueOf(Byte.toUnsignedInt(bytes[1])),
                // bytes[2+3] => Individual Address
                IndividualAddress.of(new byte[]{bytes[2], bytes[3]})
        );
    }

    private ConnectionResponseData(final int length,
                                   final ConnectionType connectionType,
                                   final IndividualAddress address) {
        Preconditions.checkArgument(length == STRUCTURE_LENGTH);
        Preconditions.checkNonNull(connectionType, "Connection Type is required.");
        Preconditions.checkNonNull(address, "Individual Address is required.");

        this.length = length;
        this.connectionType = connectionType;
        this.address = address;
    }

    /**
     * Builds a new {@link ConnectionResponseData} instance
     *
     * @param bytes complete byte array for {@link ConnectionResponseData}
     * @return a new immutable {@link ConnectionResponseData}
     */
    public static ConnectionResponseData of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new ConnectionResponseData(bytes);
    }

    /**
     * Creates a new {@link ConnectionResponseData} instance
     *
     * @param address the individual address
     * @return a new immutable {@link ConnectionResponseData}
     */
    public static ConnectionResponseData of(final ConnectionType connectionType, final IndividualAddress address) {
        return new ConnectionResponseData(STRUCTURE_LENGTH, connectionType, address);
    }

    @Override
    public byte[] toByteArray() {
        final var addressAsBytes = address.toByteArray();

        return new byte[]{
                STRUCTURE_LENGTH,                       // Structure Length
                connectionType.getCodeAsByte(),         // Connection Type
                addressAsBytes[0], addressAsBytes[1]    // Individual Address (2 bytes)
        };
    }

    public int getLength() {
        return length;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public IndividualAddress getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("length", length)
                .add("connectionType", connectionType.name())
                .add("address", address)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ConnectionResponseData) {
            final var other = (ConnectionResponseData) obj;
            return this.length == other.length //
                    && Objects.equals(this.connectionType, other.connectionType) //
                    && Objects.equals(this.address, other.address); //
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, connectionType, address);
    }
}
