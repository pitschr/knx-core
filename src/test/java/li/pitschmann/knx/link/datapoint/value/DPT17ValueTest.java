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

import li.pitschmann.knx.link.datapoint.*;
import li.pitschmann.utils.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test {@link DPT17Value}
 *
 * @author PITSCHR
 */
public final class DPT17ValueTest {
    /**
     * Test {@link DPT17Value}
     */
    @Test
    public void test() {
        this.assertValue((byte) 0x09, 9);
        this.assertValue((byte) 0x29, 41);
    }

    private void assertValue(final byte b, final int sceneNumber) {
        DPT17Value dptValue = new DPT17Value(sceneNumber);
        DPT17Value dptValueByByte = new DPT17Value(b);

        // instance methods
        assertThat(dptValue.getSceneNumber()).isEqualTo(sceneNumber);
        assertThat(dptValue.toByteArray()).containsExactly(b);

        // class methods
        assertThat(DPT17Value.toByteArray(sceneNumber)).containsExactly(b);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT17Value(sceneNumber + 1));

        // toString
        String toString = String.format("DPT17Value{dpt=%s, sceneNumber=%s, byteArray=%s}", DPT17.SCENE_NUMBER, sceneNumber,
                ByteFormatter.formatHex(b));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
