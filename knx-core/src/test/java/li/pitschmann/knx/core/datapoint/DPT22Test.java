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
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
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
public class DPT22Test implements DPTTest {

    @Override
    @Test
    public void testIdAndDescription() {
        assertThat(DPT22.CHANNEL_ACTIVATION_16.getId()).isEqualTo("22.1010");
        assertThat(DPT22.CHANNEL_ACTIVATION_16.getDescription()).isEqualTo("Channel Activation for 16 channels");

        assertThat(DPT22.MEDIA.getId()).isEqualTo("22.1000");
        assertThat(DPT22.MEDIA.getDescription()).isEqualTo("Media");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT22.CHANNEL_ACTIVATION_16;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[0])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[3])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue("0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue("0x00", "0x00", "0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // OK
        assertThat(dpt.toValue((byte) 0x00, (byte) 0x00)).isInstanceOf(DPT22Value.ChannelActivation16.class);
        assertThat(dpt.toValue((byte) 0xFF, (byte) 0xFF)).isInstanceOf(DPT22Value.ChannelActivation16.class);
        assertThat(dpt.toValue("0x00", "0x00")).isInstanceOf(DPT22Value.ChannelActivation16.class);
        assertThat(dpt.toValue("0xFF", "0xFF")).isInstanceOf(DPT22Value.ChannelActivation16.class);
    }

    @Override
    @Test
    public void testOf() {
        // DHW Controller Status (1010 1001 = 0xA9)
        DPT22.StatusDHWController statusDHWControllerDPT = DPT22.STATUS_DHW_CONTROLLER;
        DPT22Value.StatusDHWController statusDHWControllerDPV = statusDHWControllerDPT.toValue(true, false, false, true, false, true, false, true);
        assertThat(statusDHWControllerDPT.toValue((byte) 0x00, (byte) 0xA9)).isEqualTo(statusDHWControllerDPV);
        assertThat(statusDHWControllerDPT.toValue(true, false, false, true, false, true, false, true)).isEqualTo(statusDHWControllerDPV);
        assertThat(statusDHWControllerDPT.toByteArray(true, false, false, true, false, true, false, true)).containsExactly(0x00, 0xA9);
        assertThat(statusDHWControllerDPV.toByteArray()).containsExactly(0x00, 0xA9);
        assertThatThrownBy(() -> statusDHWControllerDPT.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Room Heating / Cooling Controller Status (0100 1001 1011 0011 = 0x49 0xB3)
        DPT22.StatusRoomHeatinCoolingController statusRoomHeatingCoolingControllerDPT = DPT22.STATUS_ROOM_HEATING_COOLING_CONTROLLER;
        DPT22Value.StatusRoomHeatingCoolingController statusRoomHeatingCoolingControllerDPV = statusRoomHeatingCoolingControllerDPT.toValue(true, true,
                false, false, true, true, false, true, true, false, false, true, false, false, true);
        assertThat(statusRoomHeatingCoolingControllerDPT.toValue((byte) 0x49, (byte) 0xB3)).isEqualTo(statusRoomHeatingCoolingControllerDPV);
        assertThat(statusRoomHeatingCoolingControllerDPT.toValue(true, true, false, false, true, true, false, true, true, false, false, true, false,
                false, true)).isEqualTo(statusRoomHeatingCoolingControllerDPV);
        assertThat(statusRoomHeatingCoolingControllerDPT.toByteArray(true, true, false, false, true, true, false, true, true, false, false, true,
                false, false, true)).containsExactly(0x49, 0xB3);
        assertThat(statusRoomHeatingCoolingControllerDPV.toByteArray()).containsExactly(0x49, 0xB3);
        assertThatThrownBy(() -> statusRoomHeatingCoolingControllerDPT.toValue(new byte[1]))
                .isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // Media (0000 0110 = 0x06)
        DPT22.Media mediaDPT = DPT22.MEDIA;
        DPT22Value.Media mediaDPV = mediaDPT.toValue(true, true, false, false);
        assertThat(mediaDPT.toValue((byte) 0x00, (byte) 0x06)).isEqualTo(mediaDPV);
        assertThat(mediaDPT.toValue(true, true, false, false)).isEqualTo(mediaDPV);
        assertThat(mediaDPT.toByteArray(true, true, false, false)).containsExactly(0x00, 0x06);
        assertThat(mediaDPV.toByteArray()).containsExactly(0x00, 0x06);
        assertThatThrownBy(() -> mediaDPT.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);

        // 16 Channel Activation (1001 1010 0100 0010 = 0x9A 0x42)
        DPT22.ChannelActivation16 channel16DPT = DPT22.CHANNEL_ACTIVATION_16;
        DPT22Value.ChannelActivation16 channel16DPV = channel16DPT.toValue(false, true, false, false, false, false, true, false, false, true, false,
                true, true, false, false, true);
        assertThat(channel16DPT.toValue((byte) 0x9A, (byte) 0x42)).isEqualTo(channel16DPV);
        assertThat(channel16DPT.toValue(false, true, false, false, false, false, true, false, false, true, false, true, true, false, false, true))
                .isEqualTo(channel16DPV);
        assertThat(channel16DPT.toByteArray(false, true, false, false, false, false, true, false, false, true, false, true, true, false, false, true))
                .containsExactly(0x9A, 0x42);
        assertThat(channel16DPV.toByteArray()).containsExactly(0x9A, 0x42);
        assertThatThrownBy(() -> channel16DPT.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
    }

    /**
     * Test constructor of {@link DPT22}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT22.class);
    }
}
