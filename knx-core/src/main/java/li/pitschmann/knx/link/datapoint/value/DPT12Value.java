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

package li.pitschmann.knx.link.datapoint.value;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.datapoint.DPT12;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;
import li.pitschmann.utils.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT12} (12.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Unsigned Value)                                              |
 * Encoding     | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Unsigned Value)                                              |
 *              | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (U<sub>32</sub>)
 * Range:      U = [0 .. 4294967295]
 * Unit:       pulses
 * Resolution: 1 pulse
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT12Value extends AbstractDataPointValue<DPT12> {
    private final long unsignedValue;
    private final byte[] byteArray;

    public DPT12Value(final @Nonnull DPT12 dpt, final @Nonnull byte[] bytes) {
        super(dpt);
        Preconditions.checkArgument(bytes.length == 4);
        // unsigned value
        this.unsignedValue = Bytes.toUnsignedLong(bytes);
        this.byteArray = bytes;
    }

    public DPT12Value(final @Nonnull DPT12 dpt, final long value) {
        super(dpt);
        Preconditions.checkArgument(dpt.isRangeClosed(value));
        this.unsignedValue = value;
        this.byteArray = toByteArray(value);
    }

    /**
     * Converts signed long value to 4-byte array
     *
     * @param value
     * @return byte array
     */
    @Nonnull
    public static byte[] toByteArray(final long value) {
        return new byte[]{ //
                (byte) (value >>> 24), //
                (byte) (value >>> 16), //
                (byte) (value >>> 8), //
                (byte) value};
    }

    public long getUnsignedValue() {
        return this.unsignedValue;
    }

    @Nonnull
    @Override
    public byte[] toByteArray() {
        return this.byteArray.clone();
    }

    @Nonnull
    @Override
    public String toText() {
        return getValueAsText(getUnsignedValue());
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(DPT12Value.class)
                .add("dpt", this.getDPT())
                .add("unsignedValue", this.unsignedValue)
                .add("byteArray", ByteFormatter.formatHexAsString(this.byteArray))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT12Value) {
            final var other = (DPT12Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.unsignedValue, other.unsignedValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.unsignedValue);
    }

}
