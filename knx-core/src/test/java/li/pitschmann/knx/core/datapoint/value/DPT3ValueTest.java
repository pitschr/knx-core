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

import li.pitschmann.knx.core.datapoint.DPT3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DPT3Value}
 *
 * @author PITSCHR
 */
class DPT3ValueTest {

    @Test
    @DisplayName("#(DPT3.BLINDS_CONTROL, byte) with: no control, stop/0%")
    void testByteNotControlledPercent0() {
        final var value = new DPT3Value(DPT3.BLINDS_CONTROL, (byte) 0b0000_0000);
        assertThat(value.isControlled()).isFalse();
        assertThat(value.getStepInterval()).isSameAs(StepInterval.STOP);
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("Stop");
    }

    @Test
    @DisplayName("#(DPT3.BLINDS_CONTROL, byte) with: no control, 12%")
    void testByteNotControlledPercent12() {
        final var value = new DPT3Value(DPT3.BLINDS_CONTROL, (byte) 0b0000_0100);
        assertThat(value.isControlled()).isFalse();
        assertThat(value.getStepInterval()).isSameAs(StepInterval.PERCENT_12);
        assertThat(value.toByteArray()).containsExactly(0x04);

        assertThat(value.toText()).isEqualTo("12%");
    }

    @Test
    @DisplayName("#(DPT3.BLINDS_CONTROL, byte) with: control, 25%")
    void testByteControlledPercent25() {
        final var value = new DPT3Value(DPT3.BLINDS_CONTROL, (byte) 0b0000_1011);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getStepInterval()).isSameAs(StepInterval.PERCENT_25);
        assertThat(value.toByteArray()).containsExactly(0x0B);

        assertThat(value.toText()).isEqualTo("controlled '25%'");
    }

    @Test
    @DisplayName("#(DPT3.BLINDS_CONTROL, byte) with: control, 100%")
    void testByteControlledPercent100() {
        final var value = new DPT3Value(DPT3.BLINDS_CONTROL, (byte) 0b0000_1001);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getStepInterval()).isSameAs(StepInterval.PERCENT_100);
        assertThat(value.toByteArray()).containsExactly(0x09);

        assertThat(value.toText()).isEqualTo("controlled '100%'");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueBlinds = new DPT3Value(DPT3.BLINDS_CONTROL, false, StepInterval.PERCENT_100);
        assertThat(valueBlinds).hasToString(
                "DPT3Value{dpt=3.008, controlled=false, stepInterval=PERCENT_100, byteArray=0x01}"
        );

        final var valueDimming = new DPT3Value(DPT3.DIMMING_CONTROL, true, StepInterval.PERCENT_6);
        assertThat(valueDimming).hasToString(
                "DPT3Value{dpt=3.007, controlled=true, stepInterval=PERCENT_6, byteArray=0x0D}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT3Value(DPT3.BLINDS_CONTROL, true, StepInterval.PERCENT_3);
        final var valueByte = new DPT3Value(DPT3.BLINDS_CONTROL, (byte) 0b000_1110);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(valueByte).isEqualTo(value);
        assertThat(valueByte).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT3Value(DPT3.DIMMING_CONTROL, true, StepInterval.PERCENT_3));
        assertThat(value).isNotEqualTo(new DPT3Value(DPT3.BLINDS_CONTROL, false, StepInterval.PERCENT_3));
        assertThat(value).isNotEqualTo(new DPT3Value(DPT3.BLINDS_CONTROL, true, StepInterval.PERCENT_1));
    }

    @Test
    @DisplayName("Implements 'PayloadOptimizable' interface")
    void testPayloadOptimizable() {
        assertThat(PayloadOptimizable.class).isAssignableFrom(DPT3Value.class);
    }
}
