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
import li.pitschmann.knx.core.datapoint.DPT6;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

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
    private final int value;

    public DPT6Value(final DPT6 dpt, final byte b) {
        this(dpt, (int)b);
    }

    public DPT6Value(final DPT6 dpt, final int value) {
        super(dpt);
        if (!getDPT().isRangeClosed(value)) {
            throw new KnxNumberOutOfRangeException("value", getDPT().getLowerValue(), getDPT().getUpperValue(), value);
        }

        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{(byte) value};
    }

    @Override
    public String toText() {
        return getValueAsText(value);
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("value", value)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
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
                    && Objects.equals(this.value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), value);
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
    public static final class StatusMode extends AbstractDataPointValue<DPT6.StatusMode> {
        private final boolean a;
        private final boolean b;
        private final boolean c;
        private final boolean d;
        private final boolean e;
        private final Mode mode;

        public StatusMode(final byte b) {
            this(
                    // boolean: a
                    (b & 0x80) != 0x00,
                    // boolean: b
                    (b & 0x40) != 0x00,
                    // boolean: c
                    (b & 0x20) != 0x00,
                    // boolean: d
                    (b & 0x10) != 0x00,
                    // boolean e
                    (b & 0x08) != 0x00,
                    // mode
                    Mode.of(b & 0x07)
            );
        }

        public StatusMode(final boolean a, final boolean b, final boolean c, final boolean d, final boolean e, final Mode mode) {
            super(DPT6.STATUS_MODE); // hardcoded

            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.mode = Objects.requireNonNull(mode);
        }

        public boolean isSet(final int bit) {
            switch(bit) {
                case 0: return a;
                case 1: return b;
                case 2: return c;
                case 3: return d;
                case 4: return e;
                default:
                    throw new KnxNumberOutOfRangeException("bit", 0, 4, bit);
            }
        }

        public Mode getMode() {
            return mode;
        }

        @Override
        public byte[] toByteArray() {
            var status = (byte) mode.value;
            status |= a ? 0x80 : 0x00;
            status |= b ? 0x40 : 0x00;
            status |= c ? 0x20 : 0x00;
            status |= d ? 0x10 : 0x00;
            status |= e ? 0x08 : 0x00;
            return new byte[]{status};
        }

        @Override
        public String toString() {
            // @formatter:off
            return Strings.toStringHelper(this)
                    .add("dpt", getDPT().getId())
                    .add("a", a)
                    .add("b", b)
                    .add("c", c)
                    .add("d", d)
                    .add("e", e)
                    .add("mode", getMode().name())
                    .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                    .toString();
            // @formatter:on
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof StatusMode) {
                final var other = (StatusMode) obj;
                return Objects.equals(this.getMode(), other.getMode())
                        && this.a == other.a
                        && this.b == other.b
                        && this.c == other.c
                        && this.d == other.d
                        && this.e == other.e;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getMode(), a, b, c, d, e);
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
