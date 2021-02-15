/*
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
import li.pitschmann.knx.core.datapoint.DPT18;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Data Point Value for {@link DPT18} (18.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | c   0   (Scene Number)        |
 * Encoding     | B   r   U   U   U   U   U   U |
 *              +---+---+---+---+---+---+---+---+
 * Format:     1 octet (B<sub>1</sub> r<sub>1</sub> U<sub>6</sub>)
 * Range:      c = {0, 1}
 *                  0 = activate the scene corresponding to the field Scene Number
 *                  1 = learn the scene corresponding to the field Scene Number
 *             U = [0 .. 63]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT18Value extends AbstractDataPointValue<DPT18> {
    private final boolean controlled;
    private final int sceneNumber;

    public DPT18Value(final byte b) {
        this(
                // controlled
                (b & 0x80) != 0x00,
                // scene number
                b & 0x7F
        );
    }

    public DPT18Value(final boolean controlled, final int sceneNumber) {
        super(DPT18.SCENE_CONTROL);
        if (!getDPT().isRangeClosed(sceneNumber)) {
            throw new KnxNumberOutOfRangeException("sceneNumber", getDPT().getLowerValue(), getDPT().getUpperValue(), sceneNumber);
        }
        this.controlled = controlled;
        this.sceneNumber = sceneNumber;
    }

    public boolean isControlled() {
        return controlled;
    }

    public int getSceneNumber() {
        return sceneNumber;
    }

    @Override
    public byte[] toByteArray() {
        final var controlledAsByte = controlled ? (byte) 0x80 : 0x00;
        final var sceneNumberAsByte = (byte) sceneNumber;

        return new byte[]{(byte) (controlledAsByte | sceneNumberAsByte)};
    }

    @Override
    public String toText() {

        if (isControlled()) {
            return "controlled 'scene " + sceneNumber + "'";
        } else {
            return "scene '" + sceneNumber + "'";
        }
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("controlled", controlled)
                .add("sceneNumber", sceneNumber)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT18Value) {
            final var other = (DPT18Value) obj;
            return Objects.equals(this.controlled, other.controlled) //
                    && Objects.equals(this.sceneNumber, other.sceneNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlled, sceneNumber);
    }
}
