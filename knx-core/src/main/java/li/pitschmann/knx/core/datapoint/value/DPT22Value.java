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

import li.pitschmann.knx.core.datapoint.DPT22;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;

/**
 * Data Point Value for {@link DPT22} (22.xxx)
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
public final class DPT22Value {
    private DPT22Value() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * <strong>22.100</strong> DHW Controller Status
     *
     * @see DPT22#STATUS_DHW_CONTROLLER
     */
    public static final class StatusDHWController extends AbstractDataPointFlags<DPT22.StatusDHWController> {
        public StatusDHWController(final byte[] bytes) {
            super(DPT22.STATUS_DHW_CONTROLLER, bytes);
        }

        public StatusDHWController(final boolean fault,
                                   final boolean loadActive,
                                   final boolean legionellaProtectionActive,
                                   final boolean pushActive,
                                   final boolean otherEnergySourceActive,
                                   final boolean solarEnergyOnly,
                                   final boolean solarEnergySupport,
                                   final boolean temperatureSetpointInfluenced) {
            this(new byte[]{0, Bytes.toByte(temperatureSetpointInfluenced, solarEnergySupport, solarEnergyOnly, otherEnergySourceActive, pushActive,
                    legionellaProtectionActive, loadActive, fault)});

        }

        public boolean isFault() {
            return this.isSet(0);
        }

        public boolean isLoadActive() {
            return this.isSet(1);
        }

        public boolean isLegionallaProtectionActive() {
            return this.isSet(2);
        }

        public boolean isPushActive() {
            return this.isSet(3);
        }

        public boolean isOtherEnergySourceActive() {
            return this.isSet(4);
        }

        public boolean isSolarEnergyOnly() {
            return this.isSet(5);
        }

        public boolean isSolarEnergySupport() {
            return this.isSet(6);
        }

        public boolean isTemperatureSetpointInfluenced() {
            return this.isSet(7);
        }
    }

    /**
     * <strong>22.101</strong> Room Heating / Cooling Controller Status
     *
     * @see DPT22#STATUS_ROOM_HEATING_COOLING_CONTROLLER
     */
    public static final class StatusRoomHeatingCoolingController extends AbstractDataPointFlags<DPT22.StatusRoomHeatinCoolingController> {
        public StatusRoomHeatingCoolingController(final byte[] bytes) {
            super(DPT22.STATUS_ROOM_HEATING_COOLING_CONTROLLER, bytes);
        }

        public StatusRoomHeatingCoolingController(final boolean fault,
                                                  final boolean statusEcoHeating,
                                                  final boolean temperatureFlowLimit,
                                                  final boolean temperatureReturnLimit,
                                                  final boolean statusMorningBoost,
                                                  final boolean startOptimizationActive,
                                                  final boolean stopOptimizationActive,
                                                  final boolean heatingDisabled,
                                                  final boolean heatingMode,
                                                  final boolean statusEcoCooling,
                                                  final boolean statusPreCooling,
                                                  final boolean coolingDisabled,
                                                  final boolean dewPointAlarm,
                                                  final boolean frostAlarm,
                                                  final boolean overheatAlarm) {
            this(Bytes.toByteArray(false, overheatAlarm, frostAlarm, dewPointAlarm, coolingDisabled, statusPreCooling, statusEcoCooling, heatingMode,
                    heatingDisabled, stopOptimizationActive, startOptimizationActive, statusMorningBoost, temperatureReturnLimit,
                    temperatureFlowLimit, statusEcoHeating, fault));
        }

        public boolean isFault() {
            return this.isSet(0);
        }

        public boolean isStatusEcoHeating() {
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

        public boolean isStartOptimizationActive() {
            return this.isSet(5);
        }

        public boolean isStopOptimizationActive() {
            return this.isSet(6);
        }

        public boolean isHeatingDisabled() {
            return this.isSet(7);
        }

        public boolean isHeatingMode() {
            return this.isSet(8);
        }

        public boolean isStatusEcoCooling() {
            return this.isSet(9);
        }

        public boolean isStatusPreCooling() {
            return this.isSet(10);
        }

        public boolean isCoolingDisabled() {
            return this.isSet(11);
        }

        public boolean isDewPointAlarm() {
            return this.isSet(12);
        }

        public boolean isFrostAlarm() {
            return this.isSet(13);
        }

        public boolean isOverheatAlarm() {
            return this.isSet(14);
        }

    }

    /**
     * <strong>22.1000</strong> Media
     *
     * @see DPT22#MEDIA
     */
    public static final class Media extends AbstractDataPointFlags<DPT22.Media> {
        public Media(final byte[] bytes) {
            super(DPT22.MEDIA, bytes);
        }

        public Media(final boolean tp1Supported,
                     final boolean pl110Supported,
                     final boolean rfSupported,
                     final boolean knxipSupported) {
            this(new byte[]{0, Bytes.toByte(false, false, knxipSupported, rfSupported, false, pl110Supported, tp1Supported, false)});
        }

        public boolean isTP1Supported() {
            return this.isSet(1);
        }

        public boolean isPL110Supported() {
            return this.isSet(2);
        }

        public boolean isRFSupported() {
            return this.isSet(4);
        }

        public boolean isKNXIPSupported() {
            return this.isSet(5);
        }
    }

    /**
     * <strong>22.1010</strong> Channel Activation for 16 channels
     *
     * @see DPT22#CHANNEL_ACTIVATION_16
     */
    public static final class ChannelActivation16 extends AbstractDataPointFlags<DPT22.ChannelActivation16> {
        public ChannelActivation16(final byte[] bytes) {
            super(DPT22.CHANNEL_ACTIVATION_16, bytes);
        }

        public ChannelActivation16(final boolean channel1, final boolean channel2, final boolean channel3, final boolean channel4,
                                   final boolean channel5, final boolean channel6, final boolean channel7, final boolean channel8,
                                   final boolean channel9, final boolean channel10, final boolean channel11, final boolean channel12,
                                   final boolean channel13, final boolean channel14, final boolean channel15, final boolean channel16) {
            this(Bytes.toByteArray(channel16, channel15, channel14, channel13, channel12, channel11, channel10, channel9, channel8, channel7,
                    channel6, channel5, channel4, channel3, channel2, channel1));
        }

        public boolean isChannelActive(final int channel) {
            Preconditions.checkArgument(channel >= 1 && channel <= 16,
                    "Channel must be between 1 and 16 (actual: {})", channel);
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

        @Override
        public String toText() {
            // return list of channels (e.g. "channel 1" if only channel is
            final var sb = new StringBuilder(25);
            for (var i = 0; i < 16; i++) {
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
