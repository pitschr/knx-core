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

import li.pitschmann.knx.core.datapoint.value.DPT21Value;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT21}
 *
 * @author PITSCHR
 */
class DPT21Test {
    private static final BaseDataPointType<?>[] DATAPOINT_TYPES = new BaseDataPointType<?>[]{
            DPT21.GENERAL_STATUS,
            DPT21.DEVICE_CONTROL,
            DPT21.FORCING_SIGNAL,
            DPT21.FORCING_SIGNAL_COOLING,
            DPT21.ROOM_HEATING_CONTROLLER_STATUS,
            DPT21.SOLAR_DHW_CONTROLLER_STATUS,
            DPT21.FUEL_TYPE_SET,
            DPT21.ROOM_COOLING_CONTROLLER_STATUS,
            DPT21.VENTILATION_CONTROLLER_STATUS,
            DPT21.LIGHTING_ACTUATOR_ERROR_INFO,
            DPT21.RADIO_FREQUENCY_COMMUNICATION_MODE_INFO,
            DPT21.CEMI_SERVER_SUPPORTED_FILTERING_MODE,
            DPT21.SECURITY_REPORT,
            DPT21.CHANNEL_ACTIVATION_8
    };

    @Test
    @DisplayName("DPT21 Constructor not instantiable")
    void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT21.class);
    }

    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        assertThat(DPT21.CHANNEL_ACTIVATION_8.getId()).isEqualTo("21.1010");
        assertThat(DPT21.CHANNEL_ACTIVATION_8.getDescription()).isEqualTo("Channel Activation for 8 channels");

        assertThat(DPT21.FORCING_SIGNAL_COOLING.getId()).isEqualTo("21.101");
        assertThat(DPT21.FORCING_SIGNAL_COOLING.getDescription()).isEqualTo("Forcing Signal Cooling");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        for (final var dpt : DATAPOINT_TYPES) {
            // byte is supported for length == 1 only
            assertThat(dpt.isCompatible(new byte[0])).isFalse();
            assertThat(dpt.isCompatible(new byte[1])).isTrue();
            assertThat(dpt.isCompatible(new byte[2])).isFalse();
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
            assertThat(dataPointValue.getClass().getName()).startsWith(DPT21Value.class.getName());
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
        // General Status (0000 1001 = 0x09)
        assertThat(DPT21.GENERAL_STATUS.of(true, false, false, true, false))
                .isInstanceOf(DPT21Value.GeneralStatus.class);

        // Device Control (0000 0101 = 0x05)
        assertThat(DPT21.DEVICE_CONTROL.of(true, false, true))
                .isInstanceOf(DPT21Value.DeviceControl.class);

        // Forcing Signal (1100 0110 = 0xC6)
        assertThat(DPT21.FORCING_SIGNAL.of(false, true, true, false, false, false, true, true))
                .isInstanceOf(DPT21Value.ForcingSignal.class);

        // Forcing Signal Cooling (0000 0001 = 0x01)
        assertThat(DPT21.FORCING_SIGNAL_COOLING.of(true))
                .isInstanceOf(DPT21Value.ForcingSignalCooling.class);

        // Status Room Heating Controller (0011 1100 = 0x3C)
        assertThat(DPT21.ROOM_HEATING_CONTROLLER_STATUS.of(false, false, true, true, true,
                true, false, false))
                .isInstanceOf(DPT21Value.RoomHeatingControllerStatus.class);

        // Status Solar DHW Controller (0000 0010 = 0x02)
        assertThat(DPT21.SOLAR_DHW_CONTROLLER_STATUS.of(false, true, false))
                .isInstanceOf(DPT21Value.SolarDHWControllerStatus.class);

        // Fuel Type Set (0000 0100 = 0x04)
        assertThat(DPT21.FUEL_TYPE_SET.of(false, false, true))
                .isInstanceOf(DPT21Value.FuelTypeSet.class);

        // Room Cooling Controller Status (0000 0001 = 0x01)
        assertThat(DPT21.ROOM_COOLING_CONTROLLER_STATUS.of(true))
                .isInstanceOf(DPT21Value.RoomCoolingControllerStatus.class);

        // Status Ventilation Controller (0000 0011 = 0x03)
        assertThat(DPT21.VENTILATION_CONTROLLER_STATUS.of(true, true, false, false))
                .isInstanceOf(DPT21Value.VentilationControllerStatus.class);

        // Lighting Actuator Error Information (0110 1111 = 0x6F)
        assertThat(DPT21.LIGHTING_ACTUATOR_ERROR_INFO.of(true, true, true, true, false, true, true))
                .isInstanceOf(DPT21Value.LightingActuatorErrorInfo.class);

        // Radio Frequency Communication Mode Info (0000 0111 = 0x07)
        assertThat(DPT21.RADIO_FREQUENCY_COMMUNICATION_MODE_INFO.of(true, true, true))
                .isInstanceOf(DPT21Value.RadioFrequencyCommunicationModeInfo.class);

        // cEMI Server Supported Filtering Mode (0000 0000 = 0x00)
        assertThat(DPT21.CEMI_SERVER_SUPPORTED_FILTERING_MODE.of(false, false, false))
                .isInstanceOf(DPT21Value.CEMIServerSupportedFilteringMode.class);

        // Security Report (0000 0001 = 0x01)
        assertThat(DPT21.SECURITY_REPORT.of(true))
                .isInstanceOf(DPT21Value.SecurityReport.class);

        // 8 Channel Activation (1001 1100 = 0x9C)
        assertThat(DPT21.CHANNEL_ACTIVATION_8.of(false, false, true, true, true, false, false, true))
                .isInstanceOf(DPT21Value.ChannelActivation8.class);
    }

    @Test
    @DisplayName("Test #toByteArray(..)")
    void testToByteArray() {
        // General Status (0000 1001 = 0x09)
        assertThat(DPT21.GENERAL_STATUS.toByteArray(true, false, false, true, false))
                .containsExactly(0x09);

        // Device Control (0000 0101 = 0x05)
        assertThat(DPT21.DEVICE_CONTROL.toByteArray(true, false, true))
                .containsExactly(0x05);

        // Forcing Signal (1100 0110 = 0xC6)
        assertThat(DPT21.FORCING_SIGNAL.toByteArray(false, true, true, false, false, false, true, true))
                .containsExactly(0xC6);

        // Forcing Signal Cooling (0000 0001 = 0x01)
        assertThat(DPT21.FORCING_SIGNAL_COOLING.toByteArray(true))
                .containsExactly(0x01);

        // Status Room Heating Controller (0011 1100 = 0x3C)
        assertThat(DPT21.ROOM_HEATING_CONTROLLER_STATUS.toByteArray(false, false, true, true, true,
                true, false, false))
                .containsExactly(0x3C);

        // Status Solar DHW Controller (0000 0010 = 0x02)
        assertThat(DPT21.SOLAR_DHW_CONTROLLER_STATUS.toByteArray(false, true, false))
                .containsExactly(0x02);

        // Fuel Type Set (0000 0100 = 0x04)
        assertThat(DPT21.FUEL_TYPE_SET.toByteArray(false, false, true))
                .containsExactly(0x04);

        // Room Cooling Controller Status (0000 0001 = 0x01)
        assertThat(DPT21.ROOM_COOLING_CONTROLLER_STATUS.toByteArray(true))
                .containsExactly(0x01);

        // Status Ventilation Controller (0000 0011 = 0x03)
        assertThat(DPT21.VENTILATION_CONTROLLER_STATUS.toByteArray(true, true, false, false))
                .containsExactly(0x03);

        // Lighting Actuator Error Information (0110 1111 = 0x6F)
        assertThat(DPT21.LIGHTING_ACTUATOR_ERROR_INFO.toByteArray(true, true, true, true, false, true, true))
                .containsExactly(0x6F);

        // Radio Frequency Communication Mode Info (0000 0111 = 0x07)
        assertThat(DPT21.RADIO_FREQUENCY_COMMUNICATION_MODE_INFO.toByteArray(true, true, true))
                .containsExactly(0x07);

        // cEMI Server Supported Filtering Mode (0000 0000 = 0x00)
        assertThat(DPT21.CEMI_SERVER_SUPPORTED_FILTERING_MODE.toByteArray(false, false, false))
                .containsExactly(0x00);

        // Security Report (0000 0001 = 0x01)
        assertThat(DPT21.SECURITY_REPORT.toByteArray(true))
                .containsExactly(0x01);

        // 8 Channel Activation (1001 1100 = 0x9C)
        assertThat(DPT21.CHANNEL_ACTIVATION_8.toByteArray(false, false, true, true, true, false, false, true))
                .containsExactly(0x9C);
    }
}