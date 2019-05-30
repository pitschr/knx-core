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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.knx.link.datapoint.annotation.KnxDataPointType;
import li.pitschmann.knx.link.datapoint.value.DPT1Value;

/**
 * Data Point Type 1 for 'Boolean' (1 Bit)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names |                             b |
 * Encoding    |                             B |
 *             +---+---+---+---+---+---+---+---+
 * Format:     1 bit (B<sub>1</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT1 extends AbstractDataPointType<DPT1Value> {
    /**
     * <strong>1.001</strong> Switch
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = off, 1 = on}
     * </pre>
     */
    @KnxDataPointType(id = "1.001", description = "Switch")
    public static final DPT1 SWITCH = new DPT1("1.001", "Switch", "off", "on");
    /**
     * <strong>1.002</strong> Boolean
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = false, 1 = true}
     * </pre>
     */
    @KnxDataPointType(id = "1.002", description = "Boolean")
    public static final DPT1 BOOL = new DPT1("1.002", "Boolean", "false", "true");
    /**
     * <strong>1.003</strong> Enable
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = disable, 1 = enable}
     * </pre>
     */
    @KnxDataPointType(id = "1.003", description = "Enable")
    public static final DPT1 ENABLE = new DPT1("1.003", "Enable", "Disable", "Enable");
    /**
     * <strong>1.004</strong> Ramp
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = no ramp, 1 = ramp}
     * </pre>
     */
    @KnxDataPointType(id = "1.004", description = "Ramp")
    public static final DPT1 RAMP = new DPT1("1.004", "Ramp", "No Ramp", "Ramp");
    /**
     * <strong>1.005</strong> Alarm
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = no alarm, 1 = alarm}
     * </pre>
     */
    @KnxDataPointType(id = "1.005", description = "Alarm")
    public static final DPT1 ALARM = new DPT1("1.005", "Alarm", "No Alarm", "Alarm");
    /**
     * <strong>1.006</strong> Binary Value
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = low, 1 = high}
     * </pre>
     */
    @KnxDataPointType(id = "1.006", description = "Binary Value")
    public static final DPT1 BINARY_VALUE = new DPT1("1.006", "Binary Value", "Low", "High");
    /**
     * <strong>1.007</strong> Step
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = decrease, 1 = increase}
     * </pre>
     */
    @KnxDataPointType(id = "1.007", description = "Step")
    public static final DPT1 STEP = new DPT1("1.007", "Step", "Decrease", "Increase");
    /**
     * <strong>1.008</strong> Up & Down
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = up, 1 = down}
     * </pre>
     */
    @KnxDataPointType(id = "1.008", description = "Up & Down")
    public static final DPT1 UP_DOWN = new DPT1("1.008", "Up & Down", "Up", "Down");
    /**
     * <strong>1.009</strong> Open & Close
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = open, 1 = close}
     * </pre>
     */
    @KnxDataPointType(id = "1.009", description = "Open & Close")
    public static final DPT1 OPEN_CLOSE = new DPT1("1.009", "Open & Close", "Open", "Close");
    /**
     * <strong>1.010</strong> Start
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = stop, 1 = start}
     * </pre>
     */
    @KnxDataPointType(id = "1.010", description = "Start")
    public static final DPT1 START = new DPT1("1.010", "Start", "Stop", "Start");
    /**
     * <strong>1.011</strong> State
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = inactive, 1 = active}
     * </pre>
     */
    @KnxDataPointType(id = "1.011", description = "State")
    public static final DPT1 STATE = new DPT1("1.011", "State", "Inactive", "Active");
    /**
     * <strong>1.012</strong> Invert
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = not inverted, 1 = inverted}
     * </pre>
     */
    @KnxDataPointType(id = "1.012", description = "Invert")
    public static final DPT1 INVERT = new DPT1("1.012", "Invert", "Not Inverted", "Inverted");
    /**
     * <strong>1.013</strong> Dim Send Style
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = start/stop, 1 = cyclically}
     * </pre>
     */
    @KnxDataPointType(id = "1.013", description = "Dim Send Style")
    public static final DPT1 DIM_SEND_STYLE = new DPT1("1.013", "Dim Send Style", "Start/Stop", "Cyclically");
    /**
     * <strong>1.014</strong> Input Source
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = fixed, 1 = calculated}
     * </pre>
     */
    @KnxDataPointType(id = "1.014", description = "Input Source")
    public static final DPT1 INPUT_SOURCE = new DPT1("1.014", "Input Source", "Fixed", "Calculated");
    /**
     * <strong>1.015</strong> Reset
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = no action, 1 = reset}
     * </pre>
     */
    @KnxDataPointType(id = "1.015", description = "Reset")
    public static final DPT1 RESET = new DPT1("1.015", "Reset", "No Action", "Reset");
    /**
     * <strong>1.016</strong> Acknowledge
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = no action, 1 = acknowledge}
     * </pre>
     */
    @KnxDataPointType(id = "1.016", description = "Acknowledge")
    public static final DPT1 ACK = new DPT1("1.016", "Acknowledge", "No Action", "Acknowledge");
    /**
     * <strong>1.017</strong> Trigger
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0, 1}
     *
     * For DPT_Trigger, both values 0 and 1 shall have the same effect and shall not be differentiated in sender or receiver.
     * </pre>
     */
    @KnxDataPointType(id = "1.017", description = "Trigger")
    public static final DPT1 TRIGGER = new DPT1("1.017", "Trigger", "Trigger (0)", "Trigger (1)");
    /**
     * <strong>1.018</strong> Occupancy
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = not occupied, 1 = occupied}
     * </pre>
     */
    @KnxDataPointType(id = "1.018", description = "Occupancy")
    public static final DPT1 OCCUPANCY = new DPT1("1.018", "Occupancy", "Not Occupied", "Occupied");
    /**
     * <strong>1.019</strong> Window & Door
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = closed, 1 = open}
     * </pre>
     */
    @KnxDataPointType(id = "1.019", description = "Window & Door")
    public static final DPT1 WINDOW_DOOR = new DPT1("1.019", "Window & Door", "Closed", "Open");
    /**
     * <strong>1.021</strong> Logical Function
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = or, 1 = and}
     * </pre>
     */
    @KnxDataPointType(id = "1.021", description = "Logical Function")
    public static final DPT1 LOGICAL_FUNCTION = new DPT1("1.021", "Logical Function", "OR", "AND");
    /**
     * <strong>1.022</strong> Scene
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = scene A, 1 = scene B}
     *
     * DPT_Scene_AB allows numbering the scenes with 0 and 1.
     * </pre>
     */
    @KnxDataPointType(id = "1.022", description = "Scene")
    public static final DPT1 SCENE_AB = new DPT1("1.022", "Scene", "Scene (A)", "Scene (B)");
    /**
     * <strong>1.023</strong> Shutter & Blinds Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = only move up/down, 1 = move up/down + step/stop}
     * </pre>
     */
    @KnxDataPointType(id = "1.023", description = "Shutter & Blinds Mode")
    public static final DPT1 SHUTTER_BLINDS_MODE = new DPT1("1.023", "Shutter & Blinds Mode", "Only Move Up/Down (shutter)",
            "Move Up/Down + Step/Stop (blind)");

    /**
     * <strong>1.024</strong> Day & Night
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = Day, 1 = Night}
     * </pre>
     */
    @KnxDataPointType(id = "1.024", description = "Day & Night")
    public static final DPT1 DAY_NIGHT = new DPT1("1.024", "Day & Night", "Day", "Night");

    /**
     * <strong>1.100</strong> Heating & Cooling
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                             b |
     * Encoding    |                             B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 bit (B<sub>1</sub>)
     * Range:      b = {0 = cooling, 1 = heating}
     * </pre>
     */
    @KnxDataPointType(id = "1.100", description = "Heating & Cooling")
    public static final DPT1 HEAT_COOL = new DPT1("1.100", "Heating & Cooling", "Cooling", "Heating");

    private final String textForFalse;
    private final String textForTrue;

    /**
     * Constructor for {@link DPT1}
     *
     * @param id
     * @param description
     * @param textForFalse
     * @param textForTrue
     */
    private DPT1(final String id, final String description, final String textForFalse, final String textForTrue) {
        super(id, description);
        this.textForFalse = textForFalse;
        this.textForTrue = textForTrue;
    }

    public String getTextFor(final boolean value) {
        return value ? this.textForTrue : this.textForFalse;
    }

    public String getTextForFalse() {
        return this.textForFalse;
    }

    public String getTextForTrue() {
        return this.textForTrue;
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 1 && (bytes[0] >>> 1) == 0x00;
    }

    @Override
    protected DPT1Value parse(final byte[] bytes) {
        return new DPT1Value(this, bytes[0]);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    public DPT1Value parse(final String[] args) {
        // true if 'true' or '1' or DPT related true value (e.g. switch => 'on'), otherwise false
        final var boolValue = this.findByString(args, "true", "1", this.getTextForTrue());
        return new DPT1Value(this, boolValue);
    }

    public DPT1Value toValue(final boolean booleanValue) {
        return new DPT1Value(this, booleanValue);
    }

    public byte[] toByteArray(final boolean booleanValue) {
        return DPT1Value.toByteArray(booleanValue);
    }
}
