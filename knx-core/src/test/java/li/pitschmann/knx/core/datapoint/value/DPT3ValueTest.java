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

import li.pitschmann.knx.core.datapoint.DPT3;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(value.getStepInterval()).isSameAs(DPT3Value.StepInterval.STOP);
        assertThat(value.toByteArray()).containsExactly(0x00);

        assertThat(value.toText()).isEqualTo("Stop");
    }

    @Test
    @DisplayName("#(DPT3.BLINDS_CONTROL, byte) with: no control, 12%")
    void testByteNotControlledPercent12() {
        final var value = new DPT3Value(DPT3.BLINDS_CONTROL, (byte) 0b0000_0100);
        assertThat(value.isControlled()).isFalse();
        assertThat(value.getStepInterval()).isSameAs(DPT3Value.StepInterval.PERCENT_12);
        assertThat(value.toByteArray()).containsExactly(0x04);

        assertThat(value.toText()).isEqualTo("12%");
    }

    @Test
    @DisplayName("#(DPT3.BLINDS_CONTROL, byte) with: control, 25%")
    void testByteControlledPercent25() {
        final var value = new DPT3Value(DPT3.BLINDS_CONTROL, (byte) 0b0000_1011);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getStepInterval()).isSameAs(DPT3Value.StepInterval.PERCENT_25);
        assertThat(value.toByteArray()).containsExactly(0x0B);

        assertThat(value.toText()).isEqualTo("controlled '25%'");
    }

    @Test
    @DisplayName("#(DPT3.BLINDS_CONTROL, byte) with: control, 100%")
    void testByteControlledPercent100() {
        final var value = new DPT3Value(DPT3.BLINDS_CONTROL, (byte)0b0000_1001);
        assertThat(value.isControlled()).isTrue();
        assertThat(value.getStepInterval()).isSameAs(DPT3Value.StepInterval.PERCENT_100);
        assertThat(value.toByteArray()).containsExactly(0x09);

        assertThat(value.toText()).isEqualTo("controlled '100%'");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var valueBlinds = new DPT3Value(DPT3.BLINDS_CONTROL, false, DPT3Value.StepInterval.PERCENT_100);
        assertThat(valueBlinds).hasToString(
                "DPT3Value{dpt=3.008, controlled=false, stepInterval=PERCENT_100, byteArray=0x01}"
        );

        final var valueDimming = new DPT3Value(DPT3.DIMMING_CONTROL, true, DPT3Value.StepInterval.PERCENT_6);
        assertThat(valueDimming).hasToString(
                "DPT3Value{dpt=3.007, controlled=true, stepInterval=PERCENT_6, byteArray=0x0D}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        final var value = new DPT3Value(DPT3.BLINDS_CONTROL, true, DPT3Value.StepInterval.PERCENT_3);
        final var valueByte = new DPT3Value(DPT3.BLINDS_CONTROL, (byte)0b000_1110);

        // equals & same hash code
        assertThat(value).isEqualTo(value);
        assertThat(valueByte).isEqualTo(value);
        assertThat(valueByte).hasSameHashCodeAs(value);

        // not equals
        assertThat(value).isNotEqualTo(null);
        assertThat(value).isNotEqualTo(new Object());
        assertThat(value).isNotEqualTo(new DPT3Value(DPT3.DIMMING_CONTROL, true, DPT3Value.StepInterval.PERCENT_3));
        assertThat(value).isNotEqualTo(new DPT3Value(DPT3.BLINDS_CONTROL, false, DPT3Value.StepInterval.PERCENT_3));
        assertThat(value).isNotEqualTo(new DPT3Value(DPT3.BLINDS_CONTROL, true, DPT3Value.StepInterval.PERCENT_1));
    }

    @Test
    @DisplayName("Implements 'PayloadOptimizable' interface")
    void testPayloadOptimizable() {
        assertThat(PayloadOptimizable.class).isAssignableFrom(DPT3Value.class);
    }

    @Test
    @DisplayName("StepInterval#ofCode(int)")
    public void testStepByOfCode() {
        // 0 .. 7
        assertThat(DPT3Value.StepInterval.ofCode(0)).isEqualTo(DPT3Value.StepInterval.STOP);
        assertThat(DPT3Value.StepInterval.ofCode(1)).isEqualTo(DPT3Value.StepInterval.PERCENT_100);
        assertThat(DPT3Value.StepInterval.ofCode(2)).isEqualTo(DPT3Value.StepInterval.PERCENT_50);
        assertThat(DPT3Value.StepInterval.ofCode(3)).isEqualTo(DPT3Value.StepInterval.PERCENT_25);
        assertThat(DPT3Value.StepInterval.ofCode(4)).isEqualTo(DPT3Value.StepInterval.PERCENT_12);
        assertThat(DPT3Value.StepInterval.ofCode(5)).isEqualTo(DPT3Value.StepInterval.PERCENT_6);
        assertThat(DPT3Value.StepInterval.ofCode(6)).isEqualTo(DPT3Value.StepInterval.PERCENT_3);
        assertThat(DPT3Value.StepInterval.ofCode(7)).isEqualTo(DPT3Value.StepInterval.PERCENT_1);

        // invalid
        assertThatThrownBy(() -> DPT3Value.StepInterval.ofCode(-1)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> DPT3Value.StepInterval.ofCode(8)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("StepInterval#ofInterval(int)")
    public void testStepByOfInterval() {
        // 0
        assertThat(DPT3Value.StepInterval.ofInterval(0)).isEqualTo(DPT3Value.StepInterval.STOP);
        // 1
        assertThat(DPT3Value.StepInterval.ofInterval(1)).isEqualTo(DPT3Value.StepInterval.PERCENT_100);
        // 2
        assertThat(DPT3Value.StepInterval.ofInterval(2)).isEqualTo(DPT3Value.StepInterval.PERCENT_50);
        // 3
        assertThat(DPT3Value.StepInterval.ofInterval(3)).isEqualTo(DPT3Value.StepInterval.PERCENT_25);
        assertThat(DPT3Value.StepInterval.ofInterval(4)).isEqualTo(DPT3Value.StepInterval.PERCENT_25);
        // 4
        assertThat(DPT3Value.StepInterval.ofInterval(5)).isEqualTo(DPT3Value.StepInterval.PERCENT_12);
        assertThat(DPT3Value.StepInterval.ofInterval(6)).isEqualTo(DPT3Value.StepInterval.PERCENT_12);
        assertThat(DPT3Value.StepInterval.ofInterval(7)).isEqualTo(DPT3Value.StepInterval.PERCENT_12);
        assertThat(DPT3Value.StepInterval.ofInterval(8)).isEqualTo(DPT3Value.StepInterval.PERCENT_12);
        // 5
        assertThat(DPT3Value.StepInterval.ofInterval(9)).isEqualTo(DPT3Value.StepInterval.PERCENT_6);
        assertThat(DPT3Value.StepInterval.ofInterval(10)).isEqualTo(DPT3Value.StepInterval.PERCENT_6);
        assertThat(DPT3Value.StepInterval.ofInterval(15)).isEqualTo(DPT3Value.StepInterval.PERCENT_6);
        assertThat(DPT3Value.StepInterval.ofInterval(16)).isEqualTo(DPT3Value.StepInterval.PERCENT_6);
        // 6
        assertThat(DPT3Value.StepInterval.ofInterval(17)).isEqualTo(DPT3Value.StepInterval.PERCENT_3);
        assertThat(DPT3Value.StepInterval.ofInterval(18)).isEqualTo(DPT3Value.StepInterval.PERCENT_3);
        assertThat(DPT3Value.StepInterval.ofInterval(31)).isEqualTo(DPT3Value.StepInterval.PERCENT_3);
        assertThat(DPT3Value.StepInterval.ofInterval(32)).isEqualTo(DPT3Value.StepInterval.PERCENT_3);
        // 7
        assertThat(DPT3Value.StepInterval.ofInterval(33)).isEqualTo(DPT3Value.StepInterval.PERCENT_1);
        assertThat(DPT3Value.StepInterval.ofInterval(34)).isEqualTo(DPT3Value.StepInterval.PERCENT_1);
        assertThat(DPT3Value.StepInterval.ofInterval(63)).isEqualTo(DPT3Value.StepInterval.PERCENT_1);
        assertThat(DPT3Value.StepInterval.ofInterval(64)).isEqualTo(DPT3Value.StepInterval.PERCENT_1);

        // invalid
        assertThatThrownBy(() -> DPT3Value.StepInterval.ofInterval(-1)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> DPT3Value.StepInterval.ofInterval(65)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("StepInterval#ofPercent(double)")
    public void testStepByOfPercent() {
        // 0
        assertThat(DPT3Value.StepInterval.ofPercent(0.0)).isEqualTo(DPT3Value.StepInterval.STOP);
        assertThat(DPT3Value.StepInterval.ofPercent(0.0099)).isEqualTo(DPT3Value.StepInterval.STOP);
        // 1
        assertThat(DPT3Value.StepInterval.ofPercent(0.01)).isEqualTo(DPT3Value.StepInterval.PERCENT_1);
        assertThat(DPT3Value.StepInterval.ofPercent(2.199)).isEqualTo(DPT3Value.StepInterval.PERCENT_1);
        // 2
        assertThat(DPT3Value.StepInterval.ofPercent(2.2)).isEqualTo(DPT3Value.StepInterval.PERCENT_3);
        assertThat(DPT3Value.StepInterval.ofPercent(4.499)).isEqualTo(DPT3Value.StepInterval.PERCENT_3);
        // 3
        assertThat(DPT3Value.StepInterval.ofPercent(4.5)).isEqualTo(DPT3Value.StepInterval.PERCENT_6);
        assertThat(DPT3Value.StepInterval.ofPercent(8.999)).isEqualTo(DPT3Value.StepInterval.PERCENT_6);
        // 4
        assertThat(DPT3Value.StepInterval.ofPercent(9.0)).isEqualTo(DPT3Value.StepInterval.PERCENT_12);
        assertThat(DPT3Value.StepInterval.ofPercent(18.4)).isEqualTo(DPT3Value.StepInterval.PERCENT_12);
        // 5
        assertThat(DPT3Value.StepInterval.ofPercent(18.5)).isEqualTo(DPT3Value.StepInterval.PERCENT_25);
        assertThat(DPT3Value.StepInterval.ofPercent(37.499)).isEqualTo(DPT3Value.StepInterval.PERCENT_25);
        // 6
        assertThat(DPT3Value.StepInterval.ofPercent(37.5)).isEqualTo(DPT3Value.StepInterval.PERCENT_50);
        assertThat(DPT3Value.StepInterval.ofPercent(74.999)).isEqualTo(DPT3Value.StepInterval.PERCENT_50);
        // 7
        assertThat(DPT3Value.StepInterval.ofPercent(75.0)).isEqualTo(DPT3Value.StepInterval.PERCENT_100);
        assertThat(DPT3Value.StepInterval.ofPercent(100.0)).isEqualTo(DPT3Value.StepInterval.PERCENT_100);

        // invalid
        assertThatThrownBy(() -> DPT3Value.StepInterval.ofPercent(-0.0001)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> DPT3Value.StepInterval.ofPercent(100.0001)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }
}
