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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.MultiRawDataAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Additional info container for CEMI
 *
 * @author PITSCHR
 */
public final class AdditionalInfo implements MultiRawDataAware {
    private static final AdditionalInfo EMPTY = new AdditionalInfo(new byte[0]);
    private final byte[] bytes;

    private AdditionalInfo(final byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Builds a new {@link AdditionalInfo} instance
     *
     * @param bytes complete byte array for {@link AdditionalInfo}
     * @return a new immutable {@link AdditionalInfo}
     */
    public static AdditionalInfo of(final byte[] bytes) {
        Preconditions.checkNonNull(bytes, "Bytes is required.");
        return new AdditionalInfo(bytes);
    }

    /**
     * Returns an empty {@link AdditionalInfo} instance
     *
     * @return re-usable immutable empty {@link AdditionalInfo} instance
     */
    public static AdditionalInfo empty() {
        return EMPTY;
    }

    @Override
    public byte[] toByteArray() {
        return bytes.clone();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("bytes", ByteFormatter.formatHexAsString(bytes))
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof AdditionalInfo) {
            final var other = (AdditionalInfo) obj;
            return Arrays.equals(this.bytes, other.bytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
