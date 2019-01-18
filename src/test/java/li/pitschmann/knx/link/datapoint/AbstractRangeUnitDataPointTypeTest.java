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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.knx.link.datapoint.value.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for {@link AbstractRangeUnitDataPointType}
 *
 * @author PITSCHR
 */
public class AbstractRangeUnitDataPointTypeTest {

    /**
     * Tests {@link AbstractRangeUnitDataPointType#getLowerValue()},
     * {@link AbstractRangeUnitDataPointType#getUpperValue()} and
     * {@link AbstractRangeUnitDataPointType#isRangeClosed(Comparable)}
     */
    @Test
    public void testLowerAndUpperValues() {
        // 0 .. 360
        assertThat(DPT5.ANGLE.getLowerValue()).isEqualTo(0);
        assertThat(DPT5.ANGLE.getUpperValue()).isEqualTo(360);

        assertThat(DPT5.ANGLE.isRangeClosed(-1)).isFalse();
        assertThat(DPT5.ANGLE.isRangeClosed(0)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(180)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(360)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(361)).isFalse();
    }

    /**
     * Tests {@link AbstractRangeUnitDataPointType#getUnit()} and overridden {@link AbstractRangeUnitDataPointType#getDescription()}
     */
    @Test
    public void testUnitAndDescriptions() {
        final TestRangeUnitDPT dptWithoutUnit = new TestRangeUnitDPT(null);
        assertThat(dptWithoutUnit.getUnit()).isNull();
        assertThat(dptWithoutUnit.getDescription()).isEqualTo("description");

        final TestRangeUnitDPT dptWithUnit = new TestRangeUnitDPT("unit");
        assertThat(dptWithUnit.getUnit()).isEqualTo("unit");
        assertThat(dptWithUnit.getDescription()).isEqualTo("description (unit)");
    }

    /**
     * Test instance for
     *
     * @author PITSCHR
     */
    private class TestRangeUnitDPT extends AbstractRangeUnitDataPointType<DataPointValue<?>, Integer> {
        public TestRangeUnitDPT(String unit) {
            super("id", "description", 0, 0, unit);
        }

        @Override
        protected boolean isCompatible(byte[] bytes) {
            return false;
        }

        @Override
        protected DataPointValue<?> parse(byte[] bytes) {
            return null;
        }
    }
}
