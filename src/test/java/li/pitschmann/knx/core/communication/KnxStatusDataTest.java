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

import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.cemi.APCI;
import li.pitschmann.knx.core.cemi.CEMI;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link KnxStatusData}
 *
 * @author PITSCHR
 */
public class KnxStatusDataTest {
    private static final IndividualAddress ADDRESS = IndividualAddress.of(1, 2, 3);
    private static final IndividualAddress ADDRESS_2 = IndividualAddress.of(3, 4, 5);

    /**
     * Tests the initialization of {@link KnxStatusData}
     */
    @Test
    public void testInit() {
        final var cemi = mock(CEMI.class);
        when(cemi.getSourceAddress()).thenReturn(ADDRESS);
        when(cemi.getAPCI()).thenReturn(APCI.GROUP_VALUE_READ);
        when(cemi.getData()).thenReturn(new byte[0]);

        final var beforeInit = Instant.now();
        final var statusData = new KnxStatusData(cemi);
        final var afterInit = Instant.now();

        assertThat(statusData.getTimestamp()).isBetween(beforeInit, afterInit);
        assertThat(statusData.getAPCI()).isSameAs(APCI.GROUP_VALUE_READ);
        assertThat(statusData.getData()).isEmpty();
        assertThat(statusData.getSourceAddress()).isSameAs(ADDRESS);
    }

    /**
     * Checks if the {@link KnxStatusData} is immutable.
     * <p><
     * In {@link KnxStatusData} we only need to take care about immutability for apci data.
     * Rest are immutable as they don't provide a setter.
     */
    @Test
    public void testImmutability() {
        byte[] data = new byte[]{0x11, 0x22};

        final var cemi = mock(CEMI.class);
        when(cemi.getSourceAddress()).thenReturn(ADDRESS);
        when(cemi.getAPCI()).thenReturn(APCI.GROUP_VALUE_READ);
        when(cemi.getData()).thenReturn(data);

        // create status data instance
        final var statusData = new KnxStatusData(cemi);

        // try to change outside of status data instance
        data[1] = 0x33;

        // expected to be unchanged!
        assertThat(statusData.getData()).containsExactly(0x11, 0x22);

        // try to change the reference - expected to be unchanged
        statusData.getData()[1] = 0x44;
        assertThat(statusData.getData()).containsExactly(0x11, 0x22);
    }

    /**
     * Test {@link KnxStatusData#toString()}
     */
    @Test
    public void testToString() {
        // Test #1
        final var cemi1 = mock(CEMI.class);
        when(cemi1.getSourceAddress()).thenReturn(ADDRESS);
        when(cemi1.getAPCI()).thenReturn(APCI.GROUP_VALUE_READ);
        when(cemi1.getData()).thenReturn(new byte[0]);

        final var statusData1 = new KnxStatusData(cemi1);
        assertThat(statusData1).hasToString(String.format(
                "KnxStatusData{" +
                        "dirty=false, " +
                        "timestamp=%s, " +
                        "sourceAddress=%s, " +
                        "apci=%s, " +
                        "data=[] ()" +
                        "}", statusData1.getTimestamp(), cemi1.getSourceAddress(), cemi1.getAPCI()));

        // Test #2
        final var cemi2 = mock(CEMI.class);
        when(cemi2.getSourceAddress()).thenReturn(ADDRESS_2);
        when(cemi2.getAPCI()).thenReturn(APCI.GROUP_VALUE_WRITE);
        when(cemi2.getData()).thenReturn(new byte[]{0x44, 0x22, 0x33});

        final var statusData2 = new KnxStatusData(cemi2);
        assertThat(statusData2).hasToString(String.format(
                "KnxStatusData{" +
                        "dirty=false, " +
                        "timestamp=%s, " +
                        "sourceAddress=%s, " +
                        "apci=%s, " +
                        "data=[68, 34, 51] (0x44 22 33)" +
                        "}", statusData2.getTimestamp(), cemi2.getSourceAddress(), cemi2.getAPCI()));
    }
}
