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

package li.pitschmann.knx.link.datapoint.value;

import li.pitschmann.knx.link.datapoint.DPT18;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DPT18Value}
 *
 * @author PITSCHR
 */
public final class DPT18ValueTest {
    /**
     * Test {@link DPT18Value}
     */
    @Test
    public void test() {
        this.assertValue((byte) 0x87, true, 7);
        this.assertValue((byte) 0x29, false, 41);
    }

    private void assertValue(final byte b, final boolean controlled, final int sceneNumber) {
        DPT18Value dptValue = new DPT18Value(controlled, sceneNumber);
        DPT18Value dptValueByByte = new DPT18Value(b);

        // instance methods
        assertThat(dptValue.isControlled()).isEqualTo(controlled);
        assertThat(dptValue.getSceneNumber()).isEqualTo(sceneNumber);
        assertThat(dptValue.toByteArray()).containsExactly(b);

        // class methods
        assertThat(DPT18Value.toByteArray(controlled, sceneNumber)).containsExactly(b);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT18Value(!controlled, sceneNumber));
        assertThat(dptValue).isNotEqualTo(new DPT18Value(controlled, sceneNumber + 1));

        // toString
        String toString = String.format("DPT18Value{dpt=%s, controlled=%s, sceneNumber=%s, byteArray=%s}", DPT18.SCENE_CONTROL, controlled,
                sceneNumber, ByteFormatter.formatHex(b));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
