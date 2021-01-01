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

import li.pitschmann.knx.core.datapoint.value.DPT22Value;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT22}
 *
 * @author PITSCHR
 */
class DPT22Test {
    private static final BaseDataPointType<?>[] DATAPOINT_TYPES = new BaseDataPointType<?>[]{
            DPT22.STATUS_DHW_CONTROLLER,
            DPT22.STATUS_ROOM_HEATING_COOLING_CONTROLLER,
            DPT22.MEDIA,
            DPT22.CHANNEL_ACTIVATION_16
    };

    @Test
    @DisplayName("DPT22 Constructor not instantiable")
    void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT22.class);
    }

    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        assertThat(DPT22.CHANNEL_ACTIVATION_16.getId()).isEqualTo("22.1010");
        assertThat(DPT22.CHANNEL_ACTIVATION_16.getDescription()).isEqualTo("Channel Activation for 16 channels");

        assertThat(DPT22.MEDIA.getId()).isEqualTo("22.1000");
        assertThat(DPT22.MEDIA.getDescription()).isEqualTo("Media");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        for (final var dpt : DATAPOINT_TYPES) {
            // byte is supported for length == 2 only
            assertThat(dpt.isCompatible(new byte[0])).isFalse();
            assertThat(dpt.isCompatible(new byte[1])).isFalse();
            assertThat(dpt.isCompatible(new byte[2])).isTrue();
            assertThat(dpt.isCompatible(new byte[3])).isFalse();
        }
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        for (final var dpt : DATAPOINT_TYPES) {
            // String is not supported -> always false
            for (int i = 0; i < 10; i++) {
                assertThat(dpt.isCompatible(new String[i])).isFalse();
            }
        }
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        for (final var dpt : DATAPOINT_TYPES) {
            final var dataPointValue = dpt.parse(new byte[]{0x00});
            assertThat(dataPointValue).isNotNull();
            assertThat(dataPointValue.getClass().getName()).startsWith(DPT22Value.class.getName());
        }
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        for (final var dpt : DATAPOINT_TYPES) {
            // parse for string not supported
            assertThatThrownBy(() -> dpt.parse(new String[0])).isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    @DisplayName("Test #of(..)")
    void testOf() {
        // DHW Controller Status (0000 0000 1010 1001 = 0x00 0xA9)
        assertThat(DPT22.STATUS_DHW_CONTROLLER.of(true, false, false, true, false, true, false, true))
                .isInstanceOf(DPT22Value.StatusDHWController.class);

        // Room Heating / Cooling Controller Status (0100 1001 1011 0011 = 0x49 0xB3)
        assertThat(DPT22.STATUS_ROOM_HEATING_COOLING_CONTROLLER.of(true, true, false, false, true, true, false, true, true, false, false, true, false, false, true))
                .isInstanceOf(DPT22Value.StatusRoomHeatingCoolingController.class);

        // Media (0000 0000 0000 0110 = 0x00 0x06)
        assertThat(DPT22.MEDIA.of(true, true, false, false))
                .isInstanceOf(DPT22Value.Media.class);

        // 16 Channel Activation (1001 1010 0100 0010 = 0x9A 0x42)
        assertThat(DPT22.CHANNEL_ACTIVATION_16.of(false, true, false, false, false, false, true, false, false, true, false, true, true, false, false, true))
                .isInstanceOf(DPT22Value.ChannelActivation16.class);
    }

    @Test
    @DisplayName("Test #toByteArray(..)")
    void testToByteArray() {
        // DHW Controller Status (0000 0000 1010 1001 = 0x00 0xA9)
        assertThat(DPT22.STATUS_DHW_CONTROLLER.toByteArray(true, false, false, true, false, true, false, true))
                .containsExactly(0x00, 0xA9);

        // Room Heating / Cooling Controller Status (0100 1001 1011 0011 = 0x49 0xB3)
        assertThat(DPT22.STATUS_ROOM_HEATING_COOLING_CONTROLLER.toByteArray(true, true, false, false, true, true, false, true, true, false, false, true, false, false, true))
                .containsExactly(0x49, 0xB3);

        // Media (0000 0000 0000 0110 = 0x00 0x06)
        assertThat(DPT22.MEDIA.toByteArray(true, true, false, false))
                .containsExactly(0x00, 0x06);

        // 16 Channel Activation (1001 1010 0100 0010 = 0x9A 0x42)
        assertThat(DPT22.CHANNEL_ACTIVATION_16.toByteArray(false, true, false, false, false, false, true, false, false, true, false, true, true, false, false, true))
                .containsExactly(0x9A, 0x42);
    }
}