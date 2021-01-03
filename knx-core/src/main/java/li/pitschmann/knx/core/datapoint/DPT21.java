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
import li.pitschmann.knx.core.datapoint.value.DataPointValue;

/**
 * Data Point Type 21 for 8-Bits flagged messages
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
public final class DPT21 {
    /**
     * <strong>21.001</strong> General Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   b   b   b   b   b |
     * Encoding    | r   r   r   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>3</sub> B<sub>5</sub>)
     * Range:      N = [0 .. 31]
     *                 b0 = Out of Service         {0 = false, 1 = true}
     *                 b1 = Fault                  {0 = false, 1 = true}
     *                 b2 = Overridden             {0 = false, 1 = true}
     *                 b3 = In Alarm               {0 = false, 1 = true}
     *                 b4 = Alarm Not Acknowledged {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.001", "dpst-21-1"})
    public static final GeneralStatus GENERAL_STATUS = new GeneralStatus();
    /**
     * <strong>21.002</strong> Device Control
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   b   b   b |
     * Encoding    | r   r   r   r   r   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>5</sub> B<sub>3</sub>)
     * Range:      N = [0 .. 7]
     *                 b0 = User Application is stopped  {0 = false, 1 = true}
     *                 b1 = Own Individual Address       {0 = false, 1 = true}
     *                 b2 = Verify Mode is On            {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.002", "dpst-21-2"})
    public static final DeviceControl DEVICE_CONTROL = new DeviceControl();
    /**
     * <strong>21.100</strong> Forcing Signal
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | b   b   b   b   b   b   b   b |
     * Encoding    | B   B   B   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (B<sub>8</sub>)
     * Range:      N = [0 .. 255]
     *                 b0 = Force Request                          {0 = false, 1 = true}
     *                 b1 = Protection                             {0 = false, 1 = true}
     *                 b2 = Oversupply                             {0 = false, 1 = true}
     *                 b3 = Overrun                                {0 = false, 1 = true}
     *                 b4 = Load DHW to 'Normal'                   {0 = false, 1 = true}
     *                 b5 = Load DHW to 'Legionella Protection'    {0 = false, 1 = true}
     *                 b6 = Room Heating to 'Comfort'              {0 = false, 1 = true}
     *                 b7 = Room Heating to 'Max flow Temperature' {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.100", "dpst-21-100"})
    public static final ForcingSignal FORCING_SIGNAL = new ForcingSignal();
    /**
     * <strong>21.101</strong> Forcing Signal Cooling
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   0   0   b |
     * Encoding    | r   r   r   r   r   r   r   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>7</sub> B<sub>1</sub>)
     * Range:      N = [0 .. 1]
     *                 b0 = Force Request {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.101", "dpst-21-101"})
    public static final ForcingSignalCooling FORCING_SIGNAL_COOLING = new ForcingSignalCooling();
    /**
     * <strong>21.102</strong> Room Heating Controller Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | b   b   b   b   b   b   b   b |
     * Encoding    | B   B   B   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (B<sub>8</sub>)
     * Range:      N = [0 .. 255]
     *                 b0 = Fault                              {0 = false, 1 = true}
     *                 b1 = Status 'ECO'                       {0 = false, 1 = true}
     *                 b2 = Temperature Flow Limit             {0 = false, 1 = true}
     *                 b3 = Temperature Return Limit           {0 = false, 1 = true}
     *                 b4 = Status 'Morning Boost'             {0 = false, 1 = true}
     *                 b5 = Status 'Start optimization' active {0 = false, 1 = true}
     *                 b6 = Status 'Stop optimization' active  {0 = false, 1 = true}
     *                 b7 = Summer Mode                        {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.102", "dpst-21-102"})
    public static final StatusRoomHeatingController STATUS_ROOM_HEATING_CONTROLLER = new StatusRoomHeatingController();
    /**
     * <strong>21.103</strong> Solar DHW Controller Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   b   b   b |
     * Encoding    | r   r   r   r   r   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>5</sub> B<sub>3</sub>)
     * Range:      N = [0 .. 7]
     *                 b0 = Fault                          {0 = false, 1 = true}
     *                 b1 = Status 'Solar DHW Load' active {0 = false, 1 = true}
     *                 b2 = Solar Load sufficient          {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.103", "dpst-21-103"})
    public static final StatusSolarDHWController STATUS_SOLAR_DHW_CONTROLLER = new StatusSolarDHWController();
    /**
     * <strong>21.104</strong> Fuel Type Set
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   b   b   b |
     * Encoding    | r   r   r   r   r   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>5</sub> B<sub>3</sub>)
     * Range:      N = [0 .. 7]
     *                 b0 = Oil Fuel Supported         {0 = false, 1 = true}
     *                 b1 = Gas Fuel Supported         {0 = false, 1 = true}
     *                 b2 = Solid State Fuel Supported {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.104", "dpst-21-104"})
    public static final FuelTypeSet FUEL_TYPE_SET = new FuelTypeSet();
    /**
     * <strong>21.105</strong> Room Cooling Controller Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   0   0   b |
     * Encoding    | r   r   r   r   r   r   r   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>7</sub> B<sub>1</sub>)
     * Range:      N = [0 .. 1]
     *                 b0 = Fault {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.105", "dpst-21-105"})
    public static final StatusRoomCoolingController STATUS_ROOM_COOLING_CONTROLLER = new StatusRoomCoolingController();
    /**
     * <strong>21.106</strong> Ventilation Controller Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   b   b   b   b |
     * Encoding    | r   r   r   r   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>4</sub> B<sub>4</sub>)
     * Range:      N = [0 .. 15]
     *                 b0 = Fault                  {0 = false, 1 = true}
     *                 b1 = Fan is active          {0 = false, 1 = true}
     *                 b2 = Heating mode is active {0 = false, 1 = true}
     *                 b3 = Cooling mode is active {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.106", "dpst-21-106"})
    public static final StatusVentilationController STATUS_VENTILATION_CONTROLLER = new StatusVentilationController();
    /**
     * <strong>21.601</strong> Lighting Actuator Error Information
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   b   b   b   b   b   b   b |
     * Encoding    | r   B   B   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>1</sub> B<sub>7</sub>)
     * Range:      N = [0 .. 127]
     *                 b0 = Load Detection Error {0 = false, 1 = true}
     *                 b1 = Undervoltage         {0 = false, 1 = true}
     *                 b2 = Overcurrent          {0 = false, 1 = true}
     *                 b3 = Underload            {0 = false, 1 = true}
     *                 b4 = Defective Load       {0 = false, 1 = true}
     *                 b5 = Lamp Failure         {0 = false, 1 = true}
     *                 b6 = Overheat             {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.601", "dpst-21-601"})
    public static final LightingActuatorErrorInfo LIGHTING_ACTUATOR_ERROR_INFO = new LightingActuatorErrorInfo();
    /**
     * <strong>21.1000</strong> Radio Frequency Communication Mode Info
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   b   b   b |
     * Encoding    | r   r   r   r   r   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>6</sub> B<sub>2</sub>)
     * Range:      N = [0 .. 7]
     *                 b0 = Asynchronous  {0 = false, 1 = true}
     *                 b1 = BiBat Master  {0 = false, 1 = true}
     *                 b2 = BiBat Slave   {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.1000", "dpst-21-1000"})
    public static final RadioFrequencyCommunicationModeInfo RADIO_FREQUENCY_COMMUNICATION_MODE_INFO = new RadioFrequencyCommunicationModeInfo();
    /**
     * <strong>21.1001</strong> cEMI Server Supported Filtering Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   b   b   b |
     * Encoding    | r   r   r   r   r   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>5</sub> B<sub>3</sub>)
     * Range:      N = [0 .. 7]
     *                 b0 = Filtering by Domain Address                       {0 = false, 1 = true}
     *                 b1 = Filtering by KNX Serial Number                    {0 = false, 1 = true}
     *                 b2 = Filtering by Domain Address and KNX Serial Number {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.1001", "dpst-21-1001"})
    public static final CEMIServerSupportedFilteringMode CEMI_SERVER_SUPPORTED_FILTERING_MODE = new CEMIServerSupportedFilteringMode();
    /**
     * <strong>21.1002</strong> Security Report
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   0   0   b |
     * Encoding    | r   r   r   r   r   r   r   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>7</sub> B<sub>1</sub>)
     * Range:      N = [0 .. 7]
     *                 b0 = Security Failure {0 = false, 1 = true}
     * </pre>
     */
    @DataPoint({"21.1002", "dpst-21-1002"})
    public static final SecurityReport SECURITY_REPORT = new SecurityReport();
    /**
     * <strong>21.1010</strong> Channel Activation for 8 channels
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | b   b   b   b   b   b   b   b |
     * Encoding    | B   B   B   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (B<sub>8</sub>)
     * Range:      N = [0 .. 255]
     *                 bN = Channel N+1 is active {0 = false, 1 = true}
     *                     e.g. b0 = Channel 1, b3 = Channel 4
     * </pre>
     */
    @DataPoint({"21.1010", "dpst-21-1010"})
    public static final ChannelActivation8 CHANNEL_ACTIVATION_8 = new ChannelActivation8();

    private DPT21() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Internal Data Point Type for {@link DPT21}
     *
     * @param <V>
     */
    private abstract static class InternalDataPointType<V extends DataPointValue> extends BaseDataPointType<V> {
        private InternalDataPointType(final String description) {
            super(description);
        }

        @Override
        protected final boolean isCompatible(byte[] bytes) {
            return bytes.length == 1;
        }
    }

    public static final class GeneralStatus extends InternalDataPointType<DPT21Value.GeneralStatus> {
        private GeneralStatus() {
            super("General Status");
        }

        @Override
        protected DPT21Value.GeneralStatus parse(final byte[] bytes) {
            return new DPT21Value.GeneralStatus(bytes[0]);
        }

        public DPT21Value.GeneralStatus of(final boolean outOfService, final boolean fault, final boolean overridden, final boolean inAlarm,
                                           final boolean alarmNotAcknowledged) {
            return new DPT21Value.GeneralStatus(outOfService, fault, overridden, inAlarm, alarmNotAcknowledged);
        }

        public byte[] toByteArray(final boolean outOfService, final boolean fault, final boolean overridden, final boolean inAlarm,
                                  final boolean alarmNotAcknowledged) {
            return this.of(outOfService, fault, overridden, inAlarm, alarmNotAcknowledged).toByteArray();
        }
    }

    public static final class DeviceControl extends InternalDataPointType<DPT21Value.DeviceControl> {
        private DeviceControl() {
            super("Device Control");
        }

        @Override
        protected DPT21Value.DeviceControl parse(final byte[] bytes) {
            return new DPT21Value.DeviceControl(bytes[0]);
        }

        public DPT21Value.DeviceControl of(final boolean userApplicationStopped, final boolean ownIndividualAddress,
                                           final boolean verifyModeOn) {
            return new DPT21Value.DeviceControl(userApplicationStopped, ownIndividualAddress, verifyModeOn);
        }

        public byte[] toByteArray(final boolean userApplicationStopped, final boolean ownIndividualAddress, final boolean verifyModeOn) {
            return this.of(userApplicationStopped, ownIndividualAddress, verifyModeOn).toByteArray();
        }
    }

    public static final class ForcingSignal extends InternalDataPointType<DPT21Value.ForcingSignal> {
        private ForcingSignal() {
            super("Forcing Signal");
        }

        @Override
        protected DPT21Value.ForcingSignal parse(final byte[] bytes) {
            return new DPT21Value.ForcingSignal(bytes[0]);
        }

        public DPT21Value.ForcingSignal of(final boolean forceRequest, final boolean protection, final boolean oversupply, final boolean overrun,
                                           final boolean dhwNormal, final boolean dhwLegionellaProtection, final boolean roomHeatingComfort,
                                           final boolean roomHeatingMaxFlowTemperature) {
            return new DPT21Value.ForcingSignal(forceRequest, protection, oversupply, overrun, dhwNormal, dhwLegionellaProtection, roomHeatingComfort,
                    roomHeatingMaxFlowTemperature);
        }

        public byte[] toByteArray(final boolean forceRequest, final boolean protection, final boolean oversupply, final boolean overrun,
                                  final boolean dhwNormal, final boolean dhwLegionellaProtection, final boolean roomHeatingComfort,
                                  final boolean roomHeatingMaxFlowTemperature) {
            return this.of(forceRequest, protection, oversupply, overrun, dhwNormal, dhwLegionellaProtection, roomHeatingComfort,
                    roomHeatingMaxFlowTemperature).toByteArray();
        }
    }

    public static final class ForcingSignalCooling extends InternalDataPointType<DPT21Value.ForcingSignalCooling> {
        private ForcingSignalCooling() {
            super("Forcing Signal Cooling");
        }

        @Override
        protected DPT21Value.ForcingSignalCooling parse(final byte[] bytes) {
            return new DPT21Value.ForcingSignalCooling(bytes[0]);
        }

        public DPT21Value.ForcingSignalCooling of(final boolean forceRequest) {
            return new DPT21Value.ForcingSignalCooling(forceRequest);
        }

        public byte[] toByteArray(final boolean forceRequest) {
            return this.of(forceRequest).toByteArray();
        }
    }

    public static final class StatusRoomHeatingController extends InternalDataPointType<DPT21Value.StatusRoomHeatingController> {
        private StatusRoomHeatingController() {
            super("Room Heating Controller Status");
        }

        @Override
        protected DPT21Value.StatusRoomHeatingController parse(final byte[] bytes) {
            return new DPT21Value.StatusRoomHeatingController(bytes[0]);
        }

        public DPT21Value.StatusRoomHeatingController of(final boolean fault, final boolean statusEco, final boolean temperatureFlowLimit,
                                                         final boolean temperatureReturnLimit, final boolean statusMorningBoost, final boolean statusStartOptimizationActive,
                                                         final boolean statusStopOptimizationActive, final boolean summerMode) {
            return new DPT21Value.StatusRoomHeatingController(fault, statusEco, temperatureFlowLimit, temperatureReturnLimit, statusMorningBoost,
                    statusStartOptimizationActive, statusStopOptimizationActive, summerMode);
        }

        public byte[] toByteArray(final boolean fault, final boolean statusEco, final boolean temperatureFlowLimit,
                                  final boolean temperatureReturnLimit, final boolean statusMorningBoost, final boolean statusStartOptimizationActive,
                                  final boolean statusStopOptimizationActive, final boolean summerMode) {
            return this.of(fault, statusEco, temperatureFlowLimit, temperatureReturnLimit, statusMorningBoost, statusStartOptimizationActive,
                    statusStopOptimizationActive, summerMode).toByteArray();
        }
    }

    public static final class StatusSolarDHWController extends InternalDataPointType<DPT21Value.StatusSolarDHWController> {
        private StatusSolarDHWController() {
            super("Solar DHW Controller Status");
        }

        @Override
        protected DPT21Value.StatusSolarDHWController parse(final byte[] bytes) {
            return new DPT21Value.StatusSolarDHWController(bytes[0]);
        }

        public DPT21Value.StatusSolarDHWController of(final boolean fault, final boolean statusDHWLoadActive,
                                                      final boolean solarLoadSufficient) {
            return new DPT21Value.StatusSolarDHWController(fault, statusDHWLoadActive, solarLoadSufficient);
        }

        public byte[] toByteArray(final boolean fault, final boolean statusDHWLoadActive, final boolean solarLoadSufficient) {
            return this.of(fault, statusDHWLoadActive, solarLoadSufficient).toByteArray();
        }
    }

    public static final class FuelTypeSet extends InternalDataPointType<DPT21Value.FuelTypeSet> {
        private FuelTypeSet() {
            super("Fuel Type Set");
        }

        @Override
        protected DPT21Value.FuelTypeSet parse(final byte[] bytes) {
            return new DPT21Value.FuelTypeSet(bytes[0]);
        }

        public DPT21Value.FuelTypeSet of(final boolean oilFuelSupported, final boolean gasFuelSupported, final boolean solidStateFuelSupported) {
            return new DPT21Value.FuelTypeSet(oilFuelSupported, gasFuelSupported, solidStateFuelSupported);
        }

        public byte[] toByteArray(final boolean oilFuelSupported, final boolean gasFuelSupported, final boolean solidStateFuelSupported) {
            return this.of(oilFuelSupported, gasFuelSupported, solidStateFuelSupported).toByteArray();
        }
    }

    public static final class StatusRoomCoolingController extends InternalDataPointType<DPT21Value.StatusRoomCoolingController> {
        private StatusRoomCoolingController() {
            super("Room Cooling Controller Status");
        }

        @Override
        protected DPT21Value.StatusRoomCoolingController parse(final byte[] bytes) {
            return new DPT21Value.StatusRoomCoolingController(bytes[0]);
        }

        public DPT21Value.StatusRoomCoolingController of(final boolean fault) {
            return new DPT21Value.StatusRoomCoolingController(fault);
        }

        public byte[] toByteArray(final boolean fault) {
            return this.of(fault).toByteArray();
        }
    }

    public static final class StatusVentilationController extends InternalDataPointType<DPT21Value.StatusVentilationController> {
        private StatusVentilationController() {
            super("Ventilation Controller Status");
        }

        @Override
        protected DPT21Value.StatusVentilationController parse(final byte[] bytes) {
            return new DPT21Value.StatusVentilationController(bytes[0]);
        }

        public DPT21Value.StatusVentilationController of(final boolean fault, final boolean fanActive, final boolean heatingModeActive,
                                                         final boolean coolingModeActive) {
            return new DPT21Value.StatusVentilationController(fault, fanActive, heatingModeActive, coolingModeActive);
        }

        public byte[] toByteArray(final boolean fault, final boolean fanActive, final boolean heatingModeActive, final boolean coolingModeActive) {
            return this.of(fault, fanActive, heatingModeActive, coolingModeActive).toByteArray();
        }
    }

    public static final class LightingActuatorErrorInfo extends InternalDataPointType<DPT21Value.LightingActuatorErrorInfo> {
        private LightingActuatorErrorInfo() {
            super("Lighting Actuator Error Information");
        }

        @Override
        protected DPT21Value.LightingActuatorErrorInfo parse(final byte[] bytes) {
            return new DPT21Value.LightingActuatorErrorInfo(bytes[0]);
        }

        public DPT21Value.LightingActuatorErrorInfo of(final boolean errorLoad, final boolean undervoltage, final boolean overcurrent,
                                                       final boolean underload, final boolean defectiveLoad, final boolean lampFailure, final boolean overheat) {
            return new DPT21Value.LightingActuatorErrorInfo(errorLoad, undervoltage, overcurrent, underload, defectiveLoad, lampFailure, overheat);
        }

        public byte[] toByteArray(final boolean errorLoad, final boolean undervoltage, final boolean overcurrent, final boolean underload,
                                  final boolean defectiveLoad, final boolean lampFailure, final boolean overheat) {
            return this.of(errorLoad, undervoltage, overcurrent, underload, defectiveLoad, lampFailure, overheat).toByteArray();
        }
    }

    public static final class RadioFrequencyCommunicationModeInfo extends InternalDataPointType<DPT21Value.RadioFrequencyCommunicationModeInfo> {
        private RadioFrequencyCommunicationModeInfo() {
            super("Radio Frequency Communication Mode Info");
        }

        @Override
        protected DPT21Value.RadioFrequencyCommunicationModeInfo parse(final byte[] bytes) {
            return new DPT21Value.RadioFrequencyCommunicationModeInfo(bytes[0]);
        }

        public DPT21Value.RadioFrequencyCommunicationModeInfo of(final boolean asynchronous, final boolean master, final boolean slave) {
            return new DPT21Value.RadioFrequencyCommunicationModeInfo(asynchronous, master, slave);
        }

        public byte[] toByteArray(final boolean asynchronous, final boolean master, final boolean slave) {
            return this.of(asynchronous, master, slave).toByteArray();
        }
    }

    public static final class CEMIServerSupportedFilteringMode extends BaseDataPointType<DPT21Value.CEMIServerSupportedFilteringMode> {
        private CEMIServerSupportedFilteringMode() {
            super("cEMI Server Supported Filtering Mode");
        }

        @Override
        protected boolean isCompatible(final byte[] bytes) {
            return bytes.length == 1;
        }

        @Override
        protected DPT21Value.CEMIServerSupportedFilteringMode parse(final byte[] bytes) {
            return new DPT21Value.CEMIServerSupportedFilteringMode(bytes[0]);
        }

        public DPT21Value.CEMIServerSupportedFilteringMode of(final boolean domainAddress, final boolean serialNumber,
                                                              final boolean domainAddressAndSerialNumber) {
            return new DPT21Value.CEMIServerSupportedFilteringMode(domainAddress, serialNumber, domainAddressAndSerialNumber);
        }

        public byte[] toByteArray(final boolean domainAddress, final boolean serialNumber, final boolean domainAddressAndSerialNumber) {
            return this.of(domainAddress, serialNumber, domainAddressAndSerialNumber).toByteArray();
        }
    }

    public static final class SecurityReport extends InternalDataPointType<DPT21Value.SecurityReport> {
        private SecurityReport() {
            super("Security Report");
        }

        @Override
        protected DPT21Value.SecurityReport parse(final byte[] bytes) {
            return new DPT21Value.SecurityReport(bytes[0]);
        }

        public DPT21Value.SecurityReport of(final boolean failure) {
            return new DPT21Value.SecurityReport(failure);
        }

        public byte[] toByteArray(final boolean failure) {
            return this.of(failure).toByteArray();
        }
    }

    public static final class ChannelActivation8 extends InternalDataPointType<DPT21Value.ChannelActivation8> {
        private ChannelActivation8() {
            super("Channel Activation for 8 channels");
        }

        @Override
        protected DPT21Value.ChannelActivation8 parse(final byte[] bytes) {
            return new DPT21Value.ChannelActivation8(bytes[0]);
        }

        public DPT21Value.ChannelActivation8 of(final boolean channel1, final boolean channel2, final boolean channel3, final boolean channel4,
                                                final boolean channel5, final boolean channel6, final boolean channel7, final boolean channel8) {
            return new DPT21Value.ChannelActivation8(channel1, channel2, channel3, channel4, channel5, channel6, channel7, channel8);
        }

        public byte[] toByteArray(final boolean channel1, final boolean channel2, final boolean channel3, final boolean channel4,
                                  final boolean channel5, final boolean channel6, final boolean channel7, final boolean channel8) {
            return this.of(channel1, channel2, channel3, channel4, channel5, channel6, channel7, channel8).toByteArray();
        }
    }
}
