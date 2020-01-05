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
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
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
public class DPT21Test implements DPTTest {

    @Override
    @Test
    public void testIdAndDescription() {
        assertThat(DPT21.CHANNEL_ACTIVATION_8.getId()).isEqualTo("21.1010");
        assertThat(DPT21.CHANNEL_ACTIVATION_8.getDescription()).isEqualTo("Channel Activation for 8 channels");

        assertThat(DPT21.FORCING_SIGNAL_COOLING.getId()).isEqualTo("21.101");
        assertThat(DPT21.FORCING_SIGNAL_COOLING.getDescription()).isEqualTo("Forcing Signal Cooling");
    }

    @Override
    @Test
    public void testCompatibility() {
        // failures
        assertThatThrownBy(() -> DPT21.CHANNEL_ACTIVATION_8.of(new byte[0])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT21.CHANNEL_ACTIVATION_8.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT21.CHANNEL_ACTIVATION_8.of("0x00", "0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // OK
        assertThat(DPT21.CHANNEL_ACTIVATION_8.of((byte) 0x00)).isInstanceOf(DPT21Value.ChannelActivation8.class);
        assertThat(DPT21.FORCING_SIGNAL_COOLING.of((byte) 0xFF)).isInstanceOf(DPT21Value.ForcingSignalCooling.class);
        assertThat(DPT21.CHANNEL_ACTIVATION_8.of("0x00")).isInstanceOf(DPT21Value.ChannelActivation8.class);
        assertThat(DPT21.FORCING_SIGNAL_COOLING.of("0xFF")).isInstanceOf(DPT21Value.ForcingSignalCooling.class);
    }

    @Override
    @Test
    public void testOf() {
        // General Status (0000 1001 = 0x09)
        DPT21.GeneralStatus generalStatusDPT = DPT21.GENERAL_STATUS;
        DPT21Value.GeneralStatus generalStatusDPV = generalStatusDPT.of(true, false, false, true, false);
        assertThat(generalStatusDPT.of((byte) 0x09)).isEqualTo(generalStatusDPV);
        assertThat(generalStatusDPT.of(true, false, false, true, false)).isEqualTo(generalStatusDPV);
        assertThat(generalStatusDPT.toByteArray(true, false, false, true, false)).containsExactly(0x09);
        assertThat(generalStatusDPV.toByteArray()).containsExactly(0x09);
        assertThatThrownBy(() -> generalStatusDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Device Control (0000 0101 = 0x05)
        DPT21.DeviceControl deviceControlDPT = DPT21.DEVICE_CONTROL;
        DPT21Value.DeviceControl deviceControlDPV = deviceControlDPT.of(true, false, true);
        assertThat(deviceControlDPT.of((byte) 0x05)).isEqualTo(deviceControlDPV);
        assertThat(deviceControlDPT.of(true, false, true)).isEqualTo(deviceControlDPV);
        assertThat(deviceControlDPT.toByteArray(true, false, true)).containsExactly(0x05);
        assertThat(deviceControlDPV.toByteArray()).containsExactly(0x05);
        assertThatThrownBy(() -> deviceControlDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Forcing Signal (1100 0110 = 0xC6)
        DPT21.ForcingSignal forcingSignalDPT = DPT21.FORCING_SIGNAL;
        DPT21Value.ForcingSignal forcingSignalDPV = forcingSignalDPT.of(false, true, true, false, false, false, true, true);
        assertThat(forcingSignalDPT.of((byte) 0xC6)).isEqualTo(forcingSignalDPV);
        assertThat(forcingSignalDPT.of(false, true, true, false, false, false, true, true)).isEqualTo(forcingSignalDPV);
        assertThat(forcingSignalDPT.toByteArray(false, true, true, false, false, false, true, true)).containsExactly(0xC6);
        assertThat(forcingSignalDPV.toByteArray()).containsExactly(0xC6);
        assertThatThrownBy(() -> forcingSignalDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Forcing Signal Cooling (0000 0001 = 0x01)
        DPT21.ForcingSignalCooling forcingSignalCoolingDPT = DPT21.FORCING_SIGNAL_COOLING;
        DPT21Value.ForcingSignalCooling forcingSignalCoolingDPV = forcingSignalCoolingDPT.of(true);
        assertThat(forcingSignalCoolingDPT.of((byte) 0x01)).isEqualTo(forcingSignalCoolingDPV);
        assertThat(forcingSignalCoolingDPT.of(true)).isEqualTo(forcingSignalCoolingDPV);
        assertThat(forcingSignalCoolingDPT.toByteArray(true)).containsExactly(0x01);
        assertThat(forcingSignalCoolingDPV.toByteArray()).containsExactly(0x01);
        assertThatThrownBy(() -> forcingSignalCoolingDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Status Room Heating Controller (0011 1100 = 0x3C)
        DPT21.StatusRoomHeatingController statusRoomHeatingControllerDPT = DPT21.STATUS_ROOM_HEATING_CONTROLLER;
        DPT21Value.StatusRoomHeatingController statusRoomHeatingControllerDPV = statusRoomHeatingControllerDPT.of(false, false, true, true, true,
                true, false, false);
        assertThat(statusRoomHeatingControllerDPT.of((byte) 0x3C)).isEqualTo(statusRoomHeatingControllerDPV);
        assertThat(statusRoomHeatingControllerDPT.of(false, false, true, true, true, true, false, false))
                .isEqualTo(statusRoomHeatingControllerDPV);
        assertThat(statusRoomHeatingControllerDPT.toByteArray(false, false, true, true, true, true, false, false)).containsExactly(0x3C);
        assertThat(statusRoomHeatingControllerDPV.toByteArray()).containsExactly(0x3C);
        assertThatThrownBy(() -> statusRoomHeatingControllerDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Status Solar DHW Controller (0000 0010 = 0x02)
        DPT21.StatusSolarDHWController statusSolarDHWControllerDPT = DPT21.STATUS_SOLAR_DHW_CONTROLLER;
        DPT21Value.StatusSolarDHWController statusSolarDHWControllerDPV = statusSolarDHWControllerDPT.of(false, true, false);
        assertThat(statusSolarDHWControllerDPT.of((byte) 0x02)).isEqualTo(statusSolarDHWControllerDPV);
        assertThat(statusSolarDHWControllerDPT.of(false, true, false)).isEqualTo(statusSolarDHWControllerDPV);
        assertThat(statusSolarDHWControllerDPT.toByteArray(false, true, false)).containsExactly(0x02);
        assertThat(statusSolarDHWControllerDPV.toByteArray()).containsExactly(0x02);
        assertThatThrownBy(() -> statusSolarDHWControllerDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Fuel Type Set (0000 0100 = 0x04)
        DPT21.FuelTypeSet fuelTypeSetDPT = DPT21.FUEL_TYPE_SET;
        DPT21Value.FuelTypeSet fuelTypeSetDPV = fuelTypeSetDPT.of(false, false, true);
        assertThat(fuelTypeSetDPT.of((byte) 0x04)).isEqualTo(fuelTypeSetDPV);
        assertThat(fuelTypeSetDPT.of(false, false, true)).isEqualTo(fuelTypeSetDPV);
        assertThat(fuelTypeSetDPT.toByteArray(false, false, true)).containsExactly(0x04);
        assertThat(fuelTypeSetDPV.toByteArray()).containsExactly(0x04);
        assertThatThrownBy(() -> fuelTypeSetDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Room Cooling Controller Status (0000 0001 = 0x01)
        DPT21.StatusRoomCoolingController statusRoomCoolingControllerDPT = DPT21.STATUS_ROOM_COOLING_CONTROLLER;
        DPT21Value.StatusRoomCoolingController statusRoomCoolingControllerDPV = statusRoomCoolingControllerDPT.of(true);
        assertThat(statusRoomCoolingControllerDPT.of((byte) 0x01)).isEqualTo(statusRoomCoolingControllerDPV);
        assertThat(statusRoomCoolingControllerDPT.of(true)).isEqualTo(statusRoomCoolingControllerDPV);
        assertThat(statusRoomCoolingControllerDPT.toByteArray(true)).containsExactly(0x01);
        assertThat(statusRoomCoolingControllerDPV.toByteArray()).containsExactly(0x01);
        assertThatThrownBy(() -> statusRoomCoolingControllerDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Status Ventilation Controller (0000 0011 = 0x03)
        DPT21.StatusVentilationController statusVentilationControllerDPT = DPT21.STATUS_VENTILATION_CONTROLLER;
        DPT21Value.StatusVentilationController statusVentilationControllerDPV = statusVentilationControllerDPT.of(true, true, false, false);
        assertThat(statusVentilationControllerDPT.of((byte) 0x03)).isEqualTo(statusVentilationControllerDPV);
        assertThat(statusVentilationControllerDPT.of(true, true, false, false)).isEqualTo(statusVentilationControllerDPV);
        assertThat(statusVentilationControllerDPT.toByteArray(true, true, false, false)).containsExactly(0x03);
        assertThat(statusVentilationControllerDPV.toByteArray()).containsExactly(0x03);
        assertThatThrownBy(() -> statusVentilationControllerDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Lighting Actuator Error Information (0110 1111 = 0x6F)
        DPT21.LightingActuatorErrorInfo lightActErrorInfoDPT = DPT21.LIGHTING_ACTUATOR_ERROR_INFO;
        DPT21Value.LightingActuatorErrorInfo lightingActuatorErrorInfoDPV = lightActErrorInfoDPT.of(true, true, true, true, false, true, true);
        assertThat(lightActErrorInfoDPT.of((byte) 0x6F)).isEqualTo(lightingActuatorErrorInfoDPV);
        assertThat(lightActErrorInfoDPT.of(true, true, true, true, false, true, true)).isEqualTo(lightingActuatorErrorInfoDPV);
        assertThat(lightActErrorInfoDPT.toByteArray(true, true, true, true, false, true, true)).containsExactly(0x6F);
        assertThat(lightingActuatorErrorInfoDPV.toByteArray()).containsExactly(0x6F);
        assertThatThrownBy(() -> lightActErrorInfoDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Radio Frequency Communication Mode Info (0000 0111 = 0x07)
        DPT21.RadioFrequencyCommunicationModeInfo rfCommInfoDPT = DPT21.RADIO_FREQUENCY_COMMUNICATION_MODE_INFO;
        DPT21Value.RadioFrequencyCommunicationModeInfo rfCommInfoDPV = rfCommInfoDPT.of(true, true, true);
        assertThat(rfCommInfoDPT.of((byte) 0x07)).isEqualTo(rfCommInfoDPV);
        assertThat(rfCommInfoDPT.of(true, true, true)).isEqualTo(rfCommInfoDPV);
        assertThat(rfCommInfoDPT.toByteArray(true, true, true)).containsExactly(0x07);
        assertThat(rfCommInfoDPV.toByteArray()).containsExactly(0x07);
        assertThatThrownBy(() -> rfCommInfoDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // cEMI Server Supported Filtering Mode (0000 0000 = 0x00)
        DPT21.CEMIServerSupportedFilteringMode cemiSrvSupFilterModesDPT = DPT21.CEMI_SERVER_SUPPORTED_FILTERING_MODE;
        DPT21Value.CEMIServerSupportedFilteringMode cemiSrvSupFilterModesDPV = cemiSrvSupFilterModesDPT.of(false, false, false);
        assertThat(cemiSrvSupFilterModesDPT.of((byte) 0x00)).isEqualTo(cemiSrvSupFilterModesDPV);
        assertThat(cemiSrvSupFilterModesDPT.of(false, false, false)).isEqualTo(cemiSrvSupFilterModesDPV);
        assertThat(cemiSrvSupFilterModesDPT.toByteArray(false, false, false)).containsExactly(0x00);
        assertThat(cemiSrvSupFilterModesDPV.toByteArray()).containsExactly(0x00);
        assertThatThrownBy(() -> cemiSrvSupFilterModesDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Security Report
        DPT21.SecurityReport securityReportDPT = DPT21.SECURITY_REPORT;
        DPT21Value.SecurityReport securityReportDPV = securityReportDPT.of(false);
        assertThat(securityReportDPT.of((byte) 0x00)).isEqualTo(securityReportDPV);
        assertThat(securityReportDPT.of(false)).isEqualTo(securityReportDPV);
        assertThat(securityReportDPT.toByteArray(false)).containsExactly(0x00);
        assertThat(securityReportDPV.toByteArray()).containsExactly(0x00);
        assertThatThrownBy(() -> securityReportDPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // 8 Channel Activation (1001 1100 = 0x9C)
        DPT21.ChannelActivation8 channel8DPT = DPT21.CHANNEL_ACTIVATION_8;
        DPT21Value.ChannelActivation8 channel8DPV = channel8DPT.of(false, false, true, true, true, false, false, true);
        assertThat(channel8DPT.of((byte) 0x9C)).isEqualTo(channel8DPV);
        assertThat(channel8DPT.of(false, false, true, true, true, false, false, true)).isEqualTo(channel8DPV);
        assertThat(channel8DPT.toByteArray(false, false, true, true, true, false, false, true)).containsExactly(0x9C);
        assertThat(channel8DPV.toByteArray()).containsExactly(0x9C);
        assertThatThrownBy(() -> channel8DPT.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
    }

    /**
     * Test constructor of {@link DPT21}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT21.class);
    }
}
