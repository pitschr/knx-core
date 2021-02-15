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
import li.pitschmann.knx.core.datapoint.DPT27;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT27} (27.xxx)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | m15                  (Mask Bit Info)                       m0 |
 * Encoding    | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | s15                   (Info On Off)                        s0 |
 *             | B   B   B   B   B   B   B   B   B   B   B   B   B   B   B   B |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (B<sub>32</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT27Value extends AbstractDataPointValue<DPT27> {
    private final byte[] maskBytes;
    private final byte[] onBytes;

    public DPT27Value(final byte[] bytes) {
        this(
                // mask bytes
                bytes[0], bytes[1],
                // on bytes
                bytes[2], bytes[3]
        );
    }

    public DPT27Value(final byte maskByte1, final byte maskByte0, final byte onByte1, final byte onByte0) {
        super(DPT27.COMBINED_INFO_ON_OFF);

        maskBytes = new byte[]{ maskByte1, maskByte0 };
        onBytes = new byte[]{ onByte1, onByte0 };
    }

        public boolean isValid(final int index) {
        Preconditions.checkArgument(index >= 0 && index <= 15,
                "Index must be between [0..15] but was: {}", index);
        return isBitSet(maskBytes, index);
    }

    public boolean isOn(final int index) {
        Preconditions.checkArgument(index >= 0 && index <= 15,
                "Index must be between [0..15] but was: {}", index);
        return isBitSet(onBytes, index);
    }

    /**
     * Returns map-like representation of valid On/Off bits
     * <p>
     * Example: {@code 0=on, 2=off, 15=on}
     *
     * @return human-friendly text representation
     */
    @Override
    public String toText() {
        // only print values which are valid 'on' or 'off'
        // 0=off (= 5 chars) for 0..9
        // 10=off (= 6 chars) for 10..15
        // Total max length: 5 chars * 10 digits + 6 chars * 5 digits + 2 chars+ 14 digits = 110 chars max
        final var sb = new StringBuilder(110);
        for (int i = 0; i < 16; i++) {
            if (isValid(i)) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(i)
                        .append('=')
                        .append(isOn(i) ? "on" : "off");
            }
        }
        return sb.toString();
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{
                maskBytes[0], maskBytes[1],
                onBytes[0], onBytes[1]
        };
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("info", '{' + toText() + '}')
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT27Value) {
            final var other = (DPT27Value) obj;
            return Arrays.equals(this.maskBytes, other.maskBytes) //
                    && Arrays.equals(this.onBytes, other.onBytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(maskBytes), Arrays.hashCode(onBytes));
    }
}
