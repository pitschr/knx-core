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
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.hpai.ConnectionType;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;

/**
 * Tunneling Connection Response Data (CRD)
 * <p>
 * The Connection Request Data Block structure (CRD) shall be the data block returned with the
 * {@link ServiceType#CONNECT_RESPONSE} frame.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length              | TUNNEL_CONNECTION             |
 * | (1 octet = 04h)               | (1 octet = 04h)               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | KNX Individual Address                                        |
 * | (2 Octets)                                                    |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class ConnectionResponseData extends AbstractMultiRawData {
    private final int length;
    private final ConnectionType connectionType;
    private final IndividualAddress address;

    private ConnectionResponseData(final byte[] crdRawData) {
        super(crdRawData);

        this.length = Bytes.toUnsignedInt(crdRawData[0]);
        this.connectionType = ConnectionType.valueOf(Bytes.toUnsignedInt(crdRawData[1]));
        this.address = IndividualAddress.of(new byte[]{crdRawData[2], crdRawData[3]});
    }

    /**
     * Builds a new {@link ConnectionResponseData} instance
     *
     * @param bytes complete byte array for {@link ConnectionResponseData}
     * @return immutable {@link ConnectionResponseData}
     */
    public static ConnectionResponseData valueOf(final byte[] bytes) {
        return new ConnectionResponseData(bytes);
    }

    /**
     * Creates a new {@link ConnectionResponseData} instance
     *
     * @param address
     * @return immutable {@link ConnectionResponseData}
     */
    public static ConnectionResponseData create(final IndividualAddress address) {
        // validate
        if (address == null) {
            throw new KnxNullPointerException("address");
        }

        final var addressAsBytes = address.getRawData();

        // hardcoded
        // 2 bytes (1 byte for length, 1 byte for connection type) + 2 bytes for individual address
        final var length = 2 + addressAsBytes.length;

        // create bytes
        final var bytes = new byte[length];
        bytes[0] = (byte) length;
        bytes[1] = ConnectionType.TUNNEL_CONNECTION.getCodeAsByte();
        System.arraycopy(addressAsBytes, 0, bytes, 2, addressAsBytes.length);

        return valueOf(bytes);
    }

    @Override
    protected void validate(final byte[] crdRawData) {
        if (crdRawData == null) {
            throw new KnxNullPointerException("crdRawData");
        } else if (crdRawData.length != 4) {
            throw new KnxNumberOutOfRangeException("crdRawData", 4, 4, crdRawData.length, crdRawData);
        } else if (Bytes.toUnsignedInt(crdRawData[0]) != 4) {
            throw new KnxNumberOutOfRangeException("crdRawData[0]", 4, 4, Bytes.toUnsignedInt(crdRawData[0]), crdRawData);
        }
    }

    public int getLength() {
        return this.length;
    }

    public ConnectionType getConnectionType() {
        return this.connectionType;
    }

    public IndividualAddress getAddress() {
        return this.address;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("length", this.length + " (" + ByteFormatter.formatHex(this.length) + ")")
                .add("connectionType", this.connectionType)
                .add("address", this.address.toString(false));
        // @formatter:off
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}