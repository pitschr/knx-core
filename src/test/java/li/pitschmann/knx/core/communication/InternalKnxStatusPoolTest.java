/*
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

package li.pitschmann.knx.core.communication;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.address.KnxAddress;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.cemi.APCI;
import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.cemi.MessageCode;
import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.datapoint.DPT7;
import li.pitschmann.knx.core.datapoint.DPT9;
import li.pitschmann.knx.core.datapoint.value.DPT1Value;
import li.pitschmann.knx.core.datapoint.value.DPT9Value;
import li.pitschmann.knx.core.test.KnxBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link InternalKnxStatusPool}
 *
 * @author PITSCHR
 */
public final class InternalKnxStatusPoolTest {
    private static final IndividualAddress ADDRESS = IndividualAddress.of(1, 2, 3);
    private static final IndividualAddress ADDRESS_2 = IndividualAddress.of(2, 3, 4);
    private static final IndividualAddress ADDRESS_UNKNOWN = IndividualAddress.of(3, 4, 5);

    /**
     * Test {@link InternalKnxStatusPool#updateStatus(CEMI)} and {@link KnxStatusPool#getStatusFor(KnxAddress)}.
     * <p>
     * This will test {@link InternalKnxStatusPool#updateStatus(KnxAddress, KnxStatusData)} indirectly.
     */
    @Test
    @DisplayName("Test updateStatus(CEMI) and getStatusFor(KnxAddress)")
    public void testUpdateStatus() {
        final var pool = new InternalKnxStatusPool();
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS, APCI.GROUP_VALUE_WRITE, DPT7.VALUE_2_OCTET_UNSIGNED_COUNT.of(53623)));

        // verify
        final var statusData = pool.getStatusFor(ADDRESS);
        assertThat(statusData).isNotNull();
        assertThat(statusData.getAPCI()).isSameAs(APCI.GROUP_VALUE_WRITE);
        assertThat(statusData.getData()).containsExactly(0xD1, 0x77);
    }

    /**
     * Tests {@link InternalKnxStatusPool#existsStatusFor(KnxAddress)}
     */
    @Test
    @DisplayName("Test existsStatusFor(KnxAddress)")
    public void testExistsStatusFor() {
        final var pool = new InternalKnxStatusPool();

        // verify if not exists
        assertThat(pool.existsStatusFor(ADDRESS)).isFalse();

        // now add status
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS, APCI.GROUP_VALUE_WRITE, DPT1.SWITCH.of(false)));

        // verify it it exist now
        assertThat(pool.existsStatusFor(ADDRESS)).isTrue();
    }

    /**
     * Tests {@link KnxStatusPool#getStatusFor(KnxAddress)}, {@link KnxStatusPool#getStatusFor(KnxAddress, boolean)}
     */
    @Test
    @DisplayName("Test getStatusFor(KnxAddress) for known group address")
    public void testGetStatusForKnown() {
        final var pool = new InternalKnxStatusPool();

        // not found because the status data doesn't exists
        assertThat(pool.getStatusFor(ADDRESS)).isNull();
        assertThat(pool.getStatusFor(ADDRESS, false)).isNull();
        assertThat(pool.getStatusFor(ADDRESS, true)).isNull();

        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS, APCI.GROUP_VALUE_WRITE, DPT1.SWITCH.of(false)));

        // found as it is known in status pool
        assertThat(pool.getStatusFor(ADDRESS)).isNotNull();
        assertThat(pool.getStatusFor(ADDRESS, false)).isNotNull();
        assertThat(pool.getStatusFor(ADDRESS, true)).isNotNull();

        // set it as dirty
        pool.setDirty(ADDRESS);

        // not found because the status data is marked as dirty
        assertThat(pool.getStatusFor(ADDRESS)).isNull();
        assertThat(pool.getStatusFor(ADDRESS, true)).isNull();
        assertThat(pool.getStatusFor(ADDRESS, false)).isNull();
    }

    /**
     * Tests {@link KnxStatusPool#getStatusFor(KnxAddress)} for unknown group address
     */
    @Test
    @DisplayName("Test getStatusFor(KnxAddress) for unknown group address")
    public void testGetStatusForUnknown() {
        final var pool = new InternalKnxStatusPool();
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS, APCI.GROUP_VALUE_WRITE, DPT1.SWITCH.of(false)));

        // not found because not known to status pool (IndividualAddress != GroupAddress)
        assertThat(pool.getStatusFor(GroupAddress.of(1, 2, 3))).isNull();
    }

    /**
     * Tests {@link KnxStatusPool#getValue(KnxAddress, String)} with {@link DPT1#SWITCH}.
     */
    @Test
    @DisplayName("Test getValue(..) with DPT1 Switch")
    public void testGetValueSwitch() {
        final var pool = new InternalKnxStatusPool();
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS, APCI.GROUP_VALUE_WRITE, DPT1.SWITCH.of(false)));

        // Scenario 1: Init (false)
        final var boolValue = pool.<DPT1Value>getValue(ADDRESS, DPT1.SWITCH.getId());
        assertThat(boolValue.getValue()).isFalse();
        final var boolValue2 = pool.getValue(ADDRESS, DPT1.SWITCH);
        assertThat(boolValue2.getValue()).isFalse();

        // Scenario 2: Update (false -> true)
        // update status pool
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS_2, APCI.GROUP_VALUE_WRITE, DPT1.SWITCH.of(false)));

        // 1) pre-verify (before adding to status pool)
        // 2) update status pool
        // 3) post-verify (after adding to status pool)
        assertThat(pool.<DPT1Value>getValue(ADDRESS_2, DPT1.SWITCH.getId()).getValue()).isFalse();
        assertThat(pool.getValue(ADDRESS_2, DPT1.SWITCH).getValue()).isFalse();
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS_2, APCI.GROUP_VALUE_WRITE, DPT1.SWITCH.of(true)));
        assertThat(pool.<DPT1Value>getValue(ADDRESS_2, DPT1.SWITCH.getId()).getValue()).isTrue();
        assertThat(pool.getValue(ADDRESS_2, DPT1.SWITCH).getValue()).isTrue();

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
        final var pool = new InternalKnxStatusPool();
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS, APCI.GROUP_VALUE_WRITE, DPT9.TEMPERATURE.of(19.52d)));
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS_2, APCI.GROUP_VALUE_WRITE, DPT9.TEMPERATURE.of(20.78d)));

        // Scenario 1: Init (19.52 °C)
        final var tempValue = pool.<DPT9Value>getValue(ADDRESS, DPT9.TEMPERATURE.getId());
        assertThat(tempValue.getValue()).isEqualTo(19.52d);
        final var tempValue2 = pool.getValue(ADDRESS, DPT9.TEMPERATURE);
        assertThat(tempValue2.getValue()).isEqualTo(19.52d);

        // Scenario 2: Update (20.78 °C)
        assertThat(pool.<DPT9Value>getValue(ADDRESS_2, DPT9.TEMPERATURE.getId()).getValue()).isEqualTo(20.78d);
        assertThat(pool.getValue(ADDRESS_2, DPT9.TEMPERATURE).getValue()).isEqualTo(20.78d);

        // Scenario 3: unknown address
        assertThat(pool.<DPT9Value>getValue(ADDRESS_UNKNOWN, DPT9.TEMPERATURE.getId())).isNull();
        assertThat(pool.getValue(ADDRESS_UNKNOWN, DPT9.TEMPERATURE)).isNull();

        // Scenario 4: illegal parameter
        assertThatThrownBy(() -> pool.<DPT9Value>getValue(null, DPT9.TEMPERATURE.getId())).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> pool.getValue(null, DPT9.TEMPERATURE)).isInstanceOf(NullPointerException.class);

    }

    /**
     * Test {@link KnxStatusPool#isUpdated(KnxAddress)}
     */
    @Test
    @DisplayName("Test #isUpdated(..) with unknown, known and illegal parameters")
    public void testIsUpdated() {
        final var pool = new InternalKnxStatusPool();
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS, APCI.GROUP_VALUE_READ, null));

        // Scenario 1: test with unknown status
        assertThat(pool.isUpdated(ADDRESS_UNKNOWN)).isFalse();

        // Scenario 2: test with known and updated status
        assertThat(pool.isUpdated(ADDRESS)).isTrue();

        // Scenario 3: test with known and not updated status
        pool.setDirty(ADDRESS);
        assertThat(pool.isUpdated(ADDRESS)).isFalse();
    }

    /**
     * Test {@link InternalKnxStatusPool#setDirty(KnxAddress)}
     */
    @Test
    @DisplayName("Test #setDirty(..) with known, unknown and illegal parameter")
    public void testSetDirty() {
        final var pool = new InternalKnxStatusPool();
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS, APCI.GROUP_VALUE_READ, null));

        // test with known and unknown KNX address
        pool.setDirty(ADDRESS);
        pool.setDirty(ADDRESS_UNKNOWN);

        // test with invalid null parameter
        assertThatThrownBy(() -> pool.setDirty((KnxAddress) null)).isInstanceOf(NullPointerException.class);

        // test with tunneling and routing body
        pool.setDirty(KnxBody.TUNNELING_REQUEST_BODY);
        pool.setDirty(KnxBody.ROUTING_INDICATION_BODY);

        // test with valid null request body parameter
        pool.setDirty((RequestBody) null);
    }

    /**
     * Test {@link InternalKnxStatusPool#copyStatusMap()}
     */
    @Test
    @DisplayName("Test #copyStatusMap() method")
    public void testCopyStatusMap() {
        final var pool = new InternalKnxStatusPool();

        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_IND, ADDRESS, APCI.GROUP_VALUE_READ, null));

        // call this method twice times
        final var map1 = pool.copyStatusMap();
        final var map2 = pool.copyStatusMap();

        assertThat(map1).isNotSameAs(map2); // the object reference should not be same
        assertThat(map1).isEqualTo(map2); // but entry should be same
    }

    /**
     * Test {@link KnxStatusPool#toString()}
     */
    @Test
    @DisplayName("Test #toString() method")
    public void testToString() {
        final var pool = new InternalKnxStatusPool();

        // empty status map
        assertThat(pool).hasToString("InternalKnxStatusPool{statusMap={}}");

        // with 1 element in status map
        pool.updateStatus(CEMI.useDefault(MessageCode.L_DATA_REQ, ADDRESS, APCI.GROUP_VALUE_WRITE, DPT1.SWITCH.of(false)));
        assertThat(pool).hasToString(String.format("InternalKnxStatusPool{statusMap={%s=%s}}", ADDRESS, pool.getStatusFor(ADDRESS)));
    }
}
