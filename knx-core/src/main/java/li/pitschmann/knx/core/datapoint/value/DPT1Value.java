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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Data Point Value for {@link DPT1} (1.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  |                             b |
 * Encoding     |                             B |
 *              +---+---+---+---+---+---+---+---+
 * Format:     1 bit (B<sub>1</sub>)
 * Range:      b = {0, 1}
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT1Value extends AbstractDataPointValue<DPT1> {
    private final boolean booleanValue;

    public DPT1Value(final DPT1 dpt, final byte b) {
        this(dpt, (b & 0x01) != 0x00);
    }

    public DPT1Value(final DPT1 dpt, final boolean booleanValue) {
        super(dpt);
        this.booleanValue = booleanValue;
    }

    /**
     * Converts {@code booleanValue} to byte array
     *
     * @param booleanValue boolean to be converted
     * @return one byte array from boolean
     */
    public static byte[] toByteArray(final boolean booleanValue) {
        return new byte[]{booleanValue ? (byte) 0x01 : 0x00};
    }

    public boolean getBooleanValue() {
        return this.booleanValue;
    }

    public String getBooleanText() {
        return this.getDPT().getTextFor(this.booleanValue);
    }

    @Override
    public byte[] toByteArray() {
        return toByteArray(this.booleanValue);
    }

    @Override
    public String toText() {
        return getBooleanText();
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("booleanValue", this.booleanValue)
                .add("booleanText", this.getBooleanText())
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT1Value) {
            final var other = (DPT1Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.booleanValue, other.booleanValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.booleanValue);
    }

}
