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
import li.pitschmann.knx.link.datapoint.value.DPT2Value;

/**
 * Data Point Type 2 for 'Controlled Boolean' (2 Bit)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names |                         c   b |
 * Encoding    |                         C   B |
 *             +---+---+---+---+---+---+---+---+
 * Format:     2 bits (B<sub>2</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT2 extends AbstractDataPointType<DPT2Value> {
    /**
     * <strong>2.001</strong> Switch Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = off, 1 = on}
     * </pre>
     */
    @KnxDataPointType(id = "2.001", description = "Switch Controlled")
    public static final DPT2 SWITCH_CONTROL = new DPT2("2.001", "Switch Controlled", DPT1.SWITCH);
    /**
     * <strong>2.002</strong> Boolean Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = false, 1 = true}
     * </pre>
     */
    @KnxDataPointType(id = "2.002", description = "Boolean Controlled")
    public static final DPT2 BOOL_CONTROL = new DPT2("2.002", "Boolean Controlled", DPT1.BOOL);
    /**
     * <strong>2.003</strong> Enable Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = disable, 1 = enable}
     * </pre>
     */
    @KnxDataPointType(id = "2.003", description = "Enable Controlled")
    public static final DPT2 ENABLE_CONTROL = new DPT2("2.003", "Enable Controlled", DPT1.ENABLE);
    /**
     * <strong>2.004</strong> Ramp Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = no ramp, 1 = ramp}
     * </pre>
     */
    @KnxDataPointType(id = "2.004", description = "Ramp Controlled")
    public static final DPT2 RAMP_CONTROL = new DPT2("2.004", "Ramp Controlled", DPT1.RAMP);
    /**
     * <strong>2.005</strong> Alarm Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = no alarm, 1 = alarm}
     * </pre>
     */
    @KnxDataPointType(id = "2.005", description = "Alarm Controlled")
    public static final DPT2 ALARM_CONTROL = new DPT2("2.005", "Alarm Controlled", DPT1.ALARM);
    /**
     * <strong>2.006</strong> Binary Value Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = low, 1 = high}
     * </pre>
     */
    @KnxDataPointType(id = "2.006", description = "Binary Value Controlled")
    public static final DPT2 BINARY_VALUE_CONTROL = new DPT2("2.006", "Binary Value Controlled", DPT1.BINARY_VALUE);
    /**
     * <strong>2.007</strong> Step Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = decrease, 1 = increase}
     * </pre>
     */
    @KnxDataPointType(id = "2.007", description = "Step Controlled")
    public static final DPT2 STEP_CONTROL = new DPT2("2.007", "Step Controlled", DPT1.STEP);
    /**
     * <strong>2.008</strong> Up & Down Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = up, 1 = down}
     * </pre>
     */
    @KnxDataPointType(id = "2.008", description = "Up & Down Controlled")
    public static final DPT2 UP_DOWN_CONTROL = new DPT2("2.008", "Up & Down Controlled", DPT1.UP_DOWN);
    /**
     * <strong>2.009</strong> Open & Close Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = open, 1 = close}
     * </pre>
     */
    @KnxDataPointType(id = "2.009", description = "Open & Close Controlled")
    public static final DPT2 OPEN_CLOSE_CONTROL = new DPT2("2.009", "Open & Close Controlled", DPT1.OPEN_CLOSE);
    /**
     * <strong>2.010</strong> Start Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = stop, 1 = start}
     * </pre>
     */
    @KnxDataPointType(id = "2.010", description = "Start Controlled")
    public static final DPT2 START_CONTROL = new DPT2("2.010", "Start Controlled", DPT1.START);
    /**
     * <strong>2.011</strong> State Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = inactive, 1 = active}
     * </pre>
     */
    @KnxDataPointType(id = "2.011", description = "State Controlled")
    public static final DPT2 STATE_CONTROL = new DPT2("2.011", "State Controlled", DPT1.STATE);
    /**
     * <strong>2.012</strong> Invert Controlled
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                         c   v |
     * Encoding    |                         B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     2 bits (B<sub>2</sub>)
     * Range:      c = {0 = no control, 1 = control}
     *             v = {0 = not inverted, 1 = inverted}
     * </pre>
     */
    @KnxDataPointType(id = "2.012", description = "Invert Controlled")
    public static final DPT2 INVERT_CONTROL = new DPT2("2.012", "Invert Controlled", DPT1.INVERT);

    private final DPT1 dpt1;

    /**
     * Constructor for {@link DPT2}
     *
     * @param id
     * @param description
     * @param dpt1
     */
    private DPT2(final String id, final String description, final DPT1 dpt1) {
        super(id, description);
        this.dpt1 = dpt1;
    }

    public DPT1 getDPT1() {
        return this.dpt1;
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 1 && (bytes[0] >>> 2) == 0x00;
    }

    @Override
    protected DPT2Value parse(final byte[] bytes) {
        return new DPT2Value(this, bytes[0]);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1 || args.length == 2;
    }

    @Override
    protected DPT2Value parse(final String[] args) {
        // true if 'true' or '1' or DPT related true value (e.g. switch => 'on'), otherwise false
        boolean boolValue = this.findByString(args, "true", "1", this.getDPT1().getTextForTrue());
        boolean controlled = this.findByString(args, "controlled");

        return new DPT2Value(this, controlled, boolValue);
    }

    public DPT2Value toValue(final boolean controlled, final boolean booleanValue) {
        return new DPT2Value(this, controlled, booleanValue);
    }

    public byte[] toByteArray(final boolean controlled, final boolean booleanValue) {
        return DPT2Value.toByteArray(controlled, booleanValue);
    }

}
