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

package li.pitschmann.knx.utils;

import li.pitschmann.knx.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for {@link Maps} utility class
 *
 * @author PITSCHR
 */
public class MapsTest {

    @Test
    @DisplayName("Test if new hash map is returned with correct capacity")
    public void testNewHashMap() throws ReflectiveOperationException {
        final var map = Maps.newHashMap(1000);
        assertThat(map).isInstanceOf(HashMap.class);
        assertCapacity(map, 2048);

        assertCapacity(Maps.newHashMap(1), 2);
        assertCapacity(Maps.newHashMap(2), 4);
        assertCapacity(Maps.newHashMap(3), 8);
        assertCapacity(Maps.newHashMap(4), 8);
        assertCapacity(Maps.newHashMap(200), 512);
        assertCapacity(Maps.newHashMap(1000), 2048);
        assertCapacity(Maps.newHashMap(Integer.MAX_VALUE), 1 << (Integer.SIZE - 2));
    }

    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Maps.class);
    }

    /**
     * Assert the capacity of {@link HashMap} which is hold by an internal
     * field called {@code threshold}
     *
     * @param map
     * @param expectedCapacity
     */
    private void assertCapacity(final HashMap<?, ?> map, final int expectedCapacity) {
        // we have to check 'threshold' in HashMap
        try {
            final var unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            final var unsafeInstance = (Unsafe) unsafeField.get(null);

            // check by reflection
            final var field = HashMap.class.getDeclaredField("threshold");
            final var threshold = unsafeInstance.getInt(map, unsafeInstance.objectFieldOffset(field));
            assertThat(threshold).isEqualTo(expectedCapacity);
        } catch (final ReflectiveOperationException roe) {
            fail(roe);
        }
    }
}
