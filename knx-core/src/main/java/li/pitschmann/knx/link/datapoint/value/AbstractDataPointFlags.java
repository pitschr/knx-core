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
import com.google.common.base.Preconditions;
import li.pitschmann.knx.link.datapoint.AbstractDataPointType;
import li.pitschmann.utils.ByteFormatter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Abstract class for data point flags (2 or more bytes)
 *
 * @param <T>
 * @author PITSCHR
 */
abstract class AbstractDataPointFlags<T extends AbstractDataPointType<?>> extends AbstractDataPointValue<T> {
    private final byte[] bytes;

    protected AbstractDataPointFlags(final @Nonnull T dpt, final @Nonnull byte[] bytes) {
        super(dpt);
        this.bytes = Objects.requireNonNull(bytes);
    }

    @Nonnull
    @Override
    public byte[] toByteArray() {
        return this.bytes.clone();
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this.getClass())
                .add("dpt", this.getDPT())
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
                .toString();
        // @formatter:on
    }

    public final boolean isSet(final int bit) {
        Preconditions.checkArgument(bit >= 0 && bit < this.bytes.length * 8,
                "Bit must be between 0 and " + ((this.bytes.length * 8) - 1) + " (actual: " + bit + ")");
        // e.g. 2-bytes:
        // b15 ... b8 = byte0
        // b7 ... b0 = byte1
        // bit 0 = bytes[1], bit shift = 0
        // bit 10 = bytes[0], bit shift = 2
        return (this.bytes[this.bytes.length - (1 + (bit / 8))] & (0x01 << bit % 8)) != 0;
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
