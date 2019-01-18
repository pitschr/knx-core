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

import li.pitschmann.knx.link.datapoint.value.DPT22Value.*;
import li.pitschmann.test.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test {@link DPT22Value}
 *
 * @author PITSCHR
 */
public final class DPT22ValueTest {
    /**
     * Test {@link StatusDHWController}
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
        StatusDHWController dptValue = new StatusDHWController(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7]);
        assertThat(new StatusDHWController(bytes)).isEqualTo(dptValue);

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
     * Test {@link StatusRoomHeatinCoolingController}
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
        StatusRoomHeatinCoolingController dptValue = new StatusRoomHeatinCoolingController(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5],
                bool[6], bool[7], bool[8], bool[9], bool[10], bool[11], bool[12], bool[13], bool[14]);
        assertThat(new StatusRoomHeatinCoolingController(bytes)).isEqualTo(dptValue);

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
        Media dptValue = new Media(bool[0], bool[1], bool[2], bool[3]);
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
        this.assertChannelActivation16(new byte[2], new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false});

        // b0 .. b7
        this.assertChannelActivation16(new byte[]{0x00, 0x01}, new boolean[]{true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x00, 0x02}, new boolean[]{false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x00, 0x04}, new boolean[]{false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x00, 0x08}, new boolean[]{false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x00, 0x10}, new boolean[]{false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x00, 0x20}, new boolean[]{false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x00, 0x40}, new boolean[]{false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x00, (byte) 0x80}, new boolean[]{false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false});

        // b8 .. b15
        this.assertChannelActivation16(new byte[]{0x01, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x02, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x04, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x08, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false});
        this.assertChannelActivation16(new byte[]{0x10, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false});
        this.assertChannelActivation16(new byte[]{0x20, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false});
        this.assertChannelActivation16(new byte[]{0x40, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false});
        this.assertChannelActivation16(new byte[]{(byte) 0x80, 0x00}, new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true});
        //@formatter:on
    }

    private void assertChannelActivation16(final byte[] bytes, final boolean[] bool) {
        ChannelActivation16 dptValue = new ChannelActivation16(bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7], bool[8],
                bool[9], bool[10], bool[11], bool[12], bool[13], bool[14], bool[15]);
        assertThat(new ChannelActivation16(bytes)).isEqualTo(dptValue);

        for (int i = 0; i < 16; i++) {
            assertThat(dptValue.isChannelActive(i + 1)).isEqualTo(bool[i]);
        }

        assertThatThrownBy(() -> dptValue.isChannelActive(0)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Channel must be between 1 and 16");
        assertThatThrownBy(() -> dptValue.isChannelActive(17)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Channel must be between 1 and 16");
    }

    /**
     * Test constructor of {@link DPT22Value}
     */
    @Test
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT22Value.class);
    }
}
