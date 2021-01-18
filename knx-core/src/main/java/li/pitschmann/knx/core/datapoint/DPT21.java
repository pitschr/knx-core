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
    public static final RoomHeatingControllerStatus ROOM_HEATING_CONTROLLER_STATUS = new RoomHeatingControllerStatus();

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
    public static final SolarDHWControllerStatus SOLAR_DHW_CONTROLLER_STATUS = new SolarDHWControllerStatus();

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
    public static final RoomCoolingControllerStatus ROOM_COOLING_CONTROLLER_STATUS = new RoomCoolingControllerStatus();

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
    public static final VentilationControllerStatus VENTILATION_CONTROLLER_STATUS = new VentilationControllerStatus();

    // TODO: ETS 21.107 -> Combined Status RTSM?

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
     * Range:      N = [0 .. 7]
     *                 bN = Channel N+1 is active {0 = false, 1 = true}
     *                     e.g. b0 = Channel 1, b3 = Channel 4
     * </pre>
     */
    @DataPoint({"21.1010", "dpst-21-1010"})
    public static final ChannelActivation8 CHANNEL_ACTIVATION_8 = new ChannelActivation8();

    /**
     * <strong>21.1200</strong> Virtual Contact Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | b   b   b   b   b   b   b   b |
     * Encoding    | B   B   B   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (B<sub>8</sub>)
     * Range:      N = [0 .. 7]
     *                 0 = Virtual Contact Open
     *                 1 = Virtual Contact Closed
     * </pre>
     */
    @DataPoint({"21.1200", "dpst-21-1200"})
    public static final VirtualContactStatus VIRTUAL_CONTACT_STATUS = new VirtualContactStatus();

    /**
     * <strong>21.1201</strong> Phase Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | b   b   b   b   b   b   b   b |
     * Encoding    | r   r   r   r   r   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bits (r<sub>5</sub> B<sub>3</sub>)
     * Range:      N = [0 .. 3]
     *                 0 = Phase absent
     *                 1 = Phase present
     *                 bN = Phase N+1 {0 = absent, 1 = present}
     *                     e.g. b0 = Phase 1, b1 = Phase 2, b2 = Phase 3

     * </pre>
     */
    @DataPoint({"21.1201", "dpst-21-1201"})
    public static final PhaseStatus PHASE_STATUS = new PhaseStatus();


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
    }

    public static final class RoomHeatingControllerStatus extends InternalDataPointType<DPT21Value.RoomHeatingControllerStatus> {
        private RoomHeatingControllerStatus() {
            super("Room Heating Controller Status");
        }

        @Override
        protected DPT21Value.RoomHeatingControllerStatus parse(final byte[] bytes) {
            return new DPT21Value.RoomHeatingControllerStatus(bytes[0]);
        }

        public DPT21Value.RoomHeatingControllerStatus of(final boolean fault, final boolean statusEco, final boolean temperatureFlowLimit,
                                                         final boolean temperatureReturnLimit, final boolean statusMorningBoost, final boolean statusStartOptimizationActive,
                                                         final boolean statusStopOptimizationActive, final boolean summerMode) {
            return new DPT21Value.RoomHeatingControllerStatus(fault, statusEco, temperatureFlowLimit, temperatureReturnLimit, statusMorningBoost,
                    statusStartOptimizationActive, statusStopOptimizationActive, summerMode);
        }
    }

    public static final class SolarDHWControllerStatus extends InternalDataPointType<DPT21Value.SolarDHWControllerStatus> {
        private SolarDHWControllerStatus() {
            super("Solar DHW Controller Status");
        }

        @Override
        protected DPT21Value.SolarDHWControllerStatus parse(final byte[] bytes) {
            return new DPT21Value.SolarDHWControllerStatus(bytes[0]);
        }

        public DPT21Value.SolarDHWControllerStatus of(final boolean fault, final boolean statusDHWLoadActive,
                                                      final boolean solarLoadSufficient) {
            return new DPT21Value.SolarDHWControllerStatus(fault, statusDHWLoadActive, solarLoadSufficient);
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
    }

    public static final class RoomCoolingControllerStatus extends InternalDataPointType<DPT21Value.RoomCoolingControllerStatus> {
        private RoomCoolingControllerStatus() {
            super("Room Cooling Controller Status");
        }

        @Override
        protected DPT21Value.RoomCoolingControllerStatus parse(final byte[] bytes) {
            return new DPT21Value.RoomCoolingControllerStatus(bytes[0]);
        }

        public DPT21Value.RoomCoolingControllerStatus of(final boolean fault) {
            return new DPT21Value.RoomCoolingControllerStatus(fault);
        }
    }

    public static final class VentilationControllerStatus extends InternalDataPointType<DPT21Value.VentilationControllerStatus> {
        private VentilationControllerStatus() {
            super("Ventilation Controller Status");
        }

        @Override
        protected DPT21Value.VentilationControllerStatus parse(final byte[] bytes) {
            return new DPT21Value.VentilationControllerStatus(bytes[0]);
        }

        public DPT21Value.VentilationControllerStatus of(final boolean fault, final boolean fanActive, final boolean heatingModeActive,
                                                         final boolean coolingModeActive) {
            return new DPT21Value.VentilationControllerStatus(fault, fanActive, heatingModeActive, coolingModeActive);
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
    }

    public static final class VirtualContactStatus extends InternalDataPointType<DPT21Value.VirtualContactStatus> {
        private VirtualContactStatus() {
            super("Virtual Contact Status");
        }

        @Override
        protected DPT21Value.VirtualContactStatus parse(final byte[] bytes) {
            return new DPT21Value.VirtualContactStatus(bytes[0]);
        }

        public DPT21Value.VirtualContactStatus of(final boolean contact1, final boolean contact2, final boolean contact3, final boolean contact4,
                                                  final boolean contact5, final boolean contact6, final boolean contact7, final boolean contact8) {
            return new DPT21Value.VirtualContactStatus(contact1, contact2, contact3, contact4, contact5, contact6, contact7, contact8);
        }
    }

    public static final class PhaseStatus extends InternalDataPointType<DPT21Value.PhaseStatus> {
        private PhaseStatus() {
            super("Phase Status");
        }

        @Override
        protected DPT21Value.PhaseStatus parse(final byte[] bytes) {
            return new DPT21Value.PhaseStatus(bytes[0]);
        }

        public DPT21Value.PhaseStatus of(final boolean phase1, final boolean phase2, final boolean phase3) {
            return new DPT21Value.PhaseStatus(phase1, phase2, phase3);
        }
    }
}
