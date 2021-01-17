/*
 * KNX Link - A library for KNX Net/IP communication
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
 * Test {@link DPT25Value}
 *
 * @author PITSCHR
 */
class DPT25ValueTest {

    @Test
    @DisplayName("#(byte) with: 0x00")
    void testByte_0x00() {
        final var value = new DPT25Value((byte) 0b0000_0000);
        assertThat(value.getBusy()).isZero();
        assertThat(value.getNak()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("Busy: 0, Nak: 0");
    }

    @Test
    @DisplayName("#(byte) with: 0x31")
    void testByte_0x31() {
        final var value = new DPT25Value((byte) 0b0011_0001);
        assertThat(value.getBusy()).isEqualTo(3);
        assertThat(value.getNak()).isEqualTo(1);
        assertThat(value.toByteArray()).containsExactly(0x31);

        assertThat(value.toText()).isEqualTo("Busy: 3, Nak: 1");
    }

    @Test
    @DisplayName("#(byte) with: 0x23")
    void testByte_0x23() {
        final var value = new DPT25Value((byte) 0b0010_0011);
        assertThat(value.getBusy()).isEqualTo(2);
        assertThat(value.getNak()).isEqualTo(3);
        assertThat(value.toByteArray()).containsExactly(0x23);

        assertThat(value.toText()).isEqualTo("Busy: 2, Nak: 3");
    }

    @Test
    @DisplayName("#(int, int) with: Busy=0, Nak=0")
    void testInt_Busy0_Nak0() {
        final var value = new DPT25Value(0, 0);
        assertThat(value.getBusy()).isZero();
        assertThat(value.getNak()).isZero();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("Busy: 0, Nak: 0");
    }

    @Test
    @DisplayName("#(int, int) with: Busy=0, Nak=2")
    void testInt_Busy0_Nak2() {
        final var value = new DPT25Value(0, 2);
        assertThat(value.getBusy()).isZero();
        assertThat(value.getNak()).isEqualTo(2);
        assertThat(value.toByteArray()).containsExactly(0x02);

        assertThat(value.toText()).isEqualTo("Busy: 0, Nak: 2");
    }

    @Test
    @DisplayName("#(int, int) with: Busy=3, Nak=3")
    void testInt_Busy3_Nak3() {
        final var value = new DPT25Value(3, 3);
        assertThat(value.getBusy()).isEqualTo(3);
        assertThat(value.getNak()).isEqualTo(3);
        assertThat(value.toByteArray()).containsExactly(0x33);

        assertThat(value.toText()).isEqualTo("Busy: 3, Nak: 3");
    }

    @Test
    @DisplayName("#(int, int) with BUSY number out of range")
    void testBusyOutOfRange() {
        assertThatThrownBy(() -> new DPT25Value(-1, 0))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'busy' is out of range '0'..'3'.");
        assertThatThrownBy(() -> new DPT25Value(4, 0))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '4' for argument 'busy' is out of range '0'..'3'.");
    }

    @Test
    @DisplayName("#(int, int) with NAK number out of range")
    void testNakOutOfRange() {
        assertThatThrownBy(() -> new DPT25Value(0, -1))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '-1' for argument 'nak' is out of range '0'..'3'.");
        assertThatThrownBy(() -> new DPT25Value(0, 4))
                .isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessage("Value '4' for argument 'nak' is out of range '0'..'3'.");
    }


    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueByte = new DPT25Value((byte) 0b0011_0001);
        assertThat(valueByte).hasToString(
                "DPT25Value{dpt=25.1000, busy=3, nak=1, byteArray=0x31}"
        );

        final var valueInt = new DPT25Value(2, 3);
        assertThat(valueInt).hasToString(
                "DPT25Value{dpt=25.1000, busy=2, nak=3, byteArray=0x23}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT25Value(2, 3);
        final var valueByte = new DPT25Value((byte) 0b0010_0011);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(valueByte).isEqualTo(value);
        assertThat(valueByte).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT25Value(0, 3));
        assertThat(value).isNotEqualTo(new DPT25Value(2, 0));
    }
}
