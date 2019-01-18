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

package li.pitschmann.knx.link.datapoint.value;

import li.pitschmann.knx.link.datapoint.value.DPT21Value.*;
import li.pitschmann.test.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test {@link DPT21Value}
 *
 * @author PITSCHR
 */
public final class DPT21ValueTest {
    /**
     * Test {@link GeneralStatus}
     */
    @Test
    public void testGeneralStatus() {
        this.assertGeneralStatus((byte) 0x00, new boolean[]{false, false, false, false, false});
        this.assertGeneralStatus((byte) 0x01, new boolean[]{true, false, false, false, false});
        this.assertGeneralStatus((byte) 0x02, new boolean[]{false, true, false, false, false});
        this.assertGeneralStatus((byte) 0x04, new boolean[]{false, false, true, false, false});
        this.assertGeneralStatus((byte) 0x08, new boolean[]{false, false, false, true, false});
        this.assertGeneralStatus((byte) 0x10, new boolean[]{false, false, false, false, true});
    }

    private void assertGeneralStatus(final byte b, final boolean[] bool) {
        GeneralStatus dptValue = new GeneralStatus(bool[0], bool[1], bool[2], bool[3], bool[4]);
        assertThat(new GeneralStatus(b)).isEqualTo(dptValue);

        assertThat(dptValue.isOutOfService()).isEqualTo(bool[0]);
        assertThat(dptValue.isFault()).isEqualTo(bool[1]);
        assertThat(dptValue.isOverridden()).isEqualTo(bool[2]);
        assertThat(dptValue.isInAlarm()).isEqualTo(bool[3]);
        assertThat(dptValue.isAlarmNotAcknowledged()).isEqualTo(bool[4]);
    }

    /**
     * Test {@link DeviceControl}
     */
    @Test
    public void testDeviceControl() {
        this.assertDeviceControl((byte) 0x00, new boolean[]{false, false, false});
        this.assertDeviceControl((byte) 0x01, new boolean[]{true, false, false});
        this.assertDeviceControl((byte) 0x02, new boolean[]{false, true, false});
        this.assertDeviceControl((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertDeviceControl(final byte b, final boolean[] bool) {
        DeviceControl dptValue = new DeviceControl(bool[0], bool[1], bool[2]);
        assertThat(new DeviceControl(b)).isEqualTo(dptValue);

        assertThat(dptValue.isUserApplicationStopped()).isEqualTo(bool[0]);
        assertThat(dptValue.isOwnIndividualAddress()).isEqualTo(bool[1]);
        assertThat(dptValue.isVerifyModeOn()).isEqualTo(bool[2]);
    }

    /**
     * Test {@link ForcingSignal}
     */
    @Test
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
        ForcingSignal dptValue = new ForcingSignal(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7]);
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

    /**
     * Test {@link ForcingSignalCooling}
     */
    @Test
    public void testForcingSignalCooling() {
        this.assertForcingSignalCooling((byte) 0x00, false);
        this.assertForcingSignalCooling((byte) 0x01, true);
    }

    private void assertForcingSignalCooling(final byte b, final boolean bool) {
        ForcingSignalCooling dptValue = new ForcingSignalCooling(bool);
        assertThat(new ForcingSignalCooling(b)).isEqualTo(dptValue);

        assertThat(dptValue.isForceRequest()).isEqualTo(bool);
    }

    /**
     * Test {@link StatusRoomHeatingController}
     */
    @Test
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
        StatusRoomHeatingController dptValue = new StatusRoomHeatingController(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6],
                bool[7]);
        assertThat(new StatusRoomHeatingController(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool[0]);
        assertThat(dptValue.isStatusEco()).isEqualTo(bool[1]);
        assertThat(dptValue.isTemperatureFlowLimit()).isEqualTo(bool[2]);
        assertThat(dptValue.isTemperatureReturnLimit()).isEqualTo(bool[3]);
        assertThat(dptValue.isStatusMorningBoost()).isEqualTo(bool[4]);
        assertThat(dptValue.isStatusStartOptimizationActive()).isEqualTo(bool[5]);
        assertThat(dptValue.isStatusStopOptimizationActive()).isEqualTo(bool[6]);
        assertThat(dptValue.isSummerMode()).isEqualTo(bool[7]);
    }

    /**
     * Test {@link StatusSolarDHWController}
     */
    @Test
    public void testStatusSolarDHWController() {
        this.assertStatusSolarDHWController((byte) 0x00, new boolean[]{false, false, false});
        this.assertStatusSolarDHWController((byte) 0x01, new boolean[]{true, false, false});
        this.assertStatusSolarDHWController((byte) 0x02, new boolean[]{false, true, false});
        this.assertStatusSolarDHWController((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertStatusSolarDHWController(final byte b, final boolean[] bool) {
        StatusSolarDHWController dptValue = new StatusSolarDHWController(bool[0], bool[1], bool[2]);
        assertThat(new StatusSolarDHWController(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool[0]);
        assertThat(dptValue.isStatusDHWLoadActive()).isEqualTo(bool[1]);
        assertThat(dptValue.isSolarLoadSufficient()).isEqualTo(bool[2]);
    }

    /**
     * Test {@link FuelTypeSet}
     */
    @Test
    public void testFuelTypeSet() {
        this.assertFuelTypeSet((byte) 0x00, new boolean[]{false, false, false});
        this.assertFuelTypeSet((byte) 0x01, new boolean[]{true, false, false});
        this.assertFuelTypeSet((byte) 0x02, new boolean[]{false, true, false});
        this.assertFuelTypeSet((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertFuelTypeSet(final byte b, final boolean[] bool) {
        FuelTypeSet dptValue = new FuelTypeSet(bool[0], bool[1], bool[2]);
        assertThat(new FuelTypeSet(b)).isEqualTo(dptValue);

        assertThat(dptValue.isOilFuelSupported()).isEqualTo(bool[0]);
        assertThat(dptValue.isGasFuelSupported()).isEqualTo(bool[1]);
        assertThat(dptValue.isSolidStateFuelSupported()).isEqualTo(bool[2]);
    }

    /**
     * Test {@link StatusRoomCoolingController}
     */
    @Test
    public void testStatusRoomCoolingController() {
        this.assertStatusRoomCoolingController((byte) 0x00, false);
        this.assertStatusRoomCoolingController((byte) 0x01, true);
    }

    private void assertStatusRoomCoolingController(final byte b, final boolean bool) {
        StatusRoomCoolingController dptValue = new StatusRoomCoolingController(bool);
        assertThat(new StatusRoomCoolingController(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool);
    }

    /**
     * Test {@link StatusVentilationController}
     */
    @Test
    public void testStatusVentilationController() {
        this.assertStatusVentilationController((byte) 0x00, new boolean[]{false, false, false, false});
        this.assertStatusVentilationController((byte) 0x01, new boolean[]{true, false, false, false});
        this.assertStatusVentilationController((byte) 0x02, new boolean[]{false, true, false, false});
        this.assertStatusVentilationController((byte) 0x04, new boolean[]{false, false, true, false});
        this.assertStatusVentilationController((byte) 0x08, new boolean[]{false, false, false, true});
    }

    private void assertStatusVentilationController(final byte b, final boolean[] bool) {
        StatusVentilationController dptValue = new StatusVentilationController(bool[0], bool[1], bool[2], bool[3]);
        assertThat(new StatusVentilationController(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool[0]);
        assertThat(dptValue.isFanActive()).isEqualTo(bool[1]);
        assertThat(dptValue.isHeatingModeActive()).isEqualTo(bool[2]);
        assertThat(dptValue.isCoolingModeActive()).isEqualTo(bool[3]);
    }

    /**
     * Test {@link LightingActuatorErrorInfo}
     */
    @Test
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
        LightingActuatorErrorInfo dptValue = new LightingActuatorErrorInfo(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6]);
        assertThat(new LightingActuatorErrorInfo(b)).isEqualTo(dptValue);

        assertThat(dptValue.isErrorLoad()).isEqualTo(bool[0]);
        assertThat(dptValue.isUndervoltage()).isEqualTo(bool[1]);
        assertThat(dptValue.isOvercurrent()).isEqualTo(bool[2]);
        assertThat(dptValue.isUnderload()).isEqualTo(bool[3]);
        assertThat(dptValue.isDefectiveLoad()).isEqualTo(bool[4]);
        assertThat(dptValue.isLampFailure()).isEqualTo(bool[5]);
        assertThat(dptValue.isOverheat()).isEqualTo(bool[6]);
    }

    /**
     * Test {@link RadioFrequencyCommunicationModeInfo}
     */
    @Test
    public void testRadioFrequencyCommunicationModeInfo() {
        this.assertRadioFrequencyCommunicationModeInfo((byte) 0x00, new boolean[]{false, false, false});
        this.assertRadioFrequencyCommunicationModeInfo((byte) 0x01, new boolean[]{true, false, false});
        this.assertRadioFrequencyCommunicationModeInfo((byte) 0x02, new boolean[]{false, true, false});
        this.assertRadioFrequencyCommunicationModeInfo((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertRadioFrequencyCommunicationModeInfo(final byte b, final boolean[] bool) {
        RadioFrequencyCommunicationModeInfo dptValue = new RadioFrequencyCommunicationModeInfo(bool[0], bool[1], bool[2]);
        assertThat(new RadioFrequencyCommunicationModeInfo(b)).isEqualTo(dptValue);

        assertThat(dptValue.isAsynchronous()).isEqualTo(bool[0]);
        assertThat(dptValue.isMaster()).isEqualTo(bool[1]);
        assertThat(dptValue.isSlave()).isEqualTo(bool[2]);
    }

    /**
     * Test {@link CEMIServerSupportedFilteringMode}
     */
    @Test
    public void testCEMIServerSupportedFilteringMode() {
        this.assertCEMIServerSupportedFilteringMode((byte) 0x00, new boolean[]{false, false, false});
        this.assertCEMIServerSupportedFilteringMode((byte) 0x01, new boolean[]{true, false, false});
        this.assertCEMIServerSupportedFilteringMode((byte) 0x02, new boolean[]{false, true, false});
        this.assertCEMIServerSupportedFilteringMode((byte) 0x04, new boolean[]{false, false, true});
    }

    private void assertCEMIServerSupportedFilteringMode(final byte b, final boolean[] bool) {
        CEMIServerSupportedFilteringMode dptValue = new CEMIServerSupportedFilteringMode(bool[0], bool[1], bool[2]);
        assertThat(new CEMIServerSupportedFilteringMode(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFilteredByDomainAddress()).isEqualTo(bool[0]);
        assertThat(dptValue.isFilteredBySerialNumber()).isEqualTo(bool[1]);
        assertThat(dptValue.isFilteredByDomainAddressAndSerialNumber()).isEqualTo(bool[2]);
    }

    /**
     * Test {@link SecurityReport}
     */
    @Test
    public void testSecurityReport() {
        this.assertSecurityReport((byte) 0x00, false);
        this.assertSecurityReport((byte) 0x01, true);
    }

    private void assertSecurityReport(final byte b, final boolean bool) {
        SecurityReport dptValue = new SecurityReport(bool);
        assertThat(new SecurityReport(b)).isEqualTo(dptValue);

        assertThat(dptValue.isFailure()).isEqualTo(bool);
    }

    /**
     * Test {@link ChannelActivation8}
     */
    @Test
    public void testChannelActivation8() {
        this.assertChannelActivation8((byte) 0x00, new boolean[]{false, false, false, false, false, false, false, false});
        this.assertChannelActivation8((byte) 0x01, new boolean[]{true, false, false, false, false, false, false, false});
        this.assertChannelActivation8((byte) 0x02, new boolean[]{false, true, false, false, false, false, false, false});
        this.assertChannelActivation8((byte) 0x04, new boolean[]{false, false, true, false, false, false, false, false});
        this.assertChannelActivation8((byte) 0x08, new boolean[]{false, false, false, true, false, false, false, false});
        this.assertChannelActivation8((byte) 0x10, new boolean[]{false, false, false, false, true, false, false, false});
        this.assertChannelActivation8((byte) 0x20, new boolean[]{false, false, false, false, false, true, false, false});
        this.assertChannelActivation8((byte) 0x40, new boolean[]{false, false, false, false, false, false, true, false});
        this.assertChannelActivation8((byte) 0x80, new boolean[]{false, false, false, false, false, false, false, true});
    }

    private void assertChannelActivation8(final byte b, final boolean[] bool) {
        ChannelActivation8 dptValue = new ChannelActivation8(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7]);
        assertThat(new ChannelActivation8(b)).isEqualTo(dptValue);

        for (int i = 0; i < 8; i++) {
            assertThat(dptValue.isChannelActive(i + 1)).isEqualTo(bool[i]);
        }

        assertThatThrownBy(() -> dptValue.isChannelActive(0)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Channel must be between 1 and 8");
        assertThatThrownBy(() -> dptValue.isChannelActive(9)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Channel must be between 1 and 8");
    }

    /**
     * Test constructor of {@link DPT21Value}
     */
    @Test
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT21Value.class);
    }
}
