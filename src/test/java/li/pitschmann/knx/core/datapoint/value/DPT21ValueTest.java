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

import li.pitschmann.knx.core.datapoint.value.DPT21Value.CEMIServerSupportedFilteringMode;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.ChannelActivation8;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.DeviceControl;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.ForcingSignal;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.ForcingSignalCooling;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.FuelTypeSet;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.GeneralStatus;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.LightingActuatorErrorInfo;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.PhaseStatus;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.RadioFrequencyCommunicationModeInfo;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.RoomCoolingControllerStatus;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.RoomHeatingControllerStatus;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.SecurityReport;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.SolarDHWControllerStatus;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.VentilationControllerStatus;
import li.pitschmann.knx.core.datapoint.value.DPT21Value.VirtualContactStatus;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT21Value}
 *
 * @author PITSCHR
 */
public final class DPT21ValueTest {

    @Test
    @DisplayName("Test GeneralStatus")
    public void testGeneralStatus() {
        this.assertGeneralStatus((byte) 0x00, new boolean[]{false, false, false, false, false});
        this.assertGeneralStatus((byte) 0x01, new boolean[]{true, false, false, false, false});
        this.assertGeneralStatus((byte) 0x02, new boolean[]{false, true, false, false, false});
        this.assertGeneralStatus((byte) 0x04, new boolean[]{false, false, true, false, false});
        this.assertGeneralStatus((byte) 0x08, new boolean[]{false, false, false, true, false});
        this.assertGeneralStatus((byte) 0x10, new boolean[]{false, false, false, false, true});
    }

    private void assertGeneralStatus(final byte b, final boolean[] bool) {
        final var dptValue = new GeneralStatus(bool[0], bool[1], bool[2], bool[3], bool[4]);
        assertThat(new GeneralStatus(b)).isEqualTo(dptValue);

        assertThat(dptValue.isOutOfService()).isEqualTo(bool[0]);
        assertThat(dptValue.isFault()).isEqualTo(bool[1]);
        assertThat(dptValue.isOverridden()).isEqualTo(bool[2]);
        assertThat(dptValue.isInAlarm()).isEqualTo(bool[3]);
        assertThat(dptValue.isAlarmNotAcknowledged()).isEqualTo(bool[4]);
    }

    @Test
    @DisplayName("Test DeviceControl")
    public void testDeviceControl() {
        this.assertDeviceControl((byte) 0x00, new boolean[]{false, false, false});
        this.assertDeviceControl((byte) 0x01, new boolean[]{true, false, false});
        this.assertDeviceControl((byte) 0x02, new boolean[]{false, true, false});
        this.assertDeviceControl((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertDeviceControl(final byte b, final boolean[] bool) {
        final var dptValue = new DeviceControl(bool[0], bool[1], bool[2]);
        assertThat(new DeviceControl(b)).isEqualTo(dptValue);

        assertThat(dptValue.isUserApplicationStopped()).isEqualTo(bool[0]);
        assertThat(dptValue.isOwnIndividualAddress()).isEqualTo(bool[1]);
        assertThat(dptValue.isVerifyModeOn()).isEqualTo(bool[2]);
    }

    @Test
    @DisplayName("Test ForcingSignal")
    public void testForcingSignal() {
        this.assertForcingSignal((byte) 0x00, new boolean[]{false, false, false, false, false, false, false, false});
        this.assertForcingSignal((byte) 0x01, new boolean[]{true, false, false, false, false, false, false, false});
        this.assertForcingSignal((byte) 0x02, new boolean[]{false, true, false, false, false, false, false, false});
        this.assertForcingSignal((byte) 0x04, new boolean[]{false, false, true, false, false, false, false, false});
        this.assertForcingSignal((byte) 0x08, new boolean[]{false, false, false, true, false, false, false, false});
        this.assertForcingSignal((byte) 0x10, new boolean[]{false, false, false, false, true, false, false, false});
        this.assertForcingSignal((byte) 0x20, new boolean[]{false, false, false, false, false, true, false, false});
        this.assertForcingSignal((byte) 0x40, new boolean[]{false, false, false, false, false, false, true, false});
        this.assertForcingSignal((byte) 0x80, new boolean[]{false, false, false, false, false, false, false, true});
    }

    private void assertForcingSignal(final byte b, final boolean[] bool) {
        final var dptValue = new ForcingSignal(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7]);
        assertThat(new ForcingSignal(b)).isEqualTo(dptValue);

        assertThat(dptValue.isForceRequest()).isEqualTo(bool[0]);
        assertThat(dptValue.isProtection()).isEqualTo(bool[1]);
        assertThat(dptValue.isOversupply()).isEqualTo(bool[2]);
        assertThat(dptValue.isOverrun()).isEqualTo(bool[3]);
        assertThat(dptValue.isDHWNormal()).isEqualTo(bool[4]);
        assertThat(dptValue.isDHWLegionellaProtection()).isEqualTo(bool[5]);
        assertThat(dptValue.isRoomHeatingComfort()).isEqualTo(bool[6]);
        assertThat(dptValue.isRoomHeatingMaxFlowTemperature()).isEqualTo(bool[7]);
    }

    @Test
    @DisplayName("Test ForcingSignalCooling")
    public void testForcingSignalCooling() {
        this.assertForcingSignalCooling((byte) 0x00, false, "not forced");
        this.assertForcingSignalCooling((byte) 0x01, true, "forced");
    }

    private void assertForcingSignalCooling(final byte b, final boolean bool, final String text) {
        final var dptValue = new ForcingSignalCooling(bool);
        assertThat(new ForcingSignalCooling(b)).isEqualTo(dptValue);

        assertThat(dptValue.isForceRequest()).isEqualTo(bool);
        assertThat(dptValue.toText()).isEqualTo(text);
    }

    @Test
    @DisplayName("Test RoomHeatingControllerStatus")
    public void testStatusRoomHeatingController() {
        this.assertStatusRoomHeatingController((byte) 0x00, new boolean[]{false, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatingController((byte) 0x01, new boolean[]{true, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatingController((byte) 0x02, new boolean[]{false, true, false, false, false, false, false, false});
        this.assertStatusRoomHeatingController((byte) 0x04, new boolean[]{false, false, true, false, false, false, false, false});
        this.assertStatusRoomHeatingController((byte) 0x08, new boolean[]{false, false, false, true, false, false, false, false});
        this.assertStatusRoomHeatingController((byte) 0x10, new boolean[]{false, false, false, false, true, false, false, false});
        this.assertStatusRoomHeatingController((byte) 0x20, new boolean[]{false, false, false, false, false, true, false, false});
        this.assertStatusRoomHeatingController((byte) 0x40, new boolean[]{false, false, false, false, false, false, true, false});
        this.assertStatusRoomHeatingController((byte) 0x80, new boolean[]{false, false, false, false, false, false, false, true});
    }

    private void assertStatusRoomHeatingController(final byte b, final boolean[] bool) {
        final var dptValue = new RoomHeatingControllerStatus(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7]);
        assertThat(new RoomHeatingControllerStatus(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool[0]);
        assertThat(dptValue.isStatusEco()).isEqualTo(bool[1]);
        assertThat(dptValue.isTemperatureFlowLimit()).isEqualTo(bool[2]);
        assertThat(dptValue.isTemperatureReturnLimit()).isEqualTo(bool[3]);
        assertThat(dptValue.isStatusMorningBoost()).isEqualTo(bool[4]);
        assertThat(dptValue.isStatusStartOptimizationActive()).isEqualTo(bool[5]);
        assertThat(dptValue.isStatusStopOptimizationActive()).isEqualTo(bool[6]);
        assertThat(dptValue.isSummerMode()).isEqualTo(bool[7]);
    }

    @Test
    @DisplayName("Test SolarDHWControllerStatus")
    public void testStatusSolarDHWController() {
        this.assertStatusSolarDHWController((byte) 0x00, new boolean[]{false, false, false});
        this.assertStatusSolarDHWController((byte) 0x01, new boolean[]{true, false, false});
        this.assertStatusSolarDHWController((byte) 0x02, new boolean[]{false, true, false});
        this.assertStatusSolarDHWController((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertStatusSolarDHWController(final byte b, final boolean[] bool) {
        final var dptValue = new SolarDHWControllerStatus(bool[0], bool[1], bool[2]);
        assertThat(new SolarDHWControllerStatus(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool[0]);
        assertThat(dptValue.isStatusDHWLoadActive()).isEqualTo(bool[1]);
        assertThat(dptValue.isSolarLoadSufficient()).isEqualTo(bool[2]);
    }

    @Test
    @DisplayName("Test FuelTypeSet")
    public void testFuelTypeSet() {
        this.assertFuelTypeSet((byte) 0x00, new boolean[]{false, false, false});
        this.assertFuelTypeSet((byte) 0x01, new boolean[]{true, false, false});
        this.assertFuelTypeSet((byte) 0x02, new boolean[]{false, true, false});
        this.assertFuelTypeSet((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertFuelTypeSet(final byte b, final boolean[] bool) {
        final var dptValue = new FuelTypeSet(bool[0], bool[1], bool[2]);
        assertThat(new FuelTypeSet(b)).isEqualTo(dptValue);

        assertThat(dptValue.isOilFuelSupported()).isEqualTo(bool[0]);
        assertThat(dptValue.isGasFuelSupported()).isEqualTo(bool[1]);
        assertThat(dptValue.isSolidStateFuelSupported()).isEqualTo(bool[2]);
    }

    @Test
    @DisplayName("Test RoomCoolingControllerStatus")
    public void testStatusRoomCoolingController() {
        this.assertStatusRoomCoolingController((byte) 0x00, false, "no fault");
        this.assertStatusRoomCoolingController((byte) 0x01, true, "fault");
    }

    private void assertStatusRoomCoolingController(final byte b, final boolean bool, final String text) {
        final var dptValue = new RoomCoolingControllerStatus(bool);
        assertThat(new RoomCoolingControllerStatus(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool);
        assertThat(dptValue.toText()).isEqualTo(text);
    }

    @Test
    @DisplayName("Test VentilationControllerStatus")
    public void testStatusVentilationController() {
        this.assertStatusVentilationController((byte) 0x00, new boolean[]{false, false, false, false});
        this.assertStatusVentilationController((byte) 0x01, new boolean[]{true, false, false, false});
        this.assertStatusVentilationController((byte) 0x02, new boolean[]{false, true, false, false});
        this.assertStatusVentilationController((byte) 0x04, new boolean[]{false, false, true, false});
        this.assertStatusVentilationController((byte) 0x08, new boolean[]{false, false, false, true});
    }

    private void assertStatusVentilationController(final byte b, final boolean[] bool) {
        final var dptValue = new VentilationControllerStatus(bool[0], bool[1], bool[2], bool[3]);
        assertThat(new VentilationControllerStatus(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool[0]);
        assertThat(dptValue.isFanActive()).isEqualTo(bool[1]);
        assertThat(dptValue.isHeatingModeActive()).isEqualTo(bool[2]);
        assertThat(dptValue.isCoolingModeActive()).isEqualTo(bool[3]);
    }

    @Test
    @DisplayName("Test LightingActuatorErrorInfo")
    public void testLightingActuatorErrorInfo() {
        this.assertLightingActuatorErrorInfo((byte) 0x00, new boolean[]{false, false, false, false, false, false, false});
        this.assertLightingActuatorErrorInfo((byte) 0x01, new boolean[]{true, false, false, false, false, false, false});
        this.assertLightingActuatorErrorInfo((byte) 0x02, new boolean[]{false, true, false, false, false, false, false});
        this.assertLightingActuatorErrorInfo((byte) 0x04, new boolean[]{false, false, true, false, false, false, false});
        this.assertLightingActuatorErrorInfo((byte) 0x08, new boolean[]{false, false, false, true, false, false, false});
        this.assertLightingActuatorErrorInfo((byte) 0x10, new boolean[]{false, false, false, false, true, false, false});
        this.assertLightingActuatorErrorInfo((byte) 0x20, new boolean[]{false, false, false, false, false, true, false});
        this.assertLightingActuatorErrorInfo((byte) 0x40, new boolean[]{false, false, false, false, false, false, true});
    }

    private void assertLightingActuatorErrorInfo(final byte b, final boolean[] bool) {
        final var dptValue = new LightingActuatorErrorInfo(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6]);
        assertThat(new LightingActuatorErrorInfo(b)).isEqualTo(dptValue);

        assertThat(dptValue.isErrorLoad()).isEqualTo(bool[0]);
        assertThat(dptValue.isUndervoltage()).isEqualTo(bool[1]);
        assertThat(dptValue.isOvercurrent()).isEqualTo(bool[2]);
        assertThat(dptValue.isUnderload()).isEqualTo(bool[3]);
        assertThat(dptValue.isDefectiveLoad()).isEqualTo(bool[4]);
        assertThat(dptValue.isLampFailure()).isEqualTo(bool[5]);
        assertThat(dptValue.isOverheat()).isEqualTo(bool[6]);
    }

    @Test
    @DisplayName("Test RadioFrequencyCommunicationModeInfo")
    public void testRadioFrequencyCommunicationModeInfo() {
        this.assertRadioFrequencyCommunicationModeInfo((byte) 0x00, new boolean[]{false, false, false});
        this.assertRadioFrequencyCommunicationModeInfo((byte) 0x01, new boolean[]{true, false, false});
        this.assertRadioFrequencyCommunicationModeInfo((byte) 0x02, new boolean[]{false, true, false});
        this.assertRadioFrequencyCommunicationModeInfo((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertRadioFrequencyCommunicationModeInfo(final byte b, final boolean[] bool) {
        final var dptValue = new RadioFrequencyCommunicationModeInfo(bool[0], bool[1], bool[2]);
        assertThat(new RadioFrequencyCommunicationModeInfo(b)).isEqualTo(dptValue);

        assertThat(dptValue.isAsynchronous()).isEqualTo(bool[0]);
        assertThat(dptValue.isMaster()).isEqualTo(bool[1]);
        assertThat(dptValue.isSlave()).isEqualTo(bool[2]);
    }

    @Test
    @DisplayName("Test CEMIServerSupportedFilteringMode")
    public void testCEMIServerSupportedFilteringMode() {
        this.assertCEMIServerSupportedFilteringMode((byte) 0x00, new boolean[]{false, false, false});
        this.assertCEMIServerSupportedFilteringMode((byte) 0x01, new boolean[]{true, false, false});
        this.assertCEMIServerSupportedFilteringMode((byte) 0x02, new boolean[]{false, true, false});
        this.assertCEMIServerSupportedFilteringMode((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertCEMIServerSupportedFilteringMode(final byte b, final boolean[] bool) {
        final var dptValue = new CEMIServerSupportedFilteringMode(bool[0], bool[1], bool[2]);
        assertThat(new CEMIServerSupportedFilteringMode(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFilteredByDomainAddress()).isEqualTo(bool[0]);
        assertThat(dptValue.isFilteredBySerialNumber()).isEqualTo(bool[1]);
        assertThat(dptValue.isFilteredByDomainAddressAndSerialNumber()).isEqualTo(bool[2]);
    }

    @Test
    @DisplayName("Test SecurityReport")
    public void testSecurityReport() {
        this.assertSecurityReport((byte) 0x00, false, "no failure");
        this.assertSecurityReport((byte) 0x01, true, "failure");
    }

    private void assertSecurityReport(final byte b, final boolean bool, final String text) {
        final var dptValue = new SecurityReport(bool);
        assertThat(new SecurityReport(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFailure()).isEqualTo(bool);
        assertThat(dptValue.toText()).isEqualTo(text);
    }

    @Test
    @DisplayName("Test ChannelActivation8")
    public void testChannelActivation8() {
        this.assertChannelActivation8((byte) 0x00, new boolean[]{false, false, false, false, false, false, false, false}, "No channel active");
        this.assertChannelActivation8((byte) 0x01, new boolean[]{true, false, false, false, false, false, false, false}, "1");
        this.assertChannelActivation8((byte) 0x02, new boolean[]{false, true, false, false, false, false, false, false}, "2");
        this.assertChannelActivation8((byte) 0x04, new boolean[]{false, false, true, false, false, false, false, false}, "3");
        this.assertChannelActivation8((byte) 0x08, new boolean[]{false, false, false, true, false, false, false, false}, "4");
        this.assertChannelActivation8((byte) 0x10, new boolean[]{false, false, false, false, true, false, false, false}, "5");
        this.assertChannelActivation8((byte) 0x20, new boolean[]{false, false, false, false, false, true, false, false}, "6");
        this.assertChannelActivation8((byte) 0x40, new boolean[]{false, false, false, false, false, false, true, false}, "7");
        this.assertChannelActivation8((byte) 0x80, new boolean[]{false, false, false, false, false, false, false, true}, "8");

        // multiple channels
        this.assertChannelActivation8((byte) 0x87, new boolean[]{true, true, true, false, false, false, false, true}, "1, 2, 3, 8");
        this.assertChannelActivation8((byte) 0xFF, new boolean[]{true, true, true, true, true, true, true, true}, "1, 2, 3, 4, 5, 6, 7, 8");
    }

    private void assertChannelActivation8(final byte b, final boolean[] bool, final String text) {
        final var dptValue = new ChannelActivation8(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7]);
        assertThat(new ChannelActivation8(b)).isEqualTo(dptValue);

        for (var i = 0; i < 8; i++) {
            assertThat(dptValue.isChannelActive(i + 1)).isEqualTo(bool[i]);
        }
        assertThat(dptValue.toText()).isEqualTo(text);

        assertThatThrownBy(() -> dptValue.isChannelActive(0)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Channel must be between 1 and 8");
        assertThatThrownBy(() -> dptValue.isChannelActive(9)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Channel must be between 1 and 8");
    }

    @Test
    @DisplayName("Test VirtualContactStatus")
    public void testVirtualContactStatus() {
        this.assertVirtualContactStatus((byte) 0x00, new boolean[]{false, false, false, false, false, false, false, false}, "No contact closed");
        this.assertVirtualContactStatus((byte) 0x01, new boolean[]{true, false, false, false, false, false, false, false}, "1");
        this.assertVirtualContactStatus((byte) 0x02, new boolean[]{false, true, false, false, false, false, false, false}, "2");
        this.assertVirtualContactStatus((byte) 0x04, new boolean[]{false, false, true, false, false, false, false, false}, "3");
        this.assertVirtualContactStatus((byte) 0x08, new boolean[]{false, false, false, true, false, false, false, false}, "4");
        this.assertVirtualContactStatus((byte) 0x10, new boolean[]{false, false, false, false, true, false, false, false}, "5");
        this.assertVirtualContactStatus((byte) 0x20, new boolean[]{false, false, false, false, false, true, false, false}, "6");
        this.assertVirtualContactStatus((byte) 0x40, new boolean[]{false, false, false, false, false, false, true, false}, "7");
        this.assertVirtualContactStatus((byte) 0x80, new boolean[]{false, false, false, false, false, false, false, true}, "8");
    }

    private void assertVirtualContactStatus(final byte b, final boolean[] bool, final String text) {
        final var dptValue = new VirtualContactStatus(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7]);
        assertThat(new VirtualContactStatus(b)).isEqualTo(dptValue);

        for (var i = 0; i < 8; i++) {
            assertThat(dptValue.isClosed(i + 1)).isEqualTo(bool[i]);
        }
        assertThat(dptValue.toText()).isEqualTo(text);

        assertThatThrownBy(() -> dptValue.isClosed(0)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Contact must be between 1 and 8");
        assertThatThrownBy(() -> dptValue.isClosed(9)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Contact must be between 1 and 8");
    }

    @Test
    @DisplayName("Test PhaseStatus")
    public void testPhaseStatus() {
        this.assertPhaseStatus((byte) 0x00, new boolean[]{false, false, false}, "No phase present");
        this.assertPhaseStatus((byte) 0x01, new boolean[]{true, false, false}, "1");
        this.assertPhaseStatus((byte) 0x02, new boolean[]{false, true, false}, "2");
        this.assertPhaseStatus((byte) 0x04, new boolean[]{false, false, true}, "3");
    }

    private void assertPhaseStatus(final byte b, final boolean[] bool, final String text) {
        final var dptValue = new PhaseStatus(bool[0], bool[1], bool[2]);
        assertThat(new PhaseStatus(b)).isEqualTo(dptValue);

        for (var i = 0; i < 3; i++) {
            assertThat(dptValue.isPresent(i + 1)).isEqualTo(bool[i]);
        }
        assertThat(dptValue.toText()).isEqualTo(text);

        assertThatThrownBy(() -> dptValue.isPresent(0)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Phase must be between 1 and 3");
        assertThatThrownBy(() -> dptValue.isPresent(4)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Phase must be between 1 and 3");
    }

    /**
     * Test constructor of {@link DPT21Value}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT21Value.class);
    }
}
