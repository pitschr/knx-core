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
public final class DPT1Value extends AbstractDataPointValue<DPT1> implements PayloadOptimizable {
    private final boolean value;

    public DPT1Value(final DPT1 dpt, final byte b) {
        this(
                dpt,
                // boolean
                b == 0x01
        );
    }

    public DPT1Value(final DPT1 dpt, final boolean value) {
        super(dpt);
        this.value = value;
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
     * For {@link DPT1#SWITCH} it would be {@code on} if boolean value is {@code true},
     * otherwise it would be {@code off} for boolean value {@code false}.
     * <p>
     * The text is pre-defined by the {@link DPT1}
     *
     * @return human-friendly text
     */
    public String getText() {
        return this.getDPT().getTextFor(value);
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{value ? (byte) 0x01 : 0x00};
    }

    @Override
    public String toText() {
        return getText();
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
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
        } else if (obj instanceof DPT1Value) {
            final var other = (DPT1Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), value);
    }

}
