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

import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.datapoint.DPT1;
import li.pitschmann.knx.link.datapoint.DPT9;
import li.pitschmann.knx.link.datapoint.value.DPT1Value;
import li.pitschmann.knx.link.datapoint.value.DPT9Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link KnxStatusPool}
 *
 * @author PITSCHR
 */
public class KnxStatusPoolTest {
    private static final IndividualAddress ADDRESS = IndividualAddress.of(1, 2, 3);
    private static final IndividualAddress ADDRESS_2 = IndividualAddress.of(2, 3, 4);
    private static final IndividualAddress ADDRESS_UNKNOWN = IndividualAddress.of(3, 4, 5);

    /**
     * Test {@link KnxStatusPool#updateStatus(CEMI)} and {@link KnxStatusPool#getStatusFor(KnxAddress)}.
     * <p/>
     * This will test {@link KnxStatusPool#updateStatus(KnxAddress, KnxStatusData)} indirectly.
     */
    @Test
    @DisplayName("Test updateStatus(CEMI) and getStatusFor(KnxAddress)")
    public void testUpdateStatus() {
        final var pool = new KnxStatusPool();
        pool.updateStatus(CEMI.useDefault(ADDRESS, APCI.GROUP_VALUE_WRITE, new byte[]{0x44, 0x22, 0x33}));

        // verify
        var statusData = pool.getStatusFor(ADDRESS);
        assertThat(statusData).isNotNull();
        assertThat(statusData.getApci()).isSameAs(APCI.GROUP_VALUE_WRITE);
        assertThat(statusData.getApciData()).containsExactly(0x44, 0x22, 0x33);
    }

    /**
     * Tests {@link KnxStatusPool#getStatusFor(KnxAddress)} for unknown
     */
    @Test
    @DisplayName("Test getStatusFor(KnxAddress) for unknown")
    public void testGetStatusForUnknown() {
        var pool = new KnxStatusPool();
        pool.updateStatus(CEMI.useDefault(ADDRESS, APCI.GROUP_VALUE_READ, new byte[0]));

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
        pool.updateStatus(CEMI.useDefault(ADDRESS, APCI.GROUP_VALUE_WRITE, new byte[]{0x00}));

        // Scenario 1: Init (false)
        final DPT1Value boolValue = pool.getValue(ADDRESS, DPT1.SWITCH.getId());
        assertThat(boolValue.getBooleanValue()).isFalse();
        final DPT1Value boolValue2 = pool.getValue(ADDRESS, DPT1.SWITCH);
        assertThat(boolValue2.getBooleanValue()).isFalse();

        // Scenario 2: Update (false -> true)
        // update status pool
        pool.updateStatus(CEMI.useDefault(ADDRESS_2, APCI.GROUP_VALUE_WRITE, new byte[]{0x00}));

        // 1) pre-verify (before adding to status pool)
        // 2) update status pool
        // 3) post-verify (after adding to status pool)
        assertThat(pool.<DPT1Value>getValue(ADDRESS_2, DPT1.SWITCH.getId()).getBooleanValue()).isFalse();
        assertThat(pool.getValue(ADDRESS_2, DPT1.SWITCH).getBooleanValue()).isFalse();
        pool.updateStatus(CEMI.useDefault(ADDRESS_2, APCI.GROUP_VALUE_WRITE, new byte[]{0x01}));
        assertThat(pool.<DPT1Value>getValue(ADDRESS_2, DPT1.SWITCH.getId()).getBooleanValue()).isTrue();
        assertThat(pool.getValue(ADDRESS_2, DPT1.SWITCH).getBooleanValue()).isTrue();

        // Scenario 3: unknown address
        assertThat(pool.<DPT1Value>getValue(ADDRESS_UNKNOWN, DPT1.SWITCH.getId())).isNull();
        assertThat(pool.getValue(ADDRESS_UNKNOWN, DPT1.SWITCH)).isNull();

        // Scenario 4: illegal parameter
        assertThatThrownBy(() -> pool.<DPT1Value>getValue(null, DPT1.SWITCH.getId())).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> pool.getValue(null, DPT1.SWITCH)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Tests {@link KnxStatusPool#getValue(KnxAddress, String)} with {@link DPT1#SWITCH}.
     */
    @Test
    @DisplayName("Test #getValue(..) with DPT9 Temperature")
    public void testGetValueTemperature() {
        final var pool = new KnxStatusPool();
        pool.updateStatus(CEMI.useDefault(ADDRESS, APCI.GROUP_VALUE_WRITE, new byte[]{0x07, (byte) 0xA0}));
        pool.updateStatus(CEMI.useDefault(ADDRESS_2, APCI.GROUP_VALUE_WRITE, new byte[]{0x0C, 0x0F}));

        // Scenario 1: Init (19.52 °C)
        final DPT9Value tempValue = pool.getValue(ADDRESS, DPT9.TEMPERATURE.getId());
        assertThat(tempValue.getFloatingValue()).isEqualTo(19.52d);
        final DPT9Value tempValue2 = pool.getValue(ADDRESS, DPT9.TEMPERATURE);
        assertThat(tempValue2.getFloatingValue()).isEqualTo(19.52d);

        // Scenario 2: Update (20.78 °C)
        assertThat(pool.<DPT9Value>getValue(ADDRESS_2, DPT9.TEMPERATURE.getId()).getFloatingValue()).isEqualTo(20.78d);
        assertThat(pool.getValue(ADDRESS_2, DPT9.TEMPERATURE).getFloatingValue()).isEqualTo(20.78d);

        // Scenario 3: unknown address
        assertThat(pool.<DPT9Value>getValue(ADDRESS_UNKNOWN, DPT9.TEMPERATURE.getId())).isNull();
        assertThat(pool.getValue(ADDRESS_UNKNOWN, DPT9.TEMPERATURE)).isNull();

        // Scenario 4: illegal parameter
        assertThatThrownBy(() -> pool.<DPT9Value>getValue(null, DPT9.TEMPERATURE.getId())).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> pool.getValue(null, DPT9.TEMPERATURE)).isInstanceOf(NullPointerException.class);

    }

    /**
     * Test {@link KnxStatusPool#isUpdated(KnxAddress, long, TimeUnit)} and
     * {@link KnxStatusPool#isUpdated(KnxAddress)} indirectly
     */
    @Test
    @DisplayName("Test #isUpdated(..) with unknown, known and illegal parameters")
    public void testIsUpdated() {
        final var pool = new KnxStatusPool();
        pool.updateStatus(CEMI.useDefault(ADDRESS, APCI.GROUP_VALUE_READ, new byte[0]));

        // Scenario 1: test with unknown status
        assertThat(pool.isUpdated(ADDRESS_UNKNOWN, 30, TimeUnit.MILLISECONDS)).isFalse();

        // Scenario 2: test with known and updated status
        assertThat(pool.isUpdated(ADDRESS, 30, TimeUnit.MILLISECONDS)).isTrue();

        // Scenario 3: test with known and not updated status
        pool.setDirty(ADDRESS);
        assertThat(pool.isUpdated(ADDRESS, 30, TimeUnit.MILLISECONDS)).isFalse();

        // Scenario 4: test with invalid parameters
        assertThatThrownBy(() -> pool.isUpdated(null, 0, TimeUnit.MILLISECONDS)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> pool.isUpdated(ADDRESS, 0, null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Test {@link KnxStatusPool#setDirty(KnxAddress)}
     */
    @Test
    @DisplayName("Test #setDirty(..) with known, unknown and illegal parameter")
    public void testSetDirty() {
        final var pool = new KnxStatusPool();
        pool.updateStatus(CEMI.useDefault(ADDRESS, APCI.GROUP_VALUE_READ, new byte[0]));

        // Scenario 1: test with known KNX address
        pool.setDirty(ADDRESS);

        // Scenario 2: test with unknown KNX address
        pool.setDirty(ADDRESS_UNKNOWN);

        // Scenario 3: test with invalid null parameter
        assertThatThrownBy(() -> pool.setDirty(null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Test {@link KnxStatusPool#toString()}
     */
    @Test
    @DisplayName("Test #toString() method")
    public void testToString() {
        final var pool = new KnxStatusPool();

        // empty status map
        assertThat(pool).hasToString("KnxStatusPool{statusMap={}}");

        // with 1 element in status map
        pool.updateStatus(CEMI.useDefault(ADDRESS, APCI.GROUP_VALUE_WRITE, new byte[]{0x12}));
        assertThat(pool).hasToString(String.format("KnxStatusPool{statusMap={%s=%s}}", ADDRESS, pool.getStatusFor(ADDRESS)));
    }
}
