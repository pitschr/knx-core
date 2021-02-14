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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT3;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

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
public final class DPT3Value extends AbstractDataPointValue<DPT3> implements PayloadOptimizable {
    private final boolean controlled;
    private final StepInterval stepInterval;

    public DPT3Value(final DPT3 dpt, final byte b) {
        this(dpt,
                // bit 3 = controlled
                (b & 0x08) != 0x00,
                // bit 0, 1 and 2 = stepCode
                StepInterval.ofByte((byte) (b & 0x07))
        );
    }

    public DPT3Value(final DPT3 dpt, final boolean controlled, final StepInterval stepInterval) {
        super(dpt);
        this.controlled = controlled;
        this.stepInterval = Objects.requireNonNull(stepInterval);
    }

    /**
     * Returns if the controlled flag is set
     *
     * @return boolean
     */
    public boolean isControlled() {
        return this.controlled;
    }

    /**
     * Returns the step interval which is an enumeration based on step code
     *
     * @return StepInterval
     */
    public StepInterval getStepInterval() {
        return stepInterval;
    }

    @Override
    public byte[] toByteArray() {
        var b = stepInterval.getByte();
        if (controlled) {
            b |= 0x08;
        }
        return new byte[]{b};
    }

    @Override
    public String toText() {
        final var text = getStepInterval().getText();
        if (isControlled()) {
            return "controlled '" + text + "'";
        } else {
            return text;
        }
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("controlled", controlled)
                .add("stepInterval", stepInterval.name())
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
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
                    && Objects.equals(this.stepInterval, other.stepInterval);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), controlled, stepInterval);
    }
}
