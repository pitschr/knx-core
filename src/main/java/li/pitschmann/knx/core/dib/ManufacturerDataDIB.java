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
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Manufacturer Data DIB to specify DIB for type {@link DescriptionType#MANUFACTURER_DATA}
 * <p>
 * The KNX manufacturer ID shall be added to clearly identify the
 * manufacturer. This information is not necessarily encoded in the
 * KNX device KNX Serial Number (6 octets).
 * <p>
 * The manufacturer data DIB may contain any manufacturer specific data.
 * <p>
 * <strong>Experimental! Not used yet / no test available!</strong>
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length            | Description Type Code           |
 * | (1 octet)                   | (1 octet)                       |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | KNX Manufacturer ID                                           |
 * | (2 octets)                                                    |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Any manufacturer specific data                                |
 * | (up to 252 octets)                                            |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * <p>
 * The length of manufacturer specific data is the structure length
 * minus 3 octets. Maximum 252 octets.
 * <p>
 * See: KNX Standard Core
 *
 * @author PITSCHR
 */
public final class ManufacturerDataDIB implements MultiRawDataAware {
    /**
     * Minimum Structure Length for {@link ManufacturerDataDIB}
     * <p>
     * 1 byte for Structure Length<br>
     * 1 byte for Description Type Code<br>
     * 2 bytes for KNX Manufacturer ID<br>
     */
    private static final int STRUCTURE_MIN_LENGTH = 4;
    /**
     * Maximum Structure Length for {@link ManufacturerDataDIB}
     */
    private static final int STRUCTURE_MAX_LENGTH = 0xFF;
    private final int id;
    private final byte[] data;

    private ManufacturerDataDIB(final byte[] bytes) {
        // bytes[0] -> length not relevant
        // bytes[1] -> description type not relevant

        // manufacturer id
        this.id = Bytes.toUnsignedInt(bytes[2], bytes[3]);

        // manufacturer specific data
        if (bytes.length > STRUCTURE_MIN_LENGTH) {
            this.data = Arrays.copyOfRange(bytes, STRUCTURE_MIN_LENGTH, bytes.length);
        } else {
            this.data = new byte[0];
        }
    }

    /**
     * Builds a new {@link ManufacturerDataDIB} instance
     *
     * @param bytes complete byte array for {@link ManufacturerDataDIB}
     * @return a new immutable {@link ManufacturerDataDIB}
     */
    public static ManufacturerDataDIB of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length >= STRUCTURE_MIN_LENGTH && bytes.length <= STRUCTURE_MAX_LENGTH,
                "Incompatible structure length. Expected [{}..{}] but was: {}", STRUCTURE_MIN_LENGTH, STRUCTURE_MAX_LENGTH, bytes.length);
        Preconditions.checkArgument(bytes[1] == DescriptionType.MANUFACTURER_DATA.getCodeAsByte(),
                "Incompatible value for bytes[1]. Expected '{}' but was: {}", DescriptionType.MANUFACTURER_DATA.getCodeAsByte(), bytes[1]);

        return new ManufacturerDataDIB(bytes);
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data.clone();
    }

    @Override
    public byte[] toByteArray() {
        // 4 bytes (Structure Length, Description Type + Manufacturer Id)
        // + N dynamic bytes for manufacturer specific data
        final var totalLength = 4 + data.length;

        final var bytes = new byte[totalLength];
        bytes[0] = (byte) totalLength;
        bytes[1] = DescriptionType.MANUFACTURER_DATA.getCodeAsByte();
        bytes[2] = (byte) (id >>> 8);
        bytes[3] = (byte) (id & 0xFF);
        if (data.length > 0) {
            System.arraycopy(data, 0, bytes, 4, data.length);
        }

        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("id", id)
                .add("data", ByteFormatter.formatHexAsString(data))
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ManufacturerDataDIB) {
            final var other = (ManufacturerDataDIB) obj;
            return Objects.equals(this.id, other.id)
                    && Arrays.equals(this.data, other.data);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Arrays.hashCode(data));
    }

}
