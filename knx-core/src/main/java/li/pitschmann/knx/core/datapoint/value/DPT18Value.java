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

import li.pitschmann.knx.core.datapoint.DPT18;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nullable;
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
    private boolean controlled;
    private int sceneNumber;

    public DPT18Value(final byte b) {
        super(DPT18.SCENE_CONTROL);
        // control
        this.controlled = (b & 0x80) != 0x00;
        // scene number
        this.sceneNumber = b & 0x3F;
    }

    public DPT18Value(final boolean controlled, final int sceneNumber) {
        super(DPT18.SCENE_CONTROL);
        Preconditions.checkArgument(DPT18.SCENE_CONTROL.isRangeClosed(sceneNumber));
        this.controlled = controlled;
        this.sceneNumber = sceneNumber;
    }

    /**
     * Converts {@code control} and {@code sceneNumber} value to byte array
     *
     * @param controlled
     * @param sceneNumber
     * @return byte array
     */

    public static byte[] toByteArray(final boolean controlled, final int sceneNumber) {
        final var controlledAsByte = controlled ? (byte) 0x80 : 0x00;
        final var sceneNumberAsByte = (byte) sceneNumber;

        return new byte[]{(byte) (controlledAsByte | sceneNumberAsByte)};
    }

    public boolean isControlled() {
        return this.controlled;
    }

    public int getSceneNumber() {
        return this.sceneNumber;
    }


    @Override
    public byte[] toByteArray() {
        return toByteArray(this.controlled, this.sceneNumber);
    }


    @Override
    public String toText() {
        if (isControlled()) {
            return String.format("controlled 'scene %s'", getSceneNumber());
        } else {
            return "scene " + getSceneNumber();
        }
    }


    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("controlled", this.controlled)
                .add("sceneNumber", this.sceneNumber)
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
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
        return Objects.hash(this.controlled, this.sceneNumber);
    }
}
