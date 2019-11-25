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

import li.pitschmann.knx.core.datapoint.AbstractDataPointType;
import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link AbstractDataPointFlags}
 *
 * @author PITSCHR
 */
public class AbstractDataPointFlagsTest {
    /**
     * Test {@link AbstractDataPointFlags}
     */
    @Test
    public void test() {
        final var flag1 = new TestDataPointFlags(DPT1.SWITCH, new byte[]{0x08, 0x04});
        final var flag2 = new TestDataPointFlags(DPT1.SWITCH, new byte[]{0x08, 0x04});

        // values
        assertThat(flag1.toByteArray()).containsExactly(0x08, 0x04);
        for (var bit = 0; bit < 16; bit++) {
            assertThat(flag1.isSet(bit)).isEqualTo(bit == 2 || bit == 11);
        }

        // equals
        assertThat(flag1).isEqualTo(flag1);
        assertThat(flag2).isEqualTo(flag1);
        assertThat(flag2).hasSameHashCodeAs(flag1);

        // not equals
        assertThat(flag1).isNotEqualTo(null);
        assertThat(flag1).isNotEqualTo(new Object());
        assertThat(flag1).isNotEqualTo(new TestDataPointFlags(DPT1.BOOL, new byte[]{0x08, 0x04}));
        assertThat(flag1).isNotEqualTo(new TestDataPointFlags(DPT1.SWITCH, new byte[]{0x08, 0x03}));

        // toString
        final var toString = String.format("TestDataPointFlags{dpt=%s, byteArray=0x08 04}", DPT1.SWITCH,
                ByteFormatter.formatHexAsString(new byte[]{0x08, 0x04}));
        assertThat(flag1).hasToString(toString);
    }

    /**
     * Test failures of {@link AbstractDataPointFlags}
     */
    @Test
    public void testFailures() {
        final var obj = new TestDataPointFlags(DPT1.SWITCH, new byte[]{0x08, 0x04});
        assertThatThrownBy(() -> obj.isSet(-1)).isInstanceOf(IllegalArgumentException.class).hasMessage("Bit must be between 0 and 15 (actual: -1)");
        assertThatThrownBy(() -> obj.isSet(16)).isInstanceOf(IllegalArgumentException.class).hasMessage("Bit must be between 0 and 15 (actual: 16)");
    }

    /**
     * Test {@link AbstractDataPointFlags} class
     *
     * @author PITSCHR
     */
    private static class TestDataPointFlags extends AbstractDataPointFlags<AbstractDataPointType<?>> {
        protected TestDataPointFlags(AbstractDataPointType<?> dpt, byte[] bytes) {
            super(dpt, bytes);
        }
    }
}
