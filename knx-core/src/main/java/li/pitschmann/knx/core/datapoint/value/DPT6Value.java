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

import li.pitschmann.knx.core.datapoint.DPT6;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT6} (6.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Relative Signed Value)       |
 * Encoding     | V   V   V   V   V   V   V   V |
 *              +---+---+---+---+---+---+---+---+
 * Format:     8 bit (U<sub>8</sub>)
 * Range:      V = [-128 .. 127]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT6Value extends AbstractDataPointValue<DPT6> {
    private final int relativeSignedValue;

    public DPT6Value(final DPT6 dpt, final byte b) {
        super(dpt);
        // relative signed value
        this.relativeSignedValue = b;
    }

    public DPT6Value(final DPT6 dpt, final int value) {
        super(dpt);
        Preconditions.checkArgument(dpt.isRangeClosed(value));
        this.relativeSignedValue = value;
    }

    /**
     * Converts relative signed value to byte array
     *
     * @param value
     * @return byte array
     */
    public static byte[] toByteArray(final int value) {
        return new byte[]{(byte) value};
    }

    public int getRelativeSignedValue() {
        return this.relativeSignedValue;
    }

    @Override
    public byte[] toByteArray() {
        return toByteArray(this.relativeSignedValue);
    }

    @Override
    public String toText() {
        return getValueAsText(getRelativeSignedValue());
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("relativeSignedValue", this.relativeSignedValue)
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT6Value) {
            final var other = (DPT6Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.relativeSignedValue, other.relativeSignedValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.relativeSignedValue);
    }

    /**
     * <strong>6.020</strong> Status Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | a   b   c   d   e   f   f   f |
     * Encoding    | B   B   B   B   B   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (B<sub>5</sub> N<sub>3</sub>)
     * Range:      a, b, c, d, e = {0,1}
     *                  0 = set
     *                  1 = clear
     *             f             = {001b, 010b, 100b}
     *                  001b = mode 0 is active
     *                  010b = mode 1 is active
     *                  100b = mode 2 is active
     * Unit:       N/A
     * Resolution: N/A
     * </pre>
     */
    public static class StatusMode extends AbstractDataPointValue<DPT6.StatusMode> {
        private final byte b;

        public StatusMode(final byte b) {
            super(DPT6.STATUS_MODE); // hardcoded
            this.b = b;
        }

        public StatusMode(final boolean a, final boolean b, final boolean c, final boolean d, final boolean e, final Mode mode) {
            this(toByte(a, b, c, d, e, mode));
        }

        public static byte toByte(final boolean a, final boolean b, final boolean c, final boolean d, final boolean e, final Mode mode) {
            var status = (byte) mode.value;
            status |= a ? 0x80 : 0x00;
            status |= b ? 0x40 : 0x00;
            status |= c ? 0x20 : 0x00;
            status |= d ? 0x10 : 0x00;
            status |= e ? 0x08 : 0x00;
            return status;
        }

        /**
         * Returns given params as byte array
         *
         * @param a
         * @param b
         * @param c
         * @param d
         * @param e
         * @param mode
         * @return byte array
         */
    public static byte[] toByteArray(final boolean a, final boolean b, final boolean c, final boolean d, final boolean e, final Mode mode) {
            return new byte[]{toByte(a, b, c, d, e, mode)};
        }

        public boolean isSet(final int bit) {
            if (bit < 0 || bit > 4) {
                throw new KnxNumberOutOfRangeException("bit", 0, 4, bit);
            }
            return (this.b & (0x80 >> bit)) != 0;
        }


        public Mode getMode() {
            return Mode.of(this.b & 0x07);
        }

    @Override
        public byte[] toByteArray() {
            return new byte[]{this.b};
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof StatusMode) {
                final var other = (StatusMode) obj;
                return Objects.equals(this.getMode(), other.getMode()) && this.b == other.b;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getMode(), this.b);
        }

        /**
         * Mode used for {@link StatusMode}
         *
         * @author PITSCHR
         */
        public enum Mode {
            /**
             * Mode 0
             */
            MODE_0(0x01),
            /**
             * Mode 1
             */
            MODE_1(0x02),
            /**
             * Mode 2
             */
            MODE_2(0x04);

            private int value;

            Mode(final int value) {
                this.value = value;
            }

            private static Mode of(final int code) {
                if (code == Mode.MODE_0.value) {
                    return Mode.MODE_0;
                } else if (code == Mode.MODE_1.value) {
                    return Mode.MODE_1;
                } else if (code == Mode.MODE_2.value) {
                    return Mode.MODE_2;
                } else {
                    throw new KnxEnumNotFoundException(Mode.class, code);
                }
            }
        }
    }
}
