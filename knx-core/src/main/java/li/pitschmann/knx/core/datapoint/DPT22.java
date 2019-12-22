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

/**
 * Data Point Type 22 for 16-Bits flagged messages
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names |b15  b   b   b   b   b   b   b |
 * Encoding    | B   B   B   B   B   B   B   B |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | b   b   b   b   b   b   b  b0 |
 *             | B   B   B   B   B   B   B   B |
 *             +---+---+---+---+---+---+---+---+
 * Format:     16 bits (B<sub>16</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT22 {
    /**
     * <strong>22.100</strong> DHW Controller Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   0   0   0 |
     * Encoding    | r   r   r   r   r   r   r   r |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | b   b   b   b   b   b   b   b |
     *             | B   B   B   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     16 bits (r<sub>8</sub> B<sub>8</sub>)
     * Range:      N = [0 .. 63]
     *                 b0 = Fault: DHW Controller has failure  {0 = false, 1 = true}
     *                 b1 = DHW Load is active                 {0 = false, 1 = true}
     *                 b2 = Legionella Protection is active    {0 = false, 1 = true}
     *                 b3 = DHW Push is active                 {0 = false, 1 = true}
     *                 b4 = Other Energy Source is active      {0 = false, 1 = true}
     *                 b5 = Solar Energy only                  {0 = false, 1 = true}
     *                 b6 = Solar Energy support               {0 = false, 1 = true}
     *                 b7 = Temperature Setpoint is influenced {0 = false, 1 = true}
     * </pre>
     */
    @KnxDataPointType({"22.100", "dpst-22-100"})
    public static final StatusDHWController STATUS_DHW_CONTROLLER = new StatusDHWController();
    /**
     * <strong>22.101</strong> Room Heating / Cooling Controller Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   b   b   b   b   b   b   b |
     * Encoding    | r   B   B   B   B   B   B   B |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | b   b   b   b   b   b   b   b |
     *             | B   B   B   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     16 bits (r<sub>1</sub> B<sub>15</sub>)
     * Range:      N = [0 .. 32767]
     *                 b0 = Fault                              {0 = false, 1 = true}
     *                 b1 = Status 'ECO' Heating               {0 = false, 1 = true}
     *                 b2 = Temperature Flow Limit             {0 = false, 1 = true}
     *                 b3 = Temperature Return Limit           {0 = false, 1 = true}
     *                 b4 = Status 'Morning Boost'             {0 = false, 1 = true}
     *                 b5 = Status 'Start optimization' active {0 = false, 1 = true}
     *                 b6 = Status 'Stop optimization' active  {0 = false, 1 = true}
     *                 b7 = Heating Disabled                   {0 = false, 1 = true}
     *                 b8 = Heat / Cool Mode                   {0 = cooling, 1 = heating}
     *                 b9 = Status 'ECO' Cooling               {0 = false, 1 = true}
     *                 b10 = Status 'Pre Cooling'              {0 = false, 1 = true}
     *                 b11 = Cooling disabled                  {0 = false, 1 = true}
     *                 b12 = Dew Point Alarm                   {0 = no alarm, 1 = alarm}
     *                 b13 = Frost Alarm                       {0 = no alarm, 1 = alarm}
     *                 b14 = Overheat Alarm                    {0 = no alarm, 1 = alarm}
     * </pre>
     */
    @KnxDataPointType({"22.101", "dpst-22-101"})
    public static final StatusRoomHeatinCoolingController STATUS_ROOM_HEATING_COOLING_CONTROLLER = new StatusRoomHeatinCoolingController();
    /**
     * <strong>22.1000</strong> Media
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   0   0   0   0   0   0   0 |
     * Encoding    | r   r   r   r   r   r   r   r |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | 0   0   b   b   0   b   b   0 |
     *             | r   r   B   B   r   B   B   r |
     *             +---+---+---+---+---+---+---+---+
     * Format:     16 bits (r<sub>10</sub> B<sub>2</sub> r<sub>1</sub> B<sub>2</sub> r<sub>1</sub>)
     * Range:      N = [0 .. 63]
     *                 b1 = TP1 supported    {0 = false, 1 = true}
     *                 b2 = PL110 supported  {0 = false, 1 = true}
     *                 b4 = RF supported     {0 = false, 1 = true}
     *                 b5 = KNX/IP supported {0 = false, 1 = true}
     * </pre>
     */
    @KnxDataPointType({"22.1000", "dpst-22-1000"})
    public static final Media MEDIA = new Media();
    /**
     * <strong>22.1010</strong> Channel Activation for 16 channels
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | b   b   b   b   b   b   b   b |
     * Encoding    | B   B   B   B   B   B   B   B |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | b   b   b   b   b   b   b   b |
     *             | B   B   B   B   B   B   B   B |
     *             +---+---+---+---+---+---+---+---+
     * Format:     16 bits (B<sub>16</sub>)
     * Range:      N = [0 .. 65535]
     *                 bN = Channel N+1 is active {0 = false, 1 = true}
     *                     e.g. b0 = Channel 1, b10 = Channel 11
     * </pre>
     */
    @KnxDataPointType({"22.1010", "dpst-22-1010"})
    public static final ChannelActivation16 CHANNEL_ACTIVATION_16 = new ChannelActivation16();

    private DPT22() {
        throw new AssertionError("Do not touch me!");
    }

    public static final class StatusDHWController extends AbstractDataPointType<DPT22Value.StatusDHWController> {
        private StatusDHWController() {
            super("22.100", "DHW Controller Status");
        }

        @Override
        protected boolean isCompatible(final byte[] bytes) {
            return bytes.length == 2;
        }

        @Override
        protected DPT22Value.StatusDHWController parse(final byte[] bytes) {
            return new DPT22Value.StatusDHWController(bytes);
        }

        public DPT22Value.StatusDHWController toValue(final boolean fault, final boolean loadActive, final boolean legionellaProtectionActive,
                                                      final boolean pushActive, final boolean otherEnergySourceActive, final boolean solarEnergyOnly,
                                                      final boolean solarEnergySupport, final boolean temperatureSetpointInfluenced) {
            return new DPT22Value.StatusDHWController(fault, loadActive, legionellaProtectionActive, pushActive, otherEnergySourceActive,
                    solarEnergyOnly, solarEnergySupport, temperatureSetpointInfluenced);
        }

        public byte[] toByteArray(final boolean fault, final boolean loadActive, final boolean legionellaProtectionActive, final boolean pushActive,
                                  final boolean otherEnergySourceActive, final boolean solarEnergyOnly, final boolean solarEnergySupport,
                                  final boolean temperatureSetpointInfluenced) {
            return this.toValue(fault, loadActive, legionellaProtectionActive, pushActive, otherEnergySourceActive, solarEnergyOnly,
                    solarEnergySupport, temperatureSetpointInfluenced).toByteArray();
        }
    }

    public static final class StatusRoomHeatinCoolingController extends AbstractDataPointType<DPT22Value.StatusRoomHeatingCoolingController> {
        private StatusRoomHeatinCoolingController() {
            super("22.101", "Room Heating / Cooling Controller Status");
        }

        @Override
        protected boolean isCompatible(final byte[] bytes) {
            return bytes.length == 2;
        }

        @Override
        protected DPT22Value.StatusRoomHeatingCoolingController parse(final byte[] bytes) {
            return new DPT22Value.StatusRoomHeatingCoolingController(bytes);
        }

        public DPT22Value.StatusRoomHeatingCoolingController toValue(final boolean fault, final boolean statusEcoHeating,
                                                                     final boolean temperatureFlowLimit, final boolean temperatureReturnLimit, final boolean statusMorningBoost,
                                                                     final boolean startOptimizationActive, final boolean stopOptimizationActive, final boolean heatingDisabled,
                                                                     final boolean heatingMode, final boolean statusEcoCooling, final boolean statusPreCooling, final boolean coolingDisabled,
                                                                     final boolean dewPointAlarm, final boolean frostAlarm, final boolean overheatAlarm) {
            return new DPT22Value.StatusRoomHeatingCoolingController(fault, statusEcoHeating, temperatureFlowLimit, temperatureReturnLimit,
                    statusMorningBoost, startOptimizationActive, stopOptimizationActive, heatingDisabled, heatingMode, statusEcoCooling,
                    statusPreCooling, coolingDisabled, dewPointAlarm, frostAlarm, overheatAlarm);
        }

        public byte[] toByteArray(final boolean fault, final boolean statusEcoHeating, final boolean temperatureFlowLimit,
                                  final boolean temperatureReturnLimit, final boolean statusMorningBoost, final boolean startOptimizationActive,
                                  final boolean stopOptimizationActive, final boolean heatingDisabled, final boolean heatingMode,
                                  final boolean statusEcoCooling, final boolean statusPreCooling, final boolean coolingDisabled, final boolean dewPointAlarm,
                                  final boolean frostAlarm, final boolean overheatAlarm) {
            return this.toValue(fault, statusEcoHeating, temperatureFlowLimit, temperatureReturnLimit, statusMorningBoost, startOptimizationActive,
                    stopOptimizationActive, heatingDisabled, heatingMode, statusEcoCooling, statusPreCooling, coolingDisabled, dewPointAlarm,
                    frostAlarm, overheatAlarm).toByteArray();
        }
    }

    public static final class Media extends AbstractDataPointType<DPT22Value.Media> {
        private Media() {
            super("22.1000", "Media");
        }

        @Override
        protected boolean isCompatible(final byte[] bytes) {
            return bytes.length == 2;
        }

        @Override
        protected DPT22Value.Media parse(final byte[] bytes) {
            return new DPT22Value.Media(bytes);
        }

        public DPT22Value.Media toValue(final boolean tp1Supported, final boolean pl110Supported, final boolean rfSupported,
                                        final boolean knxipSupported) {
            return new DPT22Value.Media(tp1Supported, pl110Supported, rfSupported, knxipSupported);
        }

        public byte[] toByteArray(final boolean tp1Supported, final boolean pl110Supported, final boolean rfSupported, final boolean knxipSupported) {
            return this.toValue(tp1Supported, pl110Supported, rfSupported, knxipSupported).toByteArray();
        }
    }

    public static final class ChannelActivation16 extends AbstractDataPointType<DPT22Value.ChannelActivation16> {
        private ChannelActivation16() {
            super("22.1010", "Channel Activation for 16 channels");
        }

        @Override
        protected boolean isCompatible(final byte[] bytes) {
            return bytes.length == 2;
        }

        @Override
        protected DPT22Value.ChannelActivation16 parse(final byte[] bytes) {
            return new DPT22Value.ChannelActivation16(bytes);
        }

        public DPT22Value.ChannelActivation16 toValue(final boolean channel1, final boolean channel2, final boolean channel3, final boolean channel4,
                                                      final boolean channel5, final boolean channel6, final boolean channel7, final boolean channel8, final boolean channel9,
                                                      final boolean channel10, final boolean channel11, final boolean channel12, final boolean channel13, final boolean channel14,
                                                      final boolean channel15, final boolean channel16) {
            return new DPT22Value.ChannelActivation16(channel1, channel2, channel3, channel4, channel5, channel6, channel7, channel8, channel9,
                    channel10, channel11, channel12, channel13, channel14, channel15, channel16);
        }

        public byte[] toByteArray(final boolean channel1, final boolean channel2, final boolean channel3, final boolean channel4,
                                  final boolean channel5, final boolean channel6, final boolean channel7, final boolean channel8, final boolean channel9,
                                  final boolean channel10, final boolean channel11, final boolean channel12, final boolean channel13, final boolean channel14,
                                  final boolean channel15, final boolean channel16) {
            return this.toValue(channel1, channel2, channel3, channel4, channel5, channel6, channel7, channel8, channel9, channel10, channel11,
                    channel12, channel13, channel14, channel15, channel16).toByteArray();
        }
    }
}
