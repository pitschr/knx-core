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
import li.pitschmann.knx.link.datapoint.DPT1;
import li.pitschmann.utils.ByteFormatter;

import javax.annotation.Nonnull;
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

    public DPT1Value(final @Nonnull DPT1 dpt, final byte b) {
        this(dpt, (b & 0x01) != 0x00);
    }

    public DPT1Value(final @Nonnull DPT1 dpt, final boolean booleanValue) {
        super(dpt);
        this.booleanValue = booleanValue;
    }

    /**
     * Converts {@code booleanValue} to byte array
     *
     * @param booleanValue
     * @return byte array
     */
    @Nonnull
    public static byte[] toByteArray(final boolean booleanValue) {
        return new byte[]{booleanValue ? (byte) 0x01 : 0x00};
    }

    public boolean getBooleanValue() {
        return this.booleanValue;
    }

    @Nonnull
    public String getBooleanText() {
        return this.getDPT().getTextFor(this.booleanValue);
    }

    @Nonnull
    @Override
    public byte[] toByteArray() {
        return toByteArray(this.booleanValue);
    }

    @Nonnull
    @Override
    public String toText() {
        return getBooleanText();
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(DPT1Value.class)
                .add("dpt", this.getDPT())
                .add("booleanValue", this.booleanValue)
                .add("booleanText", this.getBooleanText())
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nonnull Object obj) {
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
