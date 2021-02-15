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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.datapoint.DPT2;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DPT2Value}
 *
 * @author PITSCHR
 */
class DPT2ValueTest {

    @Test
    @DisplayName("#(DPT2.SWITCH_CONTROL, byte) with: no control, false")
    void testSwitchByteNotControlledFalse() {
        final var value = new DPT2Value(DPT2.SWITCH_CONTROL, (byte) 0x00);
        assertThat(value.getValue()).isFalse();
        assertThat(value.isControlled()).isFalse();
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.getText()).isEqualTo("Off");
        assertThat(value.toText()).isEqualTo("Off");
    }

    @Test
    @DisplayName("#(DPT2.SWITCH_CONTROL, byte) with: no control, true")
    void testSwitchByteNotControlledTrue() {
        final var value = new DPT2Value(DPT2.SWITCH_CONTROL, (byte) 0x01);
        assertThat(value.getValue()).isTrue();
        assertThat(value.isControlled()).isFalse();
        assertThat(value.toByteArray()).containsExactly(0x01);

        assertThat(value.getText()).isEqualTo("On");
        assertThat(value.toText()).isEqualTo("On");
    }

    @Test
    @DisplayName("#(DPT2.SWITCH_CONTROL, byte) with: control, false")
    void testSwitchByteControlledFalse() {
        final var value = new DPT2Value(DPT2.SWITCH_CONTROL, (byte) 0x02);
        assertThat(value.getValue()).isFalse();
        assertThat(value.isControlled()).isTrue();
        assertThat(value.toByteArray()).containsExactly(0x02);

        assertThat(value.getText()).isEqualTo("Off");
        assertThat(value.toText()).isEqualTo("controlled 'Off'");
    }

    @Test
    @DisplayName("#(DPT2.SWITCH_CONTROL, byte) with: control, true")
    void testSwitchByteControlledTrue() {
        final var value = new DPT2Value(DPT2.SWITCH_CONTROL, (byte) 0x03);
        assertThat(value.getValue()).isTrue();
        assertThat(value.isControlled()).isTrue();
        assertThat(value.toByteArray()).containsExactly(0x03);

        assertThat(value.getText()).isEqualTo("On");
        assertThat(value.toText()).isEqualTo("controlled 'On'");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueSwitch = new DPT2Value(DPT2.SWITCH_CONTROL, false, true);
        assertThat(valueSwitch).hasToString(
                "DPT2Value{dpt=2.001, controlled=false, value=true, text=On, byteArray=0x01}"
        );

        final var valueEnable = new DPT2Value(DPT2.ENABLE_CONTROL, true, false);
        assertThat(valueEnable).hasToString(
                "DPT2Value{dpt=2.003, controlled=true, value=false, text=Disable, byteArray=0x02}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPT2Value.class).verify();
    }

    @Test
    @DisplayName("Implements 'PayloadOptimizable' interface")
    void testPayloadOptimizable() {
        assertThat(PayloadOptimizable.class).isAssignableFrom(DPT2Value.class);
    }
}
