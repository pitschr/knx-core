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

package li.pitschmann.knx.core.datapoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BaseRangeDataPointType}
 *
 * @author PITSCHR
 */
public class BaseRangeDataPointTypeTest {

    @Test
    @DisplayName("Test #getLowerValue() and #getUpperValue()")
    public void testLowerAndUpperValues() {
        // 0° ... 360°
        assertThat(DPT5.ANGLE.getLowerValue()).isEqualTo(0);
        assertThat(DPT5.ANGLE.getUpperValue()).isEqualTo(360);
        // 0% ... 100%
        assertThat(DPT5.SCALING.getLowerValue()).isEqualTo(0);
        assertThat(DPT5.SCALING.getUpperValue()).isEqualTo(100);
        // -273°C .. 670760.96°C
        assertThat(DPT9.TEMPERATURE.getLowerValue()).isEqualTo(-273.0);
        assertThat(DPT9.TEMPERATURE.getUpperValue()).isEqualTo(670760.96);
    }

    @Test
    @DisplayName("Test #isRangeClosed()")
    public void testIsRangeClosed() {
        // 0° ... 360°
        assertThat(DPT5.ANGLE.isRangeClosed(-1)).isFalse();
        assertThat(DPT5.ANGLE.isRangeClosed(0)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(1)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(359)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(360)).isTrue();
        assertThat(DPT5.ANGLE.isRangeClosed(361)).isFalse();
        // 0% ... 100%
        assertThat(DPT5.SCALING.isRangeClosed(-1)).isFalse();
        assertThat(DPT5.SCALING.isRangeClosed(0)).isTrue();
        assertThat(DPT5.SCALING.isRangeClosed(99)).isTrue();
        assertThat(DPT5.SCALING.isRangeClosed(100)).isTrue();
        assertThat(DPT5.SCALING.isRangeClosed(101)).isFalse();
        // -273°C .. 670760.96°C
        assertThat(DPT9.TEMPERATURE.isRangeClosed(-273.01)).isFalse();
        assertThat(DPT9.TEMPERATURE.isRangeClosed(-273.0)).isTrue();
        assertThat(DPT9.TEMPERATURE.isRangeClosed(-272.99)).isTrue();
        assertThat(DPT9.TEMPERATURE.isRangeClosed(0.0)).isTrue();
        assertThat(DPT9.TEMPERATURE.isRangeClosed(670760.95)).isTrue();
        assertThat(DPT9.TEMPERATURE.isRangeClosed(670760.96)).isTrue();
        assertThat(DPT9.TEMPERATURE.isRangeClosed(670760.97)).isFalse();
    }
}
