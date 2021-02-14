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
import li.pitschmann.knx.core.datapoint.DPT2;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

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
public final class DPT2Value extends AbstractDataPointValue<DPT2> implements PayloadOptimizable {
    private final boolean controlled;
    private final boolean value;

    public DPT2Value(final DPT2 dpt, final byte b) {
        this(dpt,
                // bit 1 = controlled
                (b & 0x02) != 0x00,
                // bit 0 = value
            (b & 0x01) != 0x00
        );
    }

    public DPT2Value(final DPT2 dpt, final boolean controlled, final boolean booleanValue) {
        super(dpt);
        this.controlled = controlled;
        this.value = booleanValue;
    }

    /**
     * Returns if the controlled flag is set
     *
     * @return boolean
     */
    public boolean isControlled() {
        return controlled;
    }

    /**
     * Returns the boolean value
     *
     * @return boolean
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Returns the human-friendly text of actual boolean value.
     * <p>
     * For {@link DPT2#SWITCH_CONTROL} it would be {@code on} if boolean value is {@code true},
     * otherwise it would be {@code off} for boolean value {@code false}.
     * <p>
     * The text is pre-defined by the {@link DPT1} which is linked by the {@link DPT2}
     *
     * @return human-friendly text
     */
    public String getText() {
        return this.getDPT().getDPT1().getTextFor(value);
    }

    @Override
    public byte[] toByteArray() {
        var b = (byte) 0x00;
        if (controlled) {
            b |= 0x02;
        }
        if (value) {
            b |= 0x01;
        }
        return new byte[]{b};
    }

    @Override
    public String toText() {
        if (isControlled()) {
            return "controlled '" + getText() + "'";
        } else {
            return getText();
        }
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("controlled", controlled)
                .add("value", value)
                .add("text", getText())
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT2Value) {
            final var other = (DPT2Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.controlled, other.controlled) //
                    && Objects.equals(this.value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), controlled, value);
    }
}
