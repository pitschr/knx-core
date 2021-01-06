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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DataPointEnumValue;

/**
 * Data Point Type 23 for '2-Bit Boolean' (2 Bit)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Field 1)                     |
 * Encoding    |                         N   N |
 *             +---+---+---+---+---+---+---+---+
 * Format:     2 bits (B<sub>2</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT23 {
    private DPT23() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * <strong>23.001</strong> Action On/Off
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 3]
     *                  0 = Off
     *                  1 = On
     *                  2 = Off/On
     *                  3 = On/Off
     * </pre>
     */
    @DataPoint(value = {"23.001", "dpst-23-1"}, description = "Action On/Off")
    public enum ActionOnOff implements DataPointEnum<ActionOnOff> {
        @DataPointEnumValue(value = 0, description = "Off")
        OFF, //
        @DataPointEnumValue(value = 1, description = "On")
        ON, //
        @DataPointEnumValue(value = 2, description = "Off/On")
        OFF_ON, //
        @DataPointEnumValue(value = 3, description = "On/Off")
        ON_OFF
    }

    /**
     * <strong>23.002</strong> Alarm Reaction
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = No alarm is used
     *                  1 = Alarm position is UP
     *                  2 = Alarm position is DOWN
     * </pre>
     */
    @DataPoint(value = {"23.002", "dpst-23-2"}, description = "Alarm Reaction")
    public enum AlarmReaction implements DataPointEnum<AlarmReaction> {
        @DataPointEnumValue(value = 0, description = "No Alarm")
        NO_ALARM, //
        @DataPointEnumValue(value = 1, description = "Alarm Position UP")
        ALARM_POSITION_UP, //
        @DataPointEnumValue(value = 2, description = "Alarm Position DOWN")
        ALARM_POSITION_DOWN
    }

    /**
     * <strong>23.003</strong> Action Up/Down
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 3]
     *                  0 = Up
     *                  1 = Down
     *                  2 = Up/Down
     *                  3 = Down/Up
     * </pre>
     */
    @DataPoint(value = {"23.003", "dpst-23-3"}, description = "Action Up/Down")
    public enum ActionUpDown implements DataPointEnum<ActionUpDown> {
        @DataPointEnumValue(value = 0, description = "Up")
        UP, //
        @DataPointEnumValue(value = 1, description = "Down")
        DOWN, //
        @DataPointEnumValue(value = 2, description = "Up/Down")
        UP_DOWN, //
        @DataPointEnumValue(value = 3, description = "Down/Up")
        DOWN_UP
    }

    // TODO: ETS 23.102 HVAC Push Button Action?
}
