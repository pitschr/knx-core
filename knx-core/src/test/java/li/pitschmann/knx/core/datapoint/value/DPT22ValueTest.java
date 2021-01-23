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

import li.pitschmann.knx.core.datapoint.value.DPT22Value.ChannelActivation16;
import li.pitschmann.knx.core.datapoint.value.DPT22Value.DHWControllerStatus;
import li.pitschmann.knx.core.datapoint.value.DPT22Value.Media;
import li.pitschmann.knx.core.datapoint.value.DPT22Value.RoomHeatingCoolingControllerStatus;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT22Value}
 *
 * @author PITSCHR
 */
public final class DPT22ValueTest {
    /**
     * Test {@link DHWControllerStatus}
     */
    @Test
    public void testStatusDHWController() {
        // @formatter:off
        this.assertStatusDHWController(new byte[2], new boolean[]{false, false, false, false, false, false, false, false});
        this.assertStatusDHWController(new byte[]{0x00, 0x01}, new boolean[]{true, false, false, false, false, false, false, false});
        this.assertStatusDHWController(new byte[]{0x00, 0x02}, new boolean[]{false, true, false, false, false, false, false, false});
        this.assertStatusDHWController(new byte[]{0x00, 0x04}, new boolean[]{false, false, true, false, false, false, false, false});
        this.assertStatusDHWController(new byte[]{0x00, 0x08}, new boolean[]{false, false, false, true, false, false, false, false});
        this.assertStatusDHWController(new byte[]{0x00, 0x10}, new boolean[]{false, false, false, false, true, false, false, false});
        this.assertStatusDHWController(new byte[]{0x00, 0x20}, new boolean[]{false, false, false, false, false, true, false, false});
        this.assertStatusDHWController(new byte[]{0x00, 0x40}, new boolean[]{false, false, false, false, false, false, true, false});
        this.assertStatusDHWController(new byte[]{0x00, (byte) 0x80}, new boolean[]{false, false, false, false, false, false, false, true});
        // @formatter:on
    }

    private void assertStatusDHWController(final byte[] bytes, final boolean[] bool) {
        final var dptValue = new DHWControllerStatus(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7]);
        assertThat(new DHWControllerStatus(bytes)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool[0]);
        assertThat(dptValue.isLoadActive()).isEqualTo(bool[1]);
        assertThat(dptValue.isLegionallaProtectionActive()).isEqualTo(bool[2]);
        assertThat(dptValue.isPushActive()).isEqualTo(bool[3]);
        assertThat(dptValue.isOtherEnergySourceActive()).isEqualTo(bool[4]);
        assertThat(dptValue.isSolarEnergyOnly()).isEqualTo(bool[5]);
        assertThat(dptValue.isSolarEnergySupport()).isEqualTo(bool[6]);
        assertThat(dptValue.isTemperatureSetpointInfluenced()).isEqualTo(bool[7]);
    }

    /**
     * Test {@link RoomHeatingCoolingControllerStatus}
     */
    @Test
    public void testStatusRoomHeatinCoolingController() {
        // @formatter:off
        this.assertStatusRoomHeatinCoolingController(new byte[2], new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x00, 0x01}, new boolean[]{true, false, false, false, false, false, false, false, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x00, 0x02}, new boolean[]{false, true, false, false, false, false, false, false, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x00, 0x04}, new boolean[]{false, false, true, false, false, false, false, false, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x00, 0x08}, new boolean[]{false, false, false, true, false, false, false, false, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x00, 0x10}, new boolean[]{false, false, false, false, true, false, false, false, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x00, 0x20}, new boolean[]{false, false, false, false, false, true, false, false, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x00, 0x40}, new boolean[]{false, false, false, false, false, false, true, false, false, false, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x00, (byte) 0x80}, new boolean[]{false, false, false, false, false, false, false, true, false, false, false, false, false, false, false});

        this.assertStatusRoomHeatinCoolingController(new byte[]{0x01, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, true, false, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x02, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, true, false, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x04, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, true, false, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x08, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x10, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true, false, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x20, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, true, false});
        this.assertStatusRoomHeatinCoolingController(new byte[]{0x40, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, true});

        // @formatter:on
    }

    private void assertStatusRoomHeatinCoolingController(final byte[] bytes, final boolean[] bool) {
        final var dptValue = new RoomHeatingCoolingControllerStatus(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5],
                bool[6], bool[7], bool[8], bool[9], bool[10], bool[11], bool[12], bool[13], bool[14]);
        assertThat(new RoomHeatingCoolingControllerStatus(bytes)).isEqualTo(dptValue);

        assertThat(dptValue.isFault()).isEqualTo(bool[0]);
        assertThat(dptValue.isStatusEcoHeating()).isEqualTo(bool[1]);
        assertThat(dptValue.isTemperatureFlowLimit()).isEqualTo(bool[2]);
        assertThat(dptValue.isTemperatureReturnLimit()).isEqualTo(bool[3]);
        assertThat(dptValue.isStatusMorningBoost()).isEqualTo(bool[4]);
        assertThat(dptValue.isStartOptimizationActive()).isEqualTo(bool[5]);
        assertThat(dptValue.isStopOptimizationActive()).isEqualTo(bool[6]);
        assertThat(dptValue.isHeatingDisabled()).isEqualTo(bool[7]);
        assertThat(dptValue.isHeatingMode()).isEqualTo(bool[8]);
        assertThat(dptValue.isStatusEcoCooling()).isEqualTo(bool[9]);
        assertThat(dptValue.isStatusPreCooling()).isEqualTo(bool[10]);
        assertThat(dptValue.isCoolingDisabled()).isEqualTo(bool[11]);
        assertThat(dptValue.isDewPointAlarm()).isEqualTo(bool[12]);
        assertThat(dptValue.isFrostAlarm()).isEqualTo(bool[13]);
        assertThat(dptValue.isOverheatAlarm()).isEqualTo(bool[14]);
    }

    /**
     * Test {@link Media}
     */
    @Test
    public void testMedia() {
        // @formatter:off
        this.assertMedia(new byte[2], new boolean[]{false, false, false, false});
        this.assertMedia(new byte[]{0x00, 0x02}, new boolean[]{true, false, false, false});
        this.assertMedia(new byte[]{0x00, 0x04}, new boolean[]{false, true, false, false});
        this.assertMedia(new byte[]{0x00, 0x10}, new boolean[]{false, false, true, false});
        this.assertMedia(new byte[]{0x00, 0x20}, new boolean[]{false, false, false, true});
        // @formatter:on
    }

    private void assertMedia(final byte[] bytes, final boolean[] bool) {
        final var dptValue = new Media(bool[0], bool[1], bool[2], bool[3]);
        assertThat(new Media(bytes)).isEqualTo(dptValue);

        assertThat(dptValue.isTP1Supported()).isEqualTo(bool[0]);
        assertThat(dptValue.isPL110Supported()).isEqualTo(bool[1]);
        assertThat(dptValue.isRFSupported()).isEqualTo(bool[2]);
        assertThat(dptValue.isKNXIPSupported()).isEqualTo(bool[3]);
    }

    /**
     * Test {@link ChannelActivation16}
     */
    @Test
    public void testChannelActivation16() {
        // @formatter:off
        this.assertChannelActivation16(new byte[2], new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}, "no channels active");

        // b0 .. b7
        this.assertChannelActivation16(new byte[]{0x00, 0x01}, new boolean[]{true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}, "1");
        this.assertChannelActivation16(new byte[]{0x00, 0x02}, new boolean[]{false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false}, "2");
        this.assertChannelActivation16(new byte[]{0x00, 0x04}, new boolean[]{false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false}, "3");
        this.assertChannelActivation16(new byte[]{0x00, 0x08}, new boolean[]{false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false}, "4");
        this.assertChannelActivation16(new byte[]{0x00, 0x10}, new boolean[]{false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false}, "5");
        this.assertChannelActivation16(new byte[]{0x00, 0x20}, new boolean[]{false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false}, "6");
        this.assertChannelActivation16(new byte[]{0x00, 0x40}, new boolean[]{false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false}, "7");
        this.assertChannelActivation16(new byte[]{0x00, (byte) 0x80}, new boolean[]{false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false}, "8");

        // b8 .. b15
        this.assertChannelActivation16(new byte[]{0x01, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false}, "9");
        this.assertChannelActivation16(new byte[]{0x02, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false}, "10");
        this.assertChannelActivation16(new byte[]{0x04, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false}, "11");
        this.assertChannelActivation16(new byte[]{0x08, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false}, "12");
        this.assertChannelActivation16(new byte[]{0x10, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false}, "13");
        this.assertChannelActivation16(new byte[]{0x20, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false}, "14");
        this.assertChannelActivation16(new byte[]{0x40, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false}, "15");
        this.assertChannelActivation16(new byte[]{(byte) 0x80, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true}, "16");

        // multiple channels
        this.assertChannelActivation16(new byte[]{0x58, 0x08}, new boolean[]{false, false, false, true, false, false, false, false, false, false, false, true, true, false, true, false}, "4, 12, 13, 15");
        this.assertChannelActivation16(new byte[]{(byte) 0x81, 0x07}, new boolean[]{true, true, true, false, false, false, false, false, true, false, false, false, false, false, false, true}, "1, 2, 3, 9, 16");
        //@formatter:on
    }

    private void assertChannelActivation16(final byte[] bytes, final boolean[] bool, final String text) {
        final var dptValue = new ChannelActivation16(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7], bool[8],
                bool[9], bool[10], bool[11], bool[12], bool[13], bool[14], bool[15]);
        assertThat(new ChannelActivation16(bytes)).isEqualTo(dptValue);

        for (var i = 0; i < 16; i++) {
            assertThat(dptValue.isChannelActive(i + 1)).isEqualTo(bool[i]);
        }
        assertThat(dptValue.toText()).isEqualTo(text);

        assertThatThrownBy(() -> dptValue.isChannelActive(0)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Channel must be between 1 and 16");
        assertThatThrownBy(() -> dptValue.isChannelActive(17)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Channel must be between 1 and 16");
    }

    /**
     * Test constructor of {@link DPT22Value}
     */
    @DisplayName("Constructor not instantiable")
    @Test
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT22Value.class);
    }
}
