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
import li.pitschmann.knx.link.datapoint.value.DPT3Value.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.utils.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test {@link DPT3Value}
 *
 * @author PITSCHR
 */
public final class DPT3ValueTest {
    /**
     * Test {@link DPT3Value}
     */
    @Test
    public void test() {
        for (int stepCode = 0; stepCode < 7; stepCode++) {
            StepInterval stepInterval = StepInterval.ofCode(stepCode);
            this.assertValue(DPT3.DPT_CONTROL_BLINDS, (byte) stepCode, false, stepCode, stepInterval);
            this.assertValue(DPT3.DPT_CONTROL_BLINDS, (byte) (0x08 | stepCode), true, stepCode, stepInterval);
        }
    }

    /**
     * Test {@link DPT3Value} failures
     */
    @Test
    public void testFailures() {
        // step code must be between 0..7
        assertThatThrownBy(() -> new DPT3Value(DPT3.DPT_CONTROL_BLINDS, false, -1)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> new DPT3Value(DPT3.DPT_CONTROL_BLINDS, false, 8)).isInstanceOf(KnxNumberOutOfRangeException.class);

        // step interval must be defined
        assertThatThrownBy(() -> new DPT3Value(DPT3.DPT_CONTROL_BLINDS, false, null)).isInstanceOf(KnxNullPointerException.class);

    }

    private void assertValue(final DPT3 dpt, final byte b, final boolean controlled, final int stepCode, final StepInterval stepInterval) {
        DPT3Value dptValue = new DPT3Value(dpt, controlled, stepCode);
        DPT3Value dptValueByByte = new DPT3Value(dpt, b);
        DPT3Value dptvalueByStepInterval = new DPT3Value(dpt, controlled, stepInterval);

        // instance methods
        assertThat(dptValue.isControlled()).isEqualTo(controlled);
        assertThat(dptValue.getStepCode()).isEqualTo(stepCode);
        assertThat(dptValue.getStepInterval()).isEqualTo(stepInterval);
        assertThat(dptValue.toByteArray()).containsExactly(b);

        // class methods
        assertThat(DPT3Value.toByteArray(controlled, stepCode)).containsExactly(b);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);
        assertThat(dptvalueByStepInterval).isEqualTo(dptValue);
        assertThat(dptvalueByStepInterval).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT3Value(DPT3.DPT_CONTROL_DIMMING, controlled, stepCode));
        assertThat(dptValue).isNotEqualTo(new DPT3Value(dpt, controlled, stepCode + 1));
        assertThat(dptValue).isNotEqualTo(new DPT3Value(dpt, !controlled, stepCode));

        // toString
        String toString = String.format("DPT3Value{dpt=%s, controlled=%s, stepCode=%s, stepInterval=%s, byteArray=%s}", dpt, controlled, stepCode,
                stepInterval, ByteFormatter.formatHex(b));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
        assertThat(dptvalueByStepInterval).hasToString(toString);
    }

    /**
     * Tests {@link StepInterval#ofCode(int)}
     */
    @Test
    public void testStepByOfCode() {
        // 0 .. 7
        assertThat(StepInterval.ofCode(0)).isEqualTo(StepInterval.STOP);
        assertThat(StepInterval.ofCode(1)).isEqualTo(StepInterval.PERCENT_100);
        assertThat(StepInterval.ofCode(2)).isEqualTo(StepInterval.PERCENT_50);
        assertThat(StepInterval.ofCode(3)).isEqualTo(StepInterval.PERCENT_25);
        assertThat(StepInterval.ofCode(4)).isEqualTo(StepInterval.PERCENT_12);
        assertThat(StepInterval.ofCode(5)).isEqualTo(StepInterval.PERCENT_6);
        assertThat(StepInterval.ofCode(6)).isEqualTo(StepInterval.PERCENT_3);
        assertThat(StepInterval.ofCode(7)).isEqualTo(StepInterval.PERCENT_1);

        // invalid
        assertThatThrownBy(() -> StepInterval.ofCode(-1)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> StepInterval.ofCode(8)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    /**
     * Tests {@link StepInterval#ofInterval(int)}
     */
    @Test
    public void testStepByOfInterval() {
        // 0
        assertThat(StepInterval.ofInterval(0)).isEqualTo(StepInterval.STOP);
        // 1
        assertThat(StepInterval.ofInterval(1)).isEqualTo(StepInterval.PERCENT_100);
        // 2
        assertThat(StepInterval.ofInterval(2)).isEqualTo(StepInterval.PERCENT_50);
        assertThat(StepInterval.ofInterval(3)).isEqualTo(StepInterval.PERCENT_50);
        // 3
        assertThat(StepInterval.ofInterval(4)).isEqualTo(StepInterval.PERCENT_25);
        assertThat(StepInterval.ofInterval(5)).isEqualTo(StepInterval.PERCENT_25);
        assertThat(StepInterval.ofInterval(6)).isEqualTo(StepInterval.PERCENT_25);
        // 4
        for (int i = 7; i <= 12; i++) {
            assertThat(StepInterval.ofInterval(i)).isEqualTo(StepInterval.PERCENT_12);
        }
        // 5
        for (int i = 13; i <= 24; i++) {
            assertThat(StepInterval.ofInterval(i)).isEqualTo(StepInterval.PERCENT_6);
        }
        // 6
        for (int i = 25; i <= 48; i++) {
            assertThat(StepInterval.ofInterval(i)).isEqualTo(StepInterval.PERCENT_3);
        }
        // 7
        for (int i = 49; i <= 64; i++) {
            assertThat(StepInterval.ofInterval(i)).isEqualTo(StepInterval.PERCENT_1);
        }

        // invalid
        assertThatThrownBy(() -> StepInterval.ofInterval(-1)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> StepInterval.ofInterval(65)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    /**
     * Tests {@link StepInterval#ofPercent(float)}
     */
    @Test
    public void testStepByOfPercent() {
        // 0
        assertThat(StepInterval.ofPercent(0f)).isEqualTo(StepInterval.STOP);
        assertThat(StepInterval.ofPercent(0.0099f)).isEqualTo(StepInterval.STOP);
        // 1
        assertThat(StepInterval.ofPercent(0.01f)).isEqualTo(StepInterval.PERCENT_1);
        assertThat(StepInterval.ofPercent(2.199f)).isEqualTo(StepInterval.PERCENT_1);
        // 2
        assertThat(StepInterval.ofPercent(2.2f)).isEqualTo(StepInterval.PERCENT_3);
        assertThat(StepInterval.ofPercent(4.499f)).isEqualTo(StepInterval.PERCENT_3);
        // 3
        assertThat(StepInterval.ofPercent(4.5f)).isEqualTo(StepInterval.PERCENT_6);
        assertThat(StepInterval.ofPercent(8.999f)).isEqualTo(StepInterval.PERCENT_6);
        // 4
        assertThat(StepInterval.ofPercent(9.0f)).isEqualTo(StepInterval.PERCENT_12);
        assertThat(StepInterval.ofPercent(18.4f)).isEqualTo(StepInterval.PERCENT_12);
        // 5
        assertThat(StepInterval.ofPercent(18.5f)).isEqualTo(StepInterval.PERCENT_25);
        assertThat(StepInterval.ofPercent(37.499f)).isEqualTo(StepInterval.PERCENT_25);
        // 6
        assertThat(StepInterval.ofPercent(37.5f)).isEqualTo(StepInterval.PERCENT_50);
        assertThat(StepInterval.ofPercent(74.999f)).isEqualTo(StepInterval.PERCENT_50);
        // 7
        assertThat(StepInterval.ofPercent(75.0f)).isEqualTo(StepInterval.PERCENT_100);
        assertThat(StepInterval.ofPercent(100.0f)).isEqualTo(StepInterval.PERCENT_100);

        // invalid
        assertThatThrownBy(() -> StepInterval.ofPercent(-0.0001f)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> StepInterval.ofPercent(100.0001f)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }
}
