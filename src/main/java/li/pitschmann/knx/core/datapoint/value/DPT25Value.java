/*
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
import li.pitschmann.knx.core.datapoint.DPT25;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Data Point Value for {@link DPT25} (25.xxx)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Busy)          (Nak)         |
 * Encoding    | U   U   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+
 * Format:     8 bit (U<sub>4</sub>U<sub>4</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT25Value extends AbstractDataPointValue<DPT25> {
    private final int busy;
    private final int nak;

    public DPT25Value(final byte b) {
        this(
                // busy
                (b >>> 4) & 0x0F,
                // nak
                b & 0x0F
        );
    }

    public DPT25Value(final int busy, final int nak) {
        super(DPT25.BUSY_NAK_REPETITIONS);
        if (busy < 0 || busy > 15) {
            throw new KnxNumberOutOfRangeException("busy", 0, 15, busy);
        }
        if (nak < 0 || nak > 15) {
            throw new KnxNumberOutOfRangeException("nak", 0, 15, nak);
        }

        this.busy = busy;
        this.nak = nak;
    }

    /**
     * Returns the BUSY value
     *
     * @return int value, between 0 and 15
     */
    public int getBusy() {
        return busy;
    }

    /**
     * Returns the NAK value
     *
     * @return int value, between 0 and 15
     */
    public int getNak() {
        return nak;
    }

    @Override
    public byte[] toByteArray() {
        byte b = 0x00;
        b |= (byte) (busy << 4);
        b |= (byte) nak;
        return new byte[]{b};
    }

    @Override
    public String toText() {
        return "Busy: " + busy + ", Nak: " + nak;
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("busy", busy)
                .add("nak", nak)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT25Value) {
            final var other = (DPT25Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.busy, other.busy)
                    && Objects.equals(this.nak, other.nak);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), busy, nak);
    }

}
