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

package li.pitschmann.knx.link.communication;

import li.pitschmann.knx.link.body.address.*;
import li.pitschmann.knx.link.body.cemi.*;
import li.pitschmann.knx.link.datapoint.*;
import li.pitschmann.knx.link.datapoint.value.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the {@link KnxStatusPool}
 *
 * @author PITSCHR
 */
public class KnxStatusPoolTest {
    private static final IndividualAddress ADDRESS = IndividualAddress.of(1, 2, 3);
    private static final IndividualAddress ADDRESS_2 = IndividualAddress.of(2, 3, 4);

    /**
     * Test {@link KnxStatusPool#updateStatus(CEMI)} and {@link KnxStatusPool#getStatusFor(KnxAddress)}.
     * <p/>
     * This will test {@link KnxStatusPool#updateStatus(KnxAddress, KnxStatusData)} indirectly.
     */
    @Test
    @DisplayName("Test updateStatus(CEMI) and getStatusFor(KnxAddress)")
    public void testUpdateStatus() {
        var pool = new KnxStatusPool();

        final var cemi = mock(CEMI.class);
        when(cemi.getDestinationAddress()).thenReturn(ADDRESS);
        when(cemi.getApci()).thenReturn(APCI.GROUP_VALUE_WRITE);
        when(cemi.getApciData()).thenReturn(new byte[]{0x44, 0x22, 0x33});

        // add to status pool
        pool.updateStatus(cemi);

        // verify
        var statusData = pool.getStatusFor(ADDRESS);
        assertThat(statusData).isNotNull();
        assertThat(statusData.getApci()).isSameAs(cemi.getApci());
        assertThat(statusData.getApciData()).containsExactly(cemi.getApciData());
    }

    /**
     * Tests {@link KnxStatusPool#getStatusFor(KnxAddress)} for unknown
     */
    @Test
    @DisplayName("Test getStatusFor(KnxAddress) for unknown")
    public void testGetStatusForUnknown() {
        var pool = new KnxStatusPool();

        final var cemi = mock(CEMI.class);
        when(cemi.getDestinationAddress()).thenReturn(ADDRESS);
        when(cemi.getApci()).thenReturn(APCI.GROUP_VALUE_WRITE);
        when(cemi.getApciData()).thenReturn(new byte[]{0x00});

        // not found
        assertThat(pool.getStatusFor(null)).isNull();
        // not found because not known to status pool (IndividualAddress != GroupAddress)
        assertThat(pool.getStatusFor(GroupAddress.of(1, 2, 3))).isNull();
    }

    /**
     * Tests {@link KnxStatusPool#getValue(KnxAddress, String)} with {@link DPT1#SWITCH}.
     */
    @Test
    @DisplayName("Test getValue(..) with DPT1 Switch")
    public void testGetValueSwitch() {
        final var pool = new KnxStatusPool();

        final var cemiBool = mock(CEMI.class);
        when(cemiBool.getDestinationAddress()).thenReturn(ADDRESS);
        when(cemiBool.getApci()).thenReturn(APCI.GROUP_VALUE_WRITE);

        // Scenario 1: Init (false)
        // add to status pool
        when(cemiBool.getApciData()).thenReturn(new byte[]{0x00});
        pool.updateStatus(cemiBool);

        final DPT1Value boolValue = pool.getValue(ADDRESS, DPT1.SWITCH.getId());
        assertThat(boolValue.getBooleanValue()).isFalse();
        final DPT1Value boolValue2 = pool.getValue(ADDRESS, DPT1.SWITCH);
        assertThat(boolValue2.getBooleanValue()).isFalse();

        // Scenario 2: Update (false -> true)
        // update status pool
        when(cemiBool.getApciData()).thenReturn(new byte[]{0x01});

        // 1) pre-verify (before adding to status pool)
        // 2) update status pool
        // 3) post-verify (after adding to status pool)
        assertThat(pool.<DPT1Value>getValue(ADDRESS, DPT1.SWITCH.getId()).getBooleanValue()).isFalse();
        assertThat(pool.getValue(ADDRESS, DPT1.SWITCH).getBooleanValue()).isFalse();
        pool.updateStatus(cemiBool);
        assertThat(pool.<DPT1Value>getValue(ADDRESS, DPT1.SWITCH.getId()).getBooleanValue()).isTrue();
        assertThat(pool.getValue(ADDRESS, DPT1.SWITCH).getBooleanValue()).isTrue();

        // Scenario 3: unknown address
        assertThat(pool.<DPT1Value>getValue(null, DPT1.SWITCH.getId())).isNull();
        assertThat(pool.getValue(null, DPT1.SWITCH)).isNull();
    }

    /**
     * Tests {@link KnxStatusPool#getValue(KnxAddress, String)} with {@link DPT1#SWITCH}.
     */
    @Test
    @DisplayName("Test getValue(..) with DPT9 Temperature")
    public void testGetValueTemperature() {
        final var pool = new KnxStatusPool();

        final var cemi = mock(CEMI.class);
        when(cemi.getDestinationAddress()).thenReturn(ADDRESS_2);
        when(cemi.getApci()).thenReturn(APCI.GROUP_VALUE_READ);

        // Scenario 1: Init (19.52 °C)
        // add to status pool
        when(cemi.getApciData()).thenReturn(new byte[]{0x07, (byte) 0xA0});
        pool.updateStatus(cemi);

        final DPT9Value tempValue = pool.getValue(ADDRESS_2, DPT9.TEMPERATURE.getId());
        assertThat(tempValue.getFloatingValue()).isEqualTo(19.52d);
        final DPT9Value tempValue2 = pool.getValue(ADDRESS_2, DPT9.TEMPERATURE);
        assertThat(tempValue2.getFloatingValue()).isEqualTo(19.52d);

        // Scenario 2: Update (20.78 °C)
        // update status pool
        when(cemi.getApciData()).thenReturn(new byte[]{0x0C, 0x0F});
        pool.updateStatus(cemi);

        assertThat(pool.<DPT9Value>getValue(ADDRESS_2, DPT9.TEMPERATURE.getId()).getFloatingValue()).isEqualTo(20.78d);
        assertThat(pool.getValue(ADDRESS_2, DPT9.TEMPERATURE).getFloatingValue()).isEqualTo(20.78d);

        // Scenario 3: unknown address
        assertThat(pool.<DPT9Value>getValue(null, DPT9.TEMPERATURE.getId())).isNull();
        assertThat(pool.getValue(null, DPT9.TEMPERATURE)).isNull();
    }
}
