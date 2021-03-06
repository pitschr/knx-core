/*
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
import li.pitschmann.knx.core.datapoint.BaseDataPointType;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Abstract class for data point flags (2 or more bytes)
 *
 * @param <T>
 * @author PITSCHR
 */
abstract class AbstractDataPointFlags<T extends BaseDataPointType<?>> extends AbstractDataPointValue<T> {
    private final byte[] bytes;

    protected AbstractDataPointFlags(final T dpt, final byte[] bytes) {
        super(dpt);
        this.bytes = Objects.requireNonNull(bytes);
    }

    public final boolean isSet(final int bit) {
        Preconditions.checkArgument(bit >= 0 && bit < this.bytes.length * 8,
                "Bit must be between 0 and {} (actual: {})", ((this.bytes.length * 8) - 1), bit);
        return isBitSet(bytes, bit);
    }

    @Override
    public byte[] toByteArray() {
        return this.bytes.clone();
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && this.getClass().equals(obj.getClass())) {
            final var other = this.getClass().cast(obj);
            return Objects.equals(this.getDPT(), other.getDPT()) && Arrays.compare(this.bytes, other.bytes) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), Arrays.hashCode(this.bytes));
    }
}
