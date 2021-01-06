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

import li.pitschmann.knx.core.datapoint.DPT1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DPT1Value}
 *
 * @author PITSCHR
 */
class DPT1ValueTest {

    @Test
    @DisplayName("#(DPT1.SWITCH, byte) with: false")
    void testSwitchByteFalse() {
        final var value = new DPT1Value(DPT1.SWITCH, (byte) 0x00);
        assertThat(value.getValue()).isFalse();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.getText()).isEqualTo("Off");
        assertThat(value.toText()).isEqualTo("Off");
    }

    @Test
    @DisplayName("#(DPT1.SWITCH, byte) with: true")
    void testSwitchByteTrue() {
        final var value = new DPT1Value(DPT1.SWITCH, (byte) 0x01);
        assertThat(value.getValue()).isTrue();
        assertThat(value.toByteArray()).containsExactly(0x01);

        assertThat(value.getText()).isEqualTo("On");
        assertThat(value.toText()).isEqualTo("On");
    }

    @Test
    @DisplayName("#(DPT1.SWITCH, boolean) with: false")
    void testSwitchFalse() {
        final var value = new DPT1Value(DPT1.SWITCH, false);
        assertThat(value.getValue()).isFalse();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.getText()).isEqualTo("Off");
        assertThat(value.toText()).isEqualTo("Off");
    }

    @Test
    @DisplayName("#(DPT1.SWITCH, boolean) with: true")
    void testSwitchTrue() {
        final var value = new DPT1Value(DPT1.SWITCH, true);
        assertThat(value.getValue()).isTrue();
        assertThat(value.toByteArray()).containsExactly(0x01);

        assertThat(value.getText()).isEqualTo("On");
        assertThat(value.toText()).isEqualTo("On");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueSwitch = new DPT1Value(DPT1.SWITCH, true);
        assertThat(valueSwitch).hasToString(
                "DPT1Value{dpt=1.001, value=true, text=On, byteArray=0x01}"
        );

        final var valueEnable = new DPT1Value(DPT1.ENABLE, false);
        assertThat(valueEnable).hasToString(
                "DPT1Value{dpt=1.003, value=false, text=Disable, byteArray=0x00}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT1Value(DPT1.SWITCH, true);
        final var value2 = new DPT1Value(DPT1.SWITCH, true);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(value2).isEqualTo(value);
        assertThat(value2).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT1Value(DPT1.ACKNOWLEDGE, true));
        assertThat(value).isNotEqualTo(new DPT1Value(DPT1.SWITCH, false));
    }

    @Test
    @DisplayName("Implements 'PayloadOptimizable' interface")
    void testPayloadOptimizable() {
        assertThat(PayloadOptimizable.class).isAssignableFrom(DPT1Value.class);
    }
}
