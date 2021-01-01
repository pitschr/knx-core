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

package li.pitschmann.knx.core.datapoint;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BaseRangeDataPointType}
 *
 * @author PITSCHR
 */
public class BaseRangeDataPointTypeTest {

    /**
     * Tests {@link BaseRangeDataPointType#getLowerValue()},
     * {@link BaseRangeDataPointType#getUpperValue()} and
     * {@link BaseRangeDataPointType#isRangeClosed(Comparable)}
     */
    @Test
    public void testLowerAndUpperValues() {
        // 0 = 0° .. 255 = 360°
        assertThat(DPT5.ANGLE.getLowerValue()).isEqualTo(0);
        assertThat(DPT5.ANGLE.getUpperValue()).isEqualTo(255);

        assertThat(DPT5.ANGLE.isRangeClosed(-1)).isFalse();
        assertThat(DPT5.ANGLE.isRangeClosed(0)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(254)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(255)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(256)).isFalse();
    }
}
