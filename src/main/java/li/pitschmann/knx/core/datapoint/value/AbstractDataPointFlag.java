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
import li.pitschmann.knx.core.datapoint.BaseDataPointType;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Abstract class for data point flag (1 byte)
 *
 * @param <T>
 * @author PITSCHR
 */
abstract class AbstractDataPointFlag<T extends BaseDataPointType<?>> extends AbstractDataPointValue<T> {
    private final byte b;

    protected AbstractDataPointFlag(final T dpt, final byte b) {
        super(dpt);
        this.b = b;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{this.b};
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("byte", ByteFormatter.formatHex(this.b))
                .toString();
        // @formatter:on
    }

    /**
     * Returns human-friendly text which bits are set
     * <p>
     * If b0 is set, then "1" is returned.<br>
     * If b0, b1, b3 and b7 are set, then "1, 2, 4, 8" is returned.<br>
     *
     * @param emptyText text to be returned in case no bits are set
     * @return string representation of bit-set
     */
    protected String toText(final String emptyText) {
        if (b == 0) {
            return emptyText;
        } else {
            final var sb = new StringBuilder(30);
            for (var i = 0; i < 8; i++) {
                if (this.isSet(i)) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append((i + 1));
                }
            }
            return sb.toString();
        }
    }

    public final boolean isSet(final int bit) {
        Preconditions.checkArgument(bit >= 0 && bit < 8, "Bit must be between 0 and 7 (actual: {})", bit);
        return isBitSet(b, bit);
    }

    @Override
    public final boolean equals(final @Nullable Object obj) {
        if (obj instanceof AbstractDataPointFlag) {
            final var other = (AbstractDataPointFlag<?>)obj;
            return this.getClass() == obj.getClass()
                    && Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.b, other.b);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), getDPT(), b);
    }
}
