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

import li.pitschmann.knx.core.datapoint.DPT7;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT7Value}
 *
 * @author PITSCHR
 */
class DPT7ValueTest {

    @Test
    @DisplayName("#(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, byte[]) with: 0")
    void testByte_0() {
        final var value = new DPT7Value(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, new byte[]{0x00, 0x00});
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, byte[]) with: 7323")
    void testByte_7323() {
        final var value = new DPT7Value(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, new byte[]{0x1C, (byte)0x9B});
        assertThat(value.getValue()).isEqualTo(7323);
        assertThat(value.toByteArray()).containsExactly(0x1C, 0x9B);

        assertThat(value.toText()).isEqualTo("7323");
    }

    @Test
    @DisplayName("#(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, byte[]) with: 65535")
    void testByte_65535() {
        final var value = new DPT7Value(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, new byte[]{(byte) 0xFF, (byte)0xFF});
        assertThat(value.getValue()).isEqualTo(65535);
        assertThat(value.toByteArray()).containsExactly(0xFF, 0xFF);

        assertThat(value.toText()).isEqualTo("65535");
    }

    @Test
    @DisplayName("#(DPT7.TIME_PERIOD_10MS, int) with: 0")
    void testTimePeriod10ms_0() {
        final var value = new DPT7Value(DPT7.TIME_PERIOD_10MS, 0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT7.TIME_PERIOD_10MS, int) with: 22771")
    void testTimePeriod10ms_22771() {
        final var value = new DPT7Value(DPT7.TIME_PERIOD_10MS, 22771);
        assertThat(value.getValue()).isEqualTo(22771);
        assertThat(value.toByteArray()).containsExactly(0x08, 0xE5);

        assertThat(value.toText()).isEqualTo("22771");
    }

    @Test
    @DisplayName("#(DPT7.TIME_PERIOD_10MS, int) with: 655350")
    void testTimePeriod10ms_655350() {
        final var value = new DPT7Value(DPT7.TIME_PERIOD_10MS, 655350);
        assertThat(value.getValue()).isEqualTo(655350);
        assertThat(value.toByteArray()).containsExactly(0xFF, 0xFF);

        assertThat(value.toText()).isEqualTo("655350");
    }

    @Test
    @DisplayName("#(DPT7.TIME_PERIOD_100MS, int) with: 0")
    void testTimePeriod100ms_0() {
        final var value = new DPT7Value(DPT7.TIME_PERIOD_100MS, 0);
        assertThat(value.getValue()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00, 0x00);

        assertThat(value.toText()).isEqualTo("0");
    }

    @Test
    @DisplayName("#(DPT7.TIME_PERIOD_100MS, int) with: 1334455")
    void testTimePeriod100ms_22771() {
        final var value = new DPT7Value(DPT7.TIME_PERIOD_100MS, 1334400);
        assertThat(value.getValue()).isEqualTo(1334400);
        assertThat(value.toByteArray()).containsExactly(0x34, 0x20);

        assertThat(value.toText()).isEqualTo("1334400");
    }

    @Test
    @DisplayName("#(DPT7.TIME_PERIOD_100MS, int) with: 6553500")
    void testTimePeriod100ms_6553500() {
        final var value = new DPT7Value(DPT7.TIME_PERIOD_100MS, 6553500);
        assertThat(value.getValue()).isEqualTo(6553500);
        assertThat(value.toByteArray()).containsExactly(0xFF, 0xFF);

        assertThat(value.toText()).isEqualTo("6553500");
    }

    @Test
    @DisplayName("#(DPT7.TIME_PERIOD_10MS, int) with numbers out of range")
    void testScalingOutOfRange() {
        assertThatThrownBy(() -> new DPT7Value(DPT7.TIME_PERIOD_10MS, -1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'value' is out of range '0'..'655350'.");
        assertThatThrownBy(() -> new DPT7Value(DPT7.TIME_PERIOD_10MS, 655351))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '655351' for argument 'value' is out of range '0'..'655350'.");
    }

    @Test
    @DisplayName("#(DPT7.TIME_PERIOD_100MS, int) with numbers out of range")
    void testAngleOutOfRange() {
        assertThatThrownBy(() -> new DPT7Value(DPT7.TIME_PERIOD_100MS, -1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'value' is out of range '0'..'6553500'.");
        assertThatThrownBy(() -> new DPT7Value(DPT7.TIME_PERIOD_100MS, 6553501))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '6553501' for argument 'value' is out of range '0'..'6553500'.");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueUnsigned = new DPT7Value(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, 4634);
        assertThat(valueUnsigned).hasToString(
                "DPT7Value{dpt=7.001, value=4634, byteArray=0x12 1A}"
        );

        final var valueScaling = new DPT7Value(DPT7.TIME_PERIOD_10MS, 8300);
        assertThat(valueScaling).hasToString(
                "DPT7Value{dpt=7.003, value=8300, byteArray=0x03 3E}"
        );

        final var valueAngle = new DPT7Value(DPT7.TIME_PERIOD_100MS, 932200);
        assertThat(valueAngle).hasToString(
                "DPT7Value{dpt=7.004, value=932200, byteArray=0x24 6A}"
        );

    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT7Value(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, 4711);
        final var value2 = new DPT7Value(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, 4711);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(value2).isEqualTo(value);
        assertThat(value2).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT7Value(DPT7.TIME_PERIOD_10MS, 4711));
        assertThat(value).isNotEqualTo(new DPT7Value(DPT7.VALUE_2_OCTET_UNSIGNED_COUNT, 1147));
    }
}
