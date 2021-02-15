/*
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

import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link StepInterval}
 *
 * @author PITSCHR
 */
class StepIntervalTest {

    @Test
    @DisplayName("#getByte()")
    void testGetByte() {
        assertThat(StepInterval.STOP.getByte()).isEqualTo((byte) 0b0000_0000);
        assertThat(StepInterval.PERCENT_100.getByte()).isEqualTo((byte) 0b0000_0001);
        assertThat(StepInterval.PERCENT_50.getByte()).isEqualTo((byte) 0b0000_0010);
        assertThat(StepInterval.PERCENT_25.getByte()).isEqualTo((byte) 0b0000_0011);
        assertThat(StepInterval.PERCENT_12.getByte()).isEqualTo((byte) 0b0000_0100);
        assertThat(StepInterval.PERCENT_6.getByte()).isEqualTo((byte) 0b0000_0101);
        assertThat(StepInterval.PERCENT_3.getByte()).isEqualTo((byte) 0b0000_0110);
        assertThat(StepInterval.PERCENT_1.getByte()).isEqualTo((byte) 0b0000_0111);
    }

    @Test
    @DisplayName("#getText()")
    void testGetText() {
        assertThat(StepInterval.STOP.getText()).isEqualTo("Stop");
        assertThat(StepInterval.PERCENT_100.getText()).isEqualTo("100%");
        assertThat(StepInterval.PERCENT_50.getText()).isEqualTo("50%");
        assertThat(StepInterval.PERCENT_25.getText()).isEqualTo("25%");
        assertThat(StepInterval.PERCENT_12.getText()).isEqualTo("12%");
        assertThat(StepInterval.PERCENT_6.getText()).isEqualTo("6%");
        assertThat(StepInterval.PERCENT_3.getText()).isEqualTo("3%");
        assertThat(StepInterval.PERCENT_1.getText()).isEqualTo("1%");
    }

    @Test
    @DisplayName("#parse(String)")
    void testParse() {
        // "stop" variants
        assertThat(StepInterval.parse("STOP")).isEqualTo(StepInterval.STOP);        // by text
        assertThat(StepInterval.parse("0%")).isEqualTo(StepInterval.STOP);          // by percent
        assertThat(StepInterval.parse("0.002%")).isEqualTo(StepInterval.STOP);      // by percent 0.002% is nearest to 0%
        assertThat(StepInterval.parse("0,005%")).isEqualTo(StepInterval.STOP);      // by percent 0,005% is nearest to 0%
        assertThat(StepInterval.parse("0")).isEqualTo(StepInterval.STOP);           // by interval (0 = 0%)

        // Percent 12% variants
        assertThat(StepInterval.parse("12%")).isEqualTo(StepInterval.PERCENT_12);   // by text
        assertThat(StepInterval.parse("10.3%")).isEqualTo(StepInterval.PERCENT_12); // by percent 10.3% is nearest to 12%
        assertThat(StepInterval.parse("12,8%")).isEqualTo(StepInterval.PERCENT_12); // by percent 12,8% is nearest to 12%
        assertThat(StepInterval.parse("6")).isEqualTo(StepInterval.PERCENT_12);     // by interval (6 .. 8 = 12%)

        // Percent 50% variants
        assertThat(StepInterval.parse("50%")).isEqualTo(StepInterval.PERCENT_50);   // by text
        assertThat(StepInterval.parse("38.8%")).isEqualTo(StepInterval.PERCENT_50); // by percent 38,8% is nearest to 50%
        assertThat(StepInterval.parse("65,1%")).isEqualTo(StepInterval.PERCENT_50); // by percent 65,1% is nearest to 50%
        assertThat(StepInterval.parse("2")).isEqualTo(StepInterval.PERCENT_50);     // by interval (2 = 50%)
    }

    @Test
    @DisplayName("#parse(String) with invalid cases")
    void testParseInvalidCases() {
        assertThatThrownBy(() -> StepInterval.parse(""))
                .isInstanceOf(NumberFormatException.class);

        assertThatThrownBy(() -> StepInterval.parse("-1"))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'interval' is out of range '0'..'64'.");

        assertThatThrownBy(() -> StepInterval.parse("halt"))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    @DisplayName("#ofByte(byte)")
    void testOfByte() {
        // 0 (000) .. 7 (111)
        assertThat(StepInterval.ofByte((byte) 0x00)).isEqualTo(StepInterval.STOP);
        assertThat(StepInterval.ofByte((byte) 0x01)).isEqualTo(StepInterval.PERCENT_100);
        assertThat(StepInterval.ofByte((byte) 0x02)).isEqualTo(StepInterval.PERCENT_50);
        assertThat(StepInterval.ofByte((byte) 0x03)).isEqualTo(StepInterval.PERCENT_25);
        assertThat(StepInterval.ofByte((byte) 0x04)).isEqualTo(StepInterval.PERCENT_12);
        assertThat(StepInterval.ofByte((byte) 0x05)).isEqualTo(StepInterval.PERCENT_6);
        assertThat(StepInterval.ofByte((byte) 0x06)).isEqualTo(StepInterval.PERCENT_3);
        assertThat(StepInterval.ofByte((byte) 0x07)).isEqualTo(StepInterval.PERCENT_1);

        // invalid
        assertThatThrownBy(() -> StepInterval.ofByte((byte) 0x80)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> StepInterval.ofByte((byte) 0x08)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("#ofInterval(int)")
    void testOfInterval() {
        // 0
        assertThat(StepInterval.ofInterval(0)).isEqualTo(StepInterval.STOP);
        // 1
        assertThat(StepInterval.ofInterval(1)).isEqualTo(StepInterval.PERCENT_100);
        // 2
        assertThat(StepInterval.ofInterval(2)).isEqualTo(StepInterval.PERCENT_50);
        // 3
        assertThat(StepInterval.ofInterval(3)).isEqualTo(StepInterval.PERCENT_25);
        assertThat(StepInterval.ofInterval(4)).isEqualTo(StepInterval.PERCENT_25);
        // 4
        assertThat(StepInterval.ofInterval(5)).isEqualTo(StepInterval.PERCENT_12);
        assertThat(StepInterval.ofInterval(6)).isEqualTo(StepInterval.PERCENT_12);
        assertThat(StepInterval.ofInterval(7)).isEqualTo(StepInterval.PERCENT_12);
        assertThat(StepInterval.ofInterval(8)).isEqualTo(StepInterval.PERCENT_12);
        // 5
        assertThat(StepInterval.ofInterval(9)).isEqualTo(StepInterval.PERCENT_6);
        assertThat(StepInterval.ofInterval(10)).isEqualTo(StepInterval.PERCENT_6);
        assertThat(StepInterval.ofInterval(15)).isEqualTo(StepInterval.PERCENT_6);
        assertThat(StepInterval.ofInterval(16)).isEqualTo(StepInterval.PERCENT_6);
        // 6
        assertThat(StepInterval.ofInterval(17)).isEqualTo(StepInterval.PERCENT_3);
        assertThat(StepInterval.ofInterval(18)).isEqualTo(StepInterval.PERCENT_3);
        assertThat(StepInterval.ofInterval(31)).isEqualTo(StepInterval.PERCENT_3);
        assertThat(StepInterval.ofInterval(32)).isEqualTo(StepInterval.PERCENT_3);
        // 7
        assertThat(StepInterval.ofInterval(33)).isEqualTo(StepInterval.PERCENT_1);
        assertThat(StepInterval.ofInterval(34)).isEqualTo(StepInterval.PERCENT_1);
        assertThat(StepInterval.ofInterval(63)).isEqualTo(StepInterval.PERCENT_1);
        assertThat(StepInterval.ofInterval(64)).isEqualTo(StepInterval.PERCENT_1);

        // invalid
        assertThatThrownBy(() -> StepInterval.ofInterval(-1)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> StepInterval.ofInterval(65)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("#ofPercent(double)")
    void testOfPercent() {
        // 0
        assertThat(StepInterval.ofPercent(0.0)).isEqualTo(StepInterval.STOP);
        assertThat(StepInterval.ofPercent(0.0099)).isEqualTo(StepInterval.STOP);
        // 1
        assertThat(StepInterval.ofPercent(0.01)).isEqualTo(StepInterval.PERCENT_1);
        assertThat(StepInterval.ofPercent(2.199)).isEqualTo(StepInterval.PERCENT_1);
        // 2
        assertThat(StepInterval.ofPercent(2.2)).isEqualTo(StepInterval.PERCENT_3);
        assertThat(StepInterval.ofPercent(4.499)).isEqualTo(StepInterval.PERCENT_3);
        // 3
        assertThat(StepInterval.ofPercent(4.5)).isEqualTo(StepInterval.PERCENT_6);
        assertThat(StepInterval.ofPercent(8.999)).isEqualTo(StepInterval.PERCENT_6);
        // 4
        assertThat(StepInterval.ofPercent(9.0)).isEqualTo(StepInterval.PERCENT_12);
        assertThat(StepInterval.ofPercent(18.4)).isEqualTo(StepInterval.PERCENT_12);
        // 5
        assertThat(StepInterval.ofPercent(18.5)).isEqualTo(StepInterval.PERCENT_25);
        assertThat(StepInterval.ofPercent(37.499)).isEqualTo(StepInterval.PERCENT_25);
        // 6
        assertThat(StepInterval.ofPercent(37.5)).isEqualTo(StepInterval.PERCENT_50);
        assertThat(StepInterval.ofPercent(74.999)).isEqualTo(StepInterval.PERCENT_50);
        // 7
        assertThat(StepInterval.ofPercent(75.0)).isEqualTo(StepInterval.PERCENT_100);
        assertThat(StepInterval.ofPercent(100.0)).isEqualTo(StepInterval.PERCENT_100);

        // invalid
        assertThatThrownBy(() -> StepInterval.ofPercent(-0.0001)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> StepInterval.ofPercent(100.0001)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }
}
