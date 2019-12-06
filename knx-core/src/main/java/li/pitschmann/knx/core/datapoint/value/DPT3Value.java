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

import li.pitschmann.knx.core.datapoint.DPT3;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT3} (3.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  |                 c   x   x   x |
 * Encoding     |                 B   U   U   U |
 *              +---+---+---+---+---+---+---+---+
 * Format:     3 bits (B<sub>1</sub> U<sub>3</sub>)
 * Range:      c = {0 = no control, 1 = control}
 *             x = step code { 0 = break, 1 .. 7 = step }
 *                 000b = break
 *                 001b .. 111b = step
 * </pre>
 * <p>
 * The amount of intervals into which the range of 0 % .. 100 % is subdivided, or the break indication.
 *
 * @author PITSCHR
 */
public final class DPT3Value extends AbstractDataPointValue<DPT3> {
    private final boolean controlled;
    private final int stepCode;

    public DPT3Value(final DPT3 dpt, final byte b) {
        super(dpt);
        // bit 4 = controlled
        this.controlled = (b & 0x08) != 0x00;
        // bit 0 .. 3 = stepCode
        this.stepCode = b & 0x07;
    }

    public DPT3Value(final DPT3 dpt, final boolean controlled, final int stepCode) {
        super(dpt);
        // validate
        if (stepCode < 0 || stepCode > 7) {
            throw new KnxNumberOutOfRangeException("stepCode", 0, 7, stepCode);
        }

        this.controlled = controlled;
        this.stepCode = stepCode;
    }

    public DPT3Value(final DPT3 dpt, final boolean controlled, final StepInterval stepInterval) {
        super(dpt);
        // validate
        if (stepInterval == null) {
            throw new KnxNullPointerException("stepInterval");
        }

        this.controlled = controlled;
        this.stepCode = stepInterval.getStepCode();
    }

    /**
     * Converts {@code controlled} and {@code booleanValue} to byte array
     *
     * @param controlled
     * @param stepCode
     * @return byte array
     */
    public static byte[] toByteArray(final boolean controlled, final int stepCode) {
        var b = (byte) stepCode;
        if (controlled) {
            b |= 0x08;
        }
        return new byte[]{b};
    }

    public boolean isControlled() {
        return this.controlled;
    }

    public int getStepCode() {
        return this.stepCode;
    }


    public StepInterval getStepInterval() {
        return StepInterval.ofCode(this.stepCode);
    }

    @Override
    public byte[] toByteArray() {
        return toByteArray(this.controlled, this.stepCode);
    }

    @Override
    public String toText() {
        final var stepIntervalText = getStepInterval().toText();
        if (isControlled()) {
            return String.format("controlled '%s'", stepIntervalText);
        } else {
            return stepIntervalText;
        }
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("controlled", this.controlled)
                .add("stepCode", this.stepCode)
                .add("stepInterval", this.getStepInterval())
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT3Value) {
            final var other = (DPT3Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.controlled, other.controlled) //
                    && Objects.equals(this.stepCode, other.stepCode);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.controlled, this.stepCode);
    }

    /**
     * Step Interval enumeration
     *
     * @author PITSCHR
     */
    public enum StepInterval {
        /**
         * Step 000b: STOP
         */
        STOP(0x00, "0 (stop)"),
        /**
         * Step 001b: 100%
         */
        PERCENT_100(0x01, "1 (100%)"),
        /**
         * Step 010b: 50%
         */
        PERCENT_50(0x02, "2 (50%)"),
        /**
         * Step 011b: 25%
         */
        PERCENT_25(0x03, "3 (25%)"),
        /**
         * Step 100b: 12%
         */
        PERCENT_12(0x04, "4 (12%)"),
        /**
         * Step 101b: 6%
         */
        PERCENT_6(0x05, "5 (6%)"),
        /**
         * Step 110b: 3%
         */
        PERCENT_3(0x06, "6 (3%)"),
        /**
         * Step 111b: 1%
         */
        PERCENT_1(0x07, "7 (1%)");

        private final int stepCode;
        private final String text;

        StepInterval(final int stepCode, final String text) {
            this.stepCode = stepCode;
            this.text = text;
        }

        /**
         * Returns the {@link StepInterval} by {@code stepCode}.
         *
         * @param stepCode
         * @return {@link StepInterval}
         */
        public static StepInterval ofCode(final int stepCode) {
            // validate
            if (stepCode < 0 || stepCode > 7) {
                throw new KnxNumberOutOfRangeException("stepCode", 0, 7, stepCode);
            }

            return values()[stepCode];
        }

        /**
         * Returns the {@link StepInterval} by {@code interval}.
         *
         * @param interval number of interval in range of [1 .. 64]
         * @return {@link StepInterval}
         */
        public static StepInterval ofInterval(final int interval) {
            // validate
            if (interval < 0 || interval > 64) {
                throw new KnxNumberOutOfRangeException("interval", 0, 64, interval);
            }

            // return by interval
            // 1% => 49 .. 64
            if (interval > 48) {
                return PERCENT_1;
            }
            // 3% => 25 .. 48
            else if (interval > 24) {
                return PERCENT_3;
            }
            // 6% => 13 .. 24
            else if (interval > 12) {
                return PERCENT_6;
            }
            // 12% => 7 .. 12
            else if (interval > 6) {
                return PERCENT_12;
            }
            // 25% => 4 .. 6
            else if (interval > 3) {
                return PERCENT_25;
            }
            // 50% => 2 .. 3
            else if (interval > 1) {
                return PERCENT_50;
            }
            // 100% => 1
            else if (interval == 1) {
                return PERCENT_100;
            }
            // otherwise it is 0
            else {
                return STOP;
            }
        }

        /**
         * Returns the {@link StepInterval} by {@code percent}.
         *
         * @param percent number of percent in range of [1f .. 100f]
         * @return {@link StepInterval}
         */
        public static StepInterval ofPercent(final float percent) {
            // validate
            if (percent < 0f || percent > 100f) {
                throw new KnxNumberOutOfRangeException("percent", 0f, 100f, percent);
            }

            // return by percent
            // 100% => 75 .. 100
            if (percent >= 75f) {
                return PERCENT_100;
            }
            // 50% => 37.5 .. 74.9
            else if (percent >= 37.5f) {
                return PERCENT_50;
            }
            // 25% => 18.5 .. 37.4
            else if (percent >= 18.5f) {
                return PERCENT_25;
            }
            // 12% => 9 .. 18.4
            else if (percent >= 9f) {
                return PERCENT_12;
            }
            // 6% => 4.5 .. 8.9
            else if (percent >= 4.5f) {
                return PERCENT_6;
            }
            // 3% => 2.2 .. 4.4
            else if (percent >= 2.2f) {
                return PERCENT_3;
            }
            // 1% => 0.01 .. 2.1
            else if (percent >= 0.01f) {
                return PERCENT_1;
            }
            // stop if lower than 0.01
            else {
                return STOP;
            }
        }

        public int getStepCode() {
            return this.stepCode;
        }

        /**
         * Returns the human-friendly text representation of Step Interval
         * <p/>
         * <strong>This text is not STABLE and should only be used for display purposes only!</strong>
         *
         * @return human-friendy representation of Step Interval
         */
        public String toText() {
            return this.text;
        }
    }
}
