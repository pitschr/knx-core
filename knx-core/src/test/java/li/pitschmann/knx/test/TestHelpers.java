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

package li.pitschmann.knx.test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class TestHelpers {
    private TestHelpers() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Assert that the given {@link Class} is not instantiable and an {@link AssertionError} thrown is expected
     *
     * @param classToTest
     */
    public static void assertThatNotInstantiable(final Class<?> classToTest) {
        // DPT21Value should not be instantiable
        assertThatThrownBy(() -> {
            final var ctor = classToTest.getDeclaredConstructor();
            ctor.trySetAccessible();
            ctor.newInstance();
        }).hasCauseInstanceOf(AssertionError.class);
    }
}
