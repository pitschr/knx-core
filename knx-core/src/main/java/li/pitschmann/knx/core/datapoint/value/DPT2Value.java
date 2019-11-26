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

import li.pitschmann.knx.core.datapoint.DPT2;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT2} (2.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  |                         c   b |
 * Encoding     |                         C   B |
 *              +---+---+---+---+---+---+---+---+
 * Format:     2 bits (B<sub>2</sub>)
 * Range:      c = {0 = no control, 1 = control}
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT2Value extends AbstractDataPointValue<DPT2> {
    private final boolean controlled;
    private final boolean booleanValue;

    public DPT2Value(final @Nonnull DPT2 dpt, final byte b) {
        super(dpt);
        // bit 1 = controlled
        this.controlled = (b & 0x02) != 0x00;
        // bit 0 = value
        this.booleanValue = (b & 0x01) != 0x00;
    }

    public DPT2Value(final @Nonnull DPT2 dpt, final boolean controlled, final boolean booleanValue) {
        super(dpt);
        this.controlled = controlled;
        this.booleanValue = booleanValue;
    }

    /**
     * Converts {@code controlled} and {@code booleanValue} to byte array
     *
     * @param controlled
     * @param booleanValue
     * @return byte array
     */
    @Nonnull
    public static byte[] toByteArray(final boolean controlled, final boolean booleanValue) {
        var b = (byte) 0x00;
        if (controlled) {
            b |= 0x02;
        }
        if (booleanValue) {
            b |= 0x01;
        }
        return new byte[]{b};
    }

    public boolean isControlled() {
        return this.controlled;
    }

    public boolean getBooleanValue() {
        return this.booleanValue;
    }

    @Nonnull
    public String getBooleanText() {
        return this.getDPT().getDPT1().getTextFor(this.booleanValue);
    }

    @Nonnull
    @Override
    public byte[] toByteArray() {
        return toByteArray(this.controlled, this.booleanValue);
    }

    @Nonnull
    @Override
    public String toText() {
        if (isControlled()) {
            return String.format("controlled '%s'", getBooleanText());
        } else {
            return getBooleanText();
        }
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("controlled", this.controlled)
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
        } else if (obj instanceof DPT2Value) {
            final var other = (DPT2Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.controlled, other.controlled) //
                    && Objects.equals(this.booleanValue, other.booleanValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.controlled, this.booleanValue);
    }
}