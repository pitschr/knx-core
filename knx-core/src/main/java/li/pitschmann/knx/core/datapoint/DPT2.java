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

import li.pitschmann.knx.core.datapoint.value.DPT2Value;

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
    @DataPoint({"2.001", "dpt-2", "dpst-2-1"})
    public static final DPT2 SWITCH_CONTROL = new DPT2("Switch Controlled", DPT1.SWITCH);
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
    @DataPoint({"2.002", "dpst-2-2"})
    public static final DPT2 BOOL_CONTROL = new DPT2("Boolean Controlled", DPT1.BOOL);
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
    @DataPoint({"2.003", "dpst-2-3"})
    public static final DPT2 ENABLE_CONTROL = new DPT2("Enable Controlled", DPT1.ENABLE);
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
    @DataPoint({"2.004", "dpst-2-4"})
    public static final DPT2 RAMP_CONTROL = new DPT2("Ramp Controlled", DPT1.RAMP);
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
    @DataPoint({"2.005", "dpst-2-5"})
    public static final DPT2 ALARM_CONTROL = new DPT2("Alarm Controlled", DPT1.ALARM);
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
    @DataPoint({"2.006", "dpst-2-6"})
    public static final DPT2 BINARY_VALUE_CONTROL = new DPT2("Binary Value Controlled", DPT1.BINARY_VALUE);
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
    @DataPoint({"2.007", "dpst-2-7"})
    public static final DPT2 STEP_CONTROL = new DPT2("Step Controlled", DPT1.STEP);
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
    @DataPoint({"2.008", "dpst-2-8"})
    public static final DPT2 UP_DOWN_CONTROL = new DPT2("Up & Down Controlled", DPT1.UP_DOWN);
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
    @DataPoint({"2.009", "dpst-2-9"})
    public static final DPT2 OPEN_CLOSE_CONTROL = new DPT2("Open & Close Controlled", DPT1.OPEN_CLOSE);
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
    @DataPoint({"2.010", "dpst-2-10"})
    public static final DPT2 START_CONTROL = new DPT2("Start Controlled", DPT1.START);
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
    @DataPoint({"2.011", "dpst-2-11"})
    public static final DPT2 STATE_CONTROL = new DPT2("State Controlled", DPT1.STATE);
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
    @DataPoint({"2.012", "dpst-2-12"})
    public static final DPT2 INVERT_CONTROL = new DPT2("Invert Controlled", DPT1.INVERT);

    private final DPT1 dpt1;

    /**
     * Constructor for {@link DPT2}
     *
     * @param description description for {@link DPT2}
     * @param dpt1        the basis {@link DPT1} for {@link DPT2}
     */
    private DPT2(final String description,
                 final DPT1 dpt1) {
        super(description);
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
        final var boolValue = this.findByString(args, "true", "1", this.getDPT1().getTextForTrue());
        final var controlled = this.findByString(args, "controlled");

        return new DPT2Value(this, controlled, boolValue);
    }

    public DPT2Value toValue(final boolean controlled, final boolean booleanValue) {
        return new DPT2Value(this, controlled, booleanValue);
    }

    public byte[] toByteArray(final boolean controlled, final boolean booleanValue) {
        return DPT2Value.toByteArray(controlled, booleanValue);
    }

}
