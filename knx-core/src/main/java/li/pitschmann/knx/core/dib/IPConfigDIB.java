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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.MultiRawDataAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * IP Config DIB to specify DIB for type {@link DescriptionType#IP_CONFIG}
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length            | Description Type Code           |
 * | (1 octet)                   | (1 octet)                       |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | IP Address                                                    |
 * | (4 octets)                                                    |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Subnet Mask                                                   |
 * | (4 octets)                                                    |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Default Gateway                                               |
 * | (4 octets)                                                    |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | IP Capabilities             | IP assignment method            |
 * | (1 octet)                   | (1 octet)                       |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * See: KNX Standard Core
 *
 * @author PITSCHR
 */
public final class IPConfigDIB implements MultiRawDataAware {
    /**
     * Structure Length for {@link IPConfigDIB}
     * <p>
     * 1 byte for Structure Length<br>
     * 1 byte for Description Type Code<br>
     * 4 bytes for IP Address<br>
     * 4 bytes for Subnet Mask<br>
     * 4 bytes for Default Gateway<br>
     * 1 byte for IP Capabilities<br>
     * 1 byte for IP assignment method<br>
     */
    private static final int STRUCTURE_LENGTH = 16;
    private final byte[] bytes;

    private IPConfigDIB(final byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Builds a new {@link IPConfigDIB} instance
     *
     * @param bytes complete byte array for {@link IPConfigDIB}
     * @return a new immutable {@link IPConfigDIB}
     */
    public static IPConfigDIB of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new IPConfigDIB(bytes);
    }

    @Override
    public byte[] toByteArray() {
        return bytes.clone();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("bytes", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof IPConfigDIB) {
            final var other = (IPConfigDIB) obj;
            return Arrays.equals(this.bytes, other.bytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

}
