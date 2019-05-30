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

import li.pitschmann.knx.link.datapoint.AbstractDataPointType;
import li.pitschmann.knx.link.datapoint.DPT1;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link AbstractDataPointFlag}
 *
 * @author PITSCHR
 */
public class AbstractDataPointFlagTest {
    /**
     * Test {@link AbstractDataPointFlag}
     */
    @Test
    public void test() {
        TestDataPointFlag flag1 = new TestDataPointFlag(DPT1.SWITCH, (byte) 0x04);
        TestDataPointFlag flag2 = new TestDataPointFlag(DPT1.SWITCH, (byte) 0x04);

        // values
        assertThat(flag1.toByteArray()).containsExactly(0x04);
        for (var bit = 0; bit < 8; bit++) {
            assertThat(flag1.isSet(bit)).isEqualTo(bit == 2);
        }

        // equals
        assertThat(flag1).isEqualTo(flag1);
        assertThat(flag2).isEqualTo(flag1);
        assertThat(flag2).hasSameHashCodeAs(flag1);

        // not equals
        assertThat(flag1).isNotEqualTo(null);
        assertThat(flag1).isNotEqualTo(new Object());
        assertThat(flag1).isNotEqualTo(new TestDataPointFlag(DPT1.BOOL, (byte) 0x04));
        assertThat(flag1).isNotEqualTo(new TestDataPointFlag(DPT1.SWITCH, (byte) 0x03));

        // toString
        final var toString = String.format("TestDataPointFlag{dpt=%s, byte=0x04}", DPT1.SWITCH);
        assertThat(flag1).hasToString(toString);
    }

    /**
     * Test failures of {@link AbstractDataPointFlag}
     */
    @Test
    public void testFailures() {
        final var obj = new TestDataPointFlag(DPT1.SWITCH, (byte) 0x04);
        assertThatThrownBy(() -> obj.isSet(-1)).isInstanceOf(IllegalArgumentException.class).hasMessage("Bit must be between 0 and 7 (actual: -1)");
        assertThatThrownBy(() -> obj.isSet(8)).isInstanceOf(IllegalArgumentException.class).hasMessage("Bit must be between 0 and 7 (actual: 8)");
    }

    /**
     * Test {@link AbstractDataPointFlag} class
     *
     * @author PITSCHR
     */
    private static class TestDataPointFlag extends AbstractDataPointFlag<AbstractDataPointType<?>> {
        protected TestDataPointFlag(AbstractDataPointType<?> dpt, byte b) {
            super(dpt, b);
        }
    }
}
