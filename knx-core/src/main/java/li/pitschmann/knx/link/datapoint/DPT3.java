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
import li.pitschmann.knx.link.datapoint.value.DPT3Value;
import li.pitschmann.knx.link.datapoint.value.DPT3Value.StepInterval;

import java.util.regex.Pattern;

/**
 * Data Point Type 3 for 'Controlled Dimming/Blinds' (3 Bits)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names |                 c   x   x   x |
 * Encoding    |                 B   U   U   U |
 *             +---+---+---+---+---+---+---+---+
 * Format:     3 bits (B U<sub>3</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT3 extends AbstractDataPointType<DPT3Value> {

    /**
     * <strong>3.007</strong> Control Dimming (%)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                 c   x   x   x |
     * Encoding    |                 B   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     4 bits (B U<sub>3</sub>)
     * Range:      c = {0 = decrease, 1 = increase}
     *             x = step code { 0 = break, 1 .. 7 = step }
     *                 000b = break
     *                 001b .. 111b = step
     *
     * The amount of intervals into which the range of 0-100% is subdivided, or the break indication.
     * </pre>
     */
    @KnxDataPointType(id = "3.007", description = "Control Dimming")
    public static final DPT3 DPT_CONTROL_DIMMING = new DPT3("3.007", "Control Dimming", DPT1.STEP);

    /**
     * <strong>3.008</strong> Control Blinds (%)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names |                 c   x   x   x |
     * Encoding    |                 B   U   U   U |
     *             +---+---+---+---+---+---+---+---+
     * Format:     4 bits (B U<sub>3</sub>)
     * Range:      c = {0 = up, 1 = down}
     *             x = step code { 0 = break, 1 .. 7 = step }
     *                 000b = break
     *                 001b .. 111b = step
     *
     * The amount of intervals into which the range of 0-100% is subdivided, or the break indication.
     * </pre>
     */
    @KnxDataPointType(id = "3.008", description = "Control Blinds")
    public static final DPT3 DPT_CONTROL_BLINDS = new DPT3("3.008", "Control Blinds", DPT1.UP_DOWN);

    private final DPT1 dpt1;

    /**
     * Constructor for {@link DPT3}
     *
     * @param id
     * @param description
     * @param dpt1
     */
    private DPT3(final String id, final String description, final DPT1 dpt1) {
        super(id, description);
        this.dpt1 = dpt1;
    }

    public DPT1 getDPT1() {
        return this.dpt1;
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 1 && (bytes[0] >>> 4) == 0x00;
    }

    @Override
    protected DPT3Value parse(final byte[] bytes) {
        return new DPT3Value(this, bytes[0]);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1 || args.length == 2;
    }

    @Override
    protected DPT3Value parse(final String[] args) {
        final var controlled = this.findByString(args, "controlled");
        final var intValue = this.findByPattern(args, Pattern.compile("^[\\d]+$"), Integer::parseInt);

        return new DPT3Value(this, controlled, intValue);
    }

    public DPT3Value toValue(final boolean controlled, final int stepCode) {
        return new DPT3Value(this, controlled, stepCode);
    }

    public DPT3Value toValue(final boolean controlled, final StepInterval stepInterval) {
        return new DPT3Value(this, controlled, stepInterval);
    }

    public byte[] toByteArray(final boolean controlled, final int stepCode) {
        return DPT3Value.toByteArray(controlled, stepCode);
    }

    public byte[] toByteArray(final boolean controlled, final StepInterval stepInterval) {
        return DPT3Value.toByteArray(controlled, stepInterval.getStepCode());
    }

}