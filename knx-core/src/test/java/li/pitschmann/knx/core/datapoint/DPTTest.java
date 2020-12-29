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

import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DataPointType}
 *
 * @author PITSCHR
 */
public interface DPTTest {
    /**
     * General DPT test for id and description
     */
    @Test
    void testIdAndDescription();

    /**
     * General DPT test for byte array compatibility.
     */
    @Test
    void testCompatibility();

    /**
     * Test the given Data Point Type.
     */
    @Test
    void testOf();

    /**
     * Asserts the {@link DataPointType} for given arguments {@code dpt}, {@code bValueArray} and
     * {@code dptValue}
     *
     * @param dpt         data point type
     * @param bValueArray byte array with byte value
     * @param dptValue    data point type value
     */
    default void assertBaseDPT(final DataPointType dpt, final byte[] bValueArray, final DataPointValue dptValue) {
        // create by #of(byte[])
        final var baseOfValue = dpt.of(bValueArray);
        assertThat(baseOfValue).isEqualTo(dptValue);
        assertThat(dptValue.getDPT()).isEqualTo(dpt);

        // create by #toByteArray(..)
        assertThat(baseOfValue.toByteArray()).containsExactly(bValueArray);
    }
}
