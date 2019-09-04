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

import com.google.common.base.Preconditions;
import li.pitschmann.knx.link.datapoint.DPT21;
import li.pitschmann.utils.Bytes;

import javax.annotation.Nonnull;

/**
 * Data Point Value for {@link DPT21} (21.xxx)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | b   b   b   b   b   b   b   b |
 * Encoding    | B   B   B   B   B   B   B   B |
 *             +---+---+---+---+---+---+---+---+
 * Format:     8 bits (B<sub>8</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT21Value {
    private DPT21Value() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * <strong>21.001</strong> General Status
     *
     * @see DPT21#GENERAL_STATUS
     */
    public static final class GeneralStatus extends AbstractDataPointFlag<DPT21.GeneralStatus> {
        public GeneralStatus(final byte b) {
            super(DPT21.GENERAL_STATUS, b);
        }

        public GeneralStatus(final boolean outOfService, final boolean fault, final boolean overridden, final boolean inAlarm,
                             final boolean alarmNotAcknowledged) {
            this(Bytes.toByte(false, false, false, alarmNotAcknowledged, inAlarm, overridden, fault, outOfService));
        }

        public boolean isOutOfService() {
            return this.isSet(0);
        }

        public boolean isFault() {
            return this.isSet(1);
        }

        public boolean isOverridden() {
            return this.isSet(2);
        }

        public boolean isInAlarm() {
            return this.isSet(3);
        }

        public boolean isAlarmNotAcknowledged() {
            return this.isSet(4);
        }
    }

    /**
     * <strong>21.002</strong> Device Control
     *
     * @see DPT21#DEVICE_CONTROL
     */
    public static final class DeviceControl extends AbstractDataPointFlag<DPT21.DeviceControl> {
        public DeviceControl(final byte b) {
            super(DPT21.DEVICE_CONTROL, b);
        }

        public DeviceControl(final boolean userApplicationStopped, final boolean ownIndividualAddress, final boolean verifyModeOn) {
            this(Bytes.toByte(false, false, false, false, false, verifyModeOn, ownIndividualAddress, userApplicationStopped));
        }

        public boolean isUserApplicationStopped() {
            return this.isSet(0);
        }

        public boolean isOwnIndividualAddress() {
            return this.isSet(1);
        }

        public boolean isVerifyModeOn() {
            return this.isSet(2);
        }
    }

    /**
     * <strong>21.100</strong> Forcing Signal
     *
     * @see DPT21#FORCING_SIGNAL
     */
    public static final class ForcingSignal extends AbstractDataPointFlag<DPT21.ForcingSignal> {
        public ForcingSignal(final byte b) {
            super(DPT21.FORCING_SIGNAL, b);
        }

        public ForcingSignal(final boolean forceRequest, final boolean protection, final boolean oversupply, final boolean overrun,
                             final boolean dhwNormal, final boolean dhwLegionellaProtection, final boolean roomHeatingComfort,
                             final boolean roomHeatingMaxFlowTemperature) {
            this(Bytes.toByte(roomHeatingMaxFlowTemperature, roomHeatingComfort, dhwLegionellaProtection, dhwNormal, overrun, oversupply, protection,
                    forceRequest));
        }

        public boolean isForceRequest() {
            return this.isSet(0);
        }

        public boolean isProtection() {
            return this.isSet(1);
        }

        public boolean isOversupply() {
            return this.isSet(2);
        }

        public boolean isOverrun() {
            return this.isSet(3);
        }

        public boolean isDHWNormal() {
            return this.isSet(4);
        }

        public boolean isDHWLegionellaProtection() {
            return this.isSet(5);
        }

        public boolean isRoomHeatingComfort() {
            return this.isSet(6);
        }

        public boolean isRoomHeatingMaxFlowTemperature() {
            return this.isSet(7);
        }

    }

    /**
     * <strong>21.101</strong> Forcing Signal Cool
     *
     * @see DPT21#FORCING_SIGNAL_COOLING
     */
    public static final class ForcingSignalCooling extends AbstractDataPointFlag<DPT21.ForcingSignalCooling> {
        public ForcingSignalCooling(final byte b) {
            super(DPT21.FORCING_SIGNAL_COOLING, b);
        }

        public ForcingSignalCooling(final boolean forceRequest) {
            this(Bytes.toByte(false, false, false, false, false, false, false, forceRequest));
        }

        public boolean isForceRequest() {
            return this.isSet(0);
        }

        @Nonnull
        @Override
        public String toText() {
            return isForceRequest() ? "forced" : "not forced";
        }
    }

    /**
     * <strong>21.102</strong> Room Heating Controller Status
     *
     * @see DPT21#STATUS_ROOM_HEATING_CONTROLLER
     */
    public static final class StatusRoomHeatingController extends AbstractDataPointFlag<DPT21.StatusRoomHeatingController> {
        public StatusRoomHeatingController(final byte b) {
            super(DPT21.STATUS_ROOM_HEATING_CONTROLLER, b);
        }

        public StatusRoomHeatingController(final boolean fault, final boolean statusEco, final boolean temperatureFlowLimit,
                                           final boolean temperatureReturnLimit, final boolean statusMorningBoost,
                                           final boolean statusStartOptimizationActive, final boolean statusStopOptimizationActive,
                                           final boolean summerMode) {
            this(Bytes.toByte(summerMode, statusStopOptimizationActive, statusStartOptimizationActive, statusMorningBoost, temperatureReturnLimit,
                    temperatureFlowLimit, statusEco, fault));
        }

        public boolean isFault() {
            return this.isSet(0);
        }

        public boolean isStatusEco() {
            return this.isSet(1);
        }

        public boolean isTemperatureFlowLimit() {
            return this.isSet(2);
        }

        public boolean isTemperatureReturnLimit() {
            return this.isSet(3);
        }

        public boolean isStatusMorningBoost() {
            return this.isSet(4);
        }

        public boolean isStatusStartOptimizationActive() {
            return this.isSet(5);
        }

        public boolean isStatusStopOptimizationActive() {
            return this.isSet(6);
        }

        public boolean isSummerMode() {
            return this.isSet(7);
        }
    }

    /**
     * <strong>21.103</strong> Solar DHW Controller Status
     *
     * @see DPT21#STATUS_SOLAR_DHW_CONTROLLER
     */
    public static final class StatusSolarDHWController extends AbstractDataPointFlag<DPT21.StatusSolarDHWController> {
        public StatusSolarDHWController(final byte b) {
            super(DPT21.STATUS_SOLAR_DHW_CONTROLLER, b);
        }

        public StatusSolarDHWController(final boolean fault, final boolean statusDHWLoadActive, final boolean solarLoadSufficient) {
            this(Bytes.toByte(false, false, false, false, false, solarLoadSufficient, statusDHWLoadActive, fault));
        }

        public boolean isFault() {
            return this.isSet(0);
        }

        public boolean isStatusDHWLoadActive() {
            return this.isSet(1);
        }

        public boolean isSolarLoadSufficient() {
            return this.isSet(2);
        }
    }

    /**
     * <strong>21.104</strong> Fuel Type Set
     *
     * @see DPT21#FUEL_TYPE_SET
     */
    public static final class FuelTypeSet extends AbstractDataPointFlag<DPT21.FuelTypeSet> {
        public FuelTypeSet(final byte b) {
            super(DPT21.FUEL_TYPE_SET, b);
        }

        public FuelTypeSet(final boolean oilFuelSupported, final boolean gasFuelSupported, final boolean solidStateFuelSupported) {
            this(Bytes.toByte(false, false, false, false, false, solidStateFuelSupported, gasFuelSupported, oilFuelSupported));
        }

        public boolean isOilFuelSupported() {
            return this.isSet(0);
        }

        public boolean isGasFuelSupported() {
            return this.isSet(1);
        }

        public boolean isSolidStateFuelSupported() {
            return this.isSet(2);
        }
    }

    /**
     * <strong>21.105</strong> Room Cooling Controller Status
     *
     * @see DPT21#STATUS_ROOM_COOLING_CONTROLLER
     */
    public static final class StatusRoomCoolingController extends AbstractDataPointFlag<DPT21.StatusRoomCoolingController> {
        public StatusRoomCoolingController(final byte b) {
            super(DPT21.STATUS_ROOM_COOLING_CONTROLLER, b);
        }

        public StatusRoomCoolingController(final boolean fault) {
            this(Bytes.toByte(false, false, false, false, false, false, false, fault));
        }

        public boolean isFault() {
            return this.isSet(0);
        }

        @Nonnull
        @Override
        public String toText() {
            return isFault() ? "fault" : "no fault";
        }
    }

    /**
     * <strong>21.106</strong> Ventilation Controller Status
     *
     * @see DPT21#STATUS_VENTILATION_CONTROLLER
     */
    public static final class StatusVentilationController extends AbstractDataPointFlag<DPT21.StatusVentilationController> {
        public StatusVentilationController(final byte b) {
            super(DPT21.STATUS_VENTILATION_CONTROLLER, b);
        }

        public StatusVentilationController(final boolean fault, final boolean fanActive, final boolean heatingModeActive,
                                           final boolean coolingModeActive) {
            this(Bytes.toByte(false, false, false, false, coolingModeActive, heatingModeActive, fanActive, fault));
        }

        public boolean isFault() {
            return this.isSet(0);
        }

        public boolean isFanActive() {
            return this.isSet(1);
        }

        public boolean isHeatingModeActive() {
            return this.isSet(2);
        }

        public boolean isCoolingModeActive() {
            return this.isSet(3);
        }
    }

    /**
     * <strong>21.601</strong> Lighting Actuator Error Information
     *
     * @see DPT21#LIGHTING_ACTUATOR_ERROR_INFO
     */
    public static final class LightingActuatorErrorInfo extends AbstractDataPointFlag<DPT21.LightingActuatorErrorInfo> {
        public LightingActuatorErrorInfo(final byte b) {
            super(DPT21.LIGHTING_ACTUATOR_ERROR_INFO, b);
        }

        public LightingActuatorErrorInfo(final boolean errorLoad, final boolean undervoltage, final boolean overcurrent, final boolean underload,
                                         final boolean defectiveLoad, final boolean lampFailure, final boolean overheat) {
            this(Bytes.toByte(false, overheat, lampFailure, defectiveLoad, underload, overcurrent, undervoltage, errorLoad));
        }

        public boolean isErrorLoad() {
            return this.isSet(0);
        }

        public boolean isUndervoltage() {
            return this.isSet(1);
        }

        public boolean isOvercurrent() {
            return this.isSet(2);
        }

        public boolean isUnderload() {
            return this.isSet(3);
        }

        public boolean isDefectiveLoad() {
            return this.isSet(4);
        }

        public boolean isLampFailure() {
            return this.isSet(5);
        }

        public boolean isOverheat() {
            return this.isSet(6);
        }
    }

    /**
     * <strong>21.1000</strong> Radio Frequency Communication Mode Info
     *
     * @see DPT21#RADIO_FREQUENCY_COMMUNICATION_MODE_INFO
     */
    public static final class RadioFrequencyCommunicationModeInfo extends AbstractDataPointFlag<DPT21.RadioFrequencyCommunicationModeInfo> {
        public RadioFrequencyCommunicationModeInfo(final byte b) {
            super(DPT21.RADIO_FREQUENCY_COMMUNICATION_MODE_INFO, b);
        }

        public RadioFrequencyCommunicationModeInfo(final boolean asynchronous, final boolean master, final boolean slave) {
            this(Bytes.toByte(false, false, false, false, false, slave, master, asynchronous));
        }

        public boolean isAsynchronous() {
            return this.isSet(0);
        }

        public boolean isMaster() {
            return this.isSet(1);
        }

        public boolean isSlave() {
            return this.isSet(2);
        }
    }

    /**
     * <strong>21.1001</strong> cEMI Server Supported Filtering Mode
     *
     * @see DPT21#CEMI_SERVER_SUPPORTED_FILTERING_MODE
     */
    public static final class CEMIServerSupportedFilteringMode extends AbstractDataPointFlag<DPT21.CEMIServerSupportedFilteringMode> {
        public CEMIServerSupportedFilteringMode(final byte b) {
            super(DPT21.CEMI_SERVER_SUPPORTED_FILTERING_MODE, b);
        }

        public CEMIServerSupportedFilteringMode(final boolean domainAddress, final boolean serialNumber, final boolean domainAddressAndSerialNumber) {
            this(Bytes.toByte(false, false, false, false, false, domainAddressAndSerialNumber, serialNumber, domainAddress));
        }

        public boolean isFilteredByDomainAddress() {
            return this.isSet(0);
        }

        public boolean isFilteredBySerialNumber() {
            return this.isSet(1);
        }

        public boolean isFilteredByDomainAddressAndSerialNumber() {
            return this.isSet(2);
        }
    }

    /**
     * <strong>21.1002</strong> Security Report
     *
     * @see DPT21#SECURITY_REPORT
     */
    public static final class SecurityReport extends AbstractDataPointFlag<DPT21.SecurityReport> {
        public SecurityReport(final byte b) {
            super(DPT21.SECURITY_REPORT, b);
        }

        public SecurityReport(final boolean failure) {
            this(Bytes.toByte(false, false, false, false, false, false, false, failure));
        }

        public boolean isFailure() {
            return this.isSet(0);
        }

        @Nonnull
        @Override
        public String toText() {
            return isFailure() ? "failure" : "no failure";
        }
    }

    /**
     * <strong>21.1010</strong> Channel Activation for 8 channels
     *
     * @see DPT21#CHANNEL_ACTIVATION_8
     */
    public static final class ChannelActivation8 extends AbstractDataPointFlag<DPT21.ChannelActivation8> {
        public ChannelActivation8(final byte b) {
            super(DPT21.CHANNEL_ACTIVATION_8, b);
        }

        public ChannelActivation8(final boolean channel1, final boolean channel2, final boolean channel3, final boolean channel4,
                                  final boolean channel5, final boolean channel6, final boolean channel7, final boolean channel8) {
            this(Bytes.toByte(channel8, channel7, channel6, channel5, channel4, channel3, channel2, channel1));
        }

        public boolean isChannelActive(final int channel) {
            Preconditions.checkArgument(channel >= 1 && channel <= 8, "Channel must be between 1 and 8 (actual: " + channel + ")");
            return this.isSet(channel - 1);
        }

        /**
         * Returns human-friendly representation of active channels.
         * <p/>
         * If only channel 1 is active, then "1" is returned.<br/>
         * If channel 1, 3 and 8 is active, then "1, 3, 8" is returned.<br/>
         *
         * @return human-friendly representation of active channels
         */
        @Nonnull
        @Override
        public String toText() {
            // return list of channels (e.g. "channel 1" if only channel is
            final var sb = new StringBuffer(24);
            for (var i = 0; i < 8; i++) {
                if (this.isSet(i)) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append((i + 1));
                }
            }
            return sb.length() == 0 ? "no channels active" : sb.toString();
        }
    }
}