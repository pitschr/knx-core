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
import java.util.Objects;

/**
 * Abstract class for data point flag (1 byte)
 *
 * @param <T>
 * @author PITSCHR
 */
abstract class AbstractDataPointFlag<T extends AbstractDataPointType<?>> extends AbstractDataPointValue<T> {
    private final byte b;

    protected AbstractDataPointFlag(final @Nonnull T dpt, final byte b) {
        super(dpt);
        this.b = b;
    }

    @Nonnull
    @Override
    public byte[] toByteArray() {
        return new byte[]{this.b};
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this.getClass())
                .add("dpt", this.getDPT())
                .add("byte", ByteFormatter.formatHex(this.b))
                .toString();
        // @formatter:on
    }

    public final boolean isSet(final int bit) {
        Preconditions.checkArgument(bit >= 0 && bit < 8, "Bit must be between 0 and 7 (actual: " + bit + ")");
        return (this.b & (0x01 << bit)) != 0;
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && this.getClass().equals(obj.getClass())) {
            final var other = this.getClass().cast(obj);
            return Objects.equals(this.getDPT(), other.getDPT()) && this.b == other.b;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.b);
    }
}
