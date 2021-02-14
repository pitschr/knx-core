/*
 * KNX Link - A library for KNX Net/IP communication
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

import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;

/**
 * Step Interval enumeration
 * <p>
 * The step interval is shared by several data point types like DPT3, DPT250, DPT252, ...
 *
 * @author PITSCHR
 */
public enum StepInterval {
    /**
     * Step 000b: STOP
     */
    STOP((byte) 0x00, "Stop"),
    /**
     * Step 001b: 100%
     */
    PERCENT_100((byte) 0x01, "100%"),
    /**
     * Step 010b: 50%
     */
    PERCENT_50((byte) 0x02, "50%"),
    /**
     * Step 011b: 25%
     */
    PERCENT_25((byte) 0x03, "25%"),
    /**
     * Step 100b: 12%
     */
    PERCENT_12((byte) 0x04, "12%"),
    /**
     * Step 101b: 6%
     */
    PERCENT_6((byte) 0x05, "6%"),
    /**
     * Step 110b: 3%
     */
    PERCENT_3((byte) 0x06, "3%"),
    /**
     * Step 111b: 1%
     */
    PERCENT_1((byte) 0x07, "1%");

    private final byte stepByte;
    private final String text;

    StepInterval(final byte stepByte, final String text) {
        this.stepByte = stepByte;
        this.text = text;
    }

    /**
     * Parses the String and returns the most suitable {@link StepInterval}
     * <p>
     * Look up Path, from top to bottom:
     * <ul>
     *     <li>Look up by "stop" text</li>
     *     <li>Look up by {@link StepInterval#getText()}</li>
     *     <li>Look up by value with percent sign {@link StepInterval#ofPercent(double)} (dot and comma supported as separator)</li>
     *     <li>Look up by step interval as digit-only using {@link StepInterval#ofInterval(int)}</li>
     * </ul>
     *
     * @param stepString the step as string; may not be null
     * @return {@link StepInterval}
     * @throws NumberFormatException        if the value could not be parsed at last step (digit-only)
     * @throws KnxNumberOutOfRangeException if the provided integer is out of supported range
     */
    public static StepInterval parse(final String stepString) {
        // look up by text
        if ("stop".equalsIgnoreCase(stepString)) {
            return StepInterval.STOP;
        }

        // Look up by text
        for (var value : values()) {
            if (value.getText().equals(stepString)) {
                return value;
            }
        }
        // Look up by percent sign
        if (stepString.endsWith("%")) {
            final var stepStringFixed = stepString
                    .substring(0, stepString.length() - 1) // remove '%'
                    .replace(',', '.');  // we support ',' as equivalent to '.'
            return ofPercent(Double.parseDouble(stepStringFixed));
        }
        // Last station: Look up by digits only
        final var intValue = Integer.parseInt(stepString);
        return ofInterval(intValue);
    }

    /**
     * Returns the {@link StepInterval} by byte.
     * This shall be used by internally only.
     *
     * @param stepAsByte the step code as byte [000..111]
     * @return {@link StepInterval}
     */
    public static StepInterval ofByte(final byte stepAsByte) {
        // validate
        if (stepAsByte < 0 || stepAsByte > 7) {
            throw new KnxNumberOutOfRangeException("stepAsByte", 0, 7, stepAsByte);
        }
        return values()[stepAsByte];
    }

    /**
     * Returns the {@link StepInterval} by {@code interval}.
     * <p>
     * The formula of step interval: {@code 2^(step-1)}
     *
     * @param interval number of interval in range of [0 .. 64]
     * @return {@link StepInterval}
     */
    public static StepInterval ofInterval(final int interval) {
        // validate
        if (interval < 0 || interval > 64) {
            throw new KnxNumberOutOfRangeException("interval", 0, 64, interval);
        }

        // return by interval
        // 1% => 33 .. 64 = 2^(7-1)
        if (interval > 32) {
            return PERCENT_1;
        }
        // 3% => 17 .. 32 = 2^(6-1)
        else if (interval > 16) {
            return PERCENT_3;
        }
        // 6% => 9 .. 16 = 2^(5-1)
        else if (interval > 8) {
            return PERCENT_6;
        }
        // 12% => 5 .. 8 = 2^(4-1)
        else if (interval > 4) {
            return PERCENT_12;
        }
        // 25% => 3 .. 4 = 2^(3-1)
        else if (interval == 3 || interval == 4) {
            return PERCENT_25;
        }
        // 50% => 2 = 2^(2-1)
        else if (interval == 2) {
            return PERCENT_50;
        }
        // 100% => 1 = 2^(1-1)
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
     * @param percent number of percent in range of [0% .. 100.00%]
     * @return {@link StepInterval}
     */
    public static StepInterval ofPercent(final double percent) {
        // validate
        if (percent < 0 || percent > 100) {
            throw new KnxNumberOutOfRangeException("percent", 0.0, 100.0, percent);
        }

        // return by percent
        // 100% => 75 .. 100
        if (percent >= 75.0) {
            return PERCENT_100;
        }
        // 50% => 37.5 .. 74.9
        else if (percent >= 37.5) {
            return PERCENT_50;
        }
        // 25% => 18.5 .. 37.4
        else if (percent >= 18.5) {
            return PERCENT_25;
        }
        // 12% => 9 .. 18.4
        else if (percent >= 9) {
            return PERCENT_12;
        }
        // 6% => 4.5 .. 8.9
        else if (percent >= 4.5) {
            return PERCENT_6;
        }
        // 3% => 2.2 .. 4.4
        else if (percent >= 2.2) {
            return PERCENT_3;
        }
        // 1% => 0.01 .. 2.1
        else if (percent >= 0.01) {
            return PERCENT_1;
        }
        // stop if lower than 0.01
        else {
            return STOP;
        }
    }

    /**
     * Returns the step as byte
     *
     * @return byte
     */
    public byte getByte() {
        return stepByte;
    }

    /**
     * Returns the human-friendly text representation of Step Interval
     * <p>
     * <strong>This text is not STABLE and should only be used for display purposes only!</strong>
     *
     * @return human-friendly representation of Step Interval
     */
    public String getText() {
        return text;
    }
}
