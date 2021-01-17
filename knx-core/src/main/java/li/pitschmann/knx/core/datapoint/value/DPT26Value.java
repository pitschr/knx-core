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
import li.pitschmann.knx.core.datapoint.DPT26;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Data Point Value for {@link DPT26} (26.xxx)
 *
 * <pre>
 *             +-7-+-6-------+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | 0   (Active) (Scene Number)         |
 * Encoding    | r   B        U   U   U   U   U   U  |
 *             +---+---------+---+---+---+---+---+---+
 * Format:     1 octet (r<sub>1</sub> B<sub>1</sub> U<sub>6</sub>)
 * Active:     B = {0 = inactive, 1 = active}
 * Scene Nr:   U = [0 .. 63]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT26Value extends AbstractDataPointValue<DPT26> {
    private boolean active;
    private int sceneNumber;

    public DPT26Value(final byte b) {
        this(
                // active
                (b & 0x40) != 0x00,
                // scene number
                b & 0x3F
        );
    }

    public DPT26Value(final boolean active, final int sceneNumber) {
        super(DPT26.SCENE_INFORMATION);
        if (!getDPT().isRangeClosed(sceneNumber)) {
            throw new KnxNumberOutOfRangeException("sceneNumber", getDPT().getLowerValue(), getDPT().getUpperValue(), sceneNumber);
        }
        this.active = active;
        this.sceneNumber = sceneNumber;
    }

    public boolean isActive() {
        return active;
    }

    public int getSceneNumber() {
        return sceneNumber;
    }

    @Override
    public byte[] toByteArray() {
        final var activeAsByte = active ? (byte) 0x40 : 0x00;
        final var sceneNumberAsByte = (byte) sceneNumber;

        return new byte[]{(byte) (activeAsByte | sceneNumberAsByte)};
    }

    @Override
    public String toText() {

        if (isActive()) {
            return "active scene '" + sceneNumber + "'";
        } else {
            return "inactive scene '" + sceneNumber + "'";
        }
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("active", active)
                .add("sceneNumber", sceneNumber)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT26Value) {
            final var other = (DPT26Value) obj;
            return Objects.equals(this.active, other.active) //
                    && Objects.equals(this.sceneNumber, other.sceneNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), active, sceneNumber);
    }
}
