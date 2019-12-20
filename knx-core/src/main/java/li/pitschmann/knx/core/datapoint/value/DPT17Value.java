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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT17;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Data Point Value for {@link DPT17} (17.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | 0   0   (Scene Number)        |
 * Encoding     | r   r   U   U   U   U   U   U |
 *              +---+---+---+---+---+---+---+---+
 * Format:     1 octet (r<sub>2</sub> U<sub>6</sub>)
 * Range:      U = [0 .. 63]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT17Value extends AbstractDataPointValue<DPT17> {
    private int sceneNumber;

    public DPT17Value(final byte b) {
        this(Bytes.toUnsignedInt(b));
    }

    public DPT17Value(final int sceneNumber) {
        super(DPT17.SCENE_NUMBER);
        Preconditions.checkArgument(DPT17.SCENE_NUMBER.isRangeClosed(sceneNumber));
        this.sceneNumber = sceneNumber;
    }

    /**
     * Converts {@code sceneNumber} value to byte array
     *
     * @param sceneNumber scene number [0..63]
     * @return byte array
     */
    public static byte[] toByteArray(final int sceneNumber) {
        return new byte[]{(byte) sceneNumber};
    }

    public int getSceneNumber() {
        return this.sceneNumber;
    }

    @Override
    public byte[] toByteArray() {
        return toByteArray(this.sceneNumber);
    }

    @Override
    public String toText() {
        return "scene " + getSceneNumber();
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("sceneNumber", this.sceneNumber)
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT17Value) {
            final var other = (DPT17Value) obj;
            return Objects.equals(this.sceneNumber, other.sceneNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.sceneNumber);
    }
}
