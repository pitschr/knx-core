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

package li.pitschmann.knx.core.utils;

import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test cases for {@link Preconditions} utility class
 *
 * @author PITSCHR
 */
public class PreconditionsTest {

    @Test
    @DisplayName("Test if object is non-null")
    public void testCheckNonNull() {
        final var objMock = mock(Object.class);
        when(objMock.toString()).thenReturn("Object{checkNonNull}");

        // no exceptions
        assertThat(Preconditions.checkNonNull(objMock)).isSameAs(objMock);
        assertThat(Preconditions.checkNonNull(objMock, "This is null.")).isSameAs(objMock);
        assertThat(Preconditions.checkNonNull(objMock, "This is null for: {}", new Object())).isSameAs(objMock);
        assertThat(Preconditions.checkNonNull(objMock, new Object()));

        // with exceptions
        assertThatThrownBy(() -> Preconditions.checkNonNull(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(null);

        assertThatThrownBy(() -> Preconditions.checkNonNull(null, "#checkNonNull"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("#checkNonNull");

        assertThatThrownBy(() -> Preconditions.checkNonNull(null, objMock))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null for: Object{checkNonNull}");

        assertThatThrownBy(() -> Preconditions.checkNonNull(null, "1: {}, 2: {}, 3: {}", "NULL", "nothing", 0d))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("1: NULL, 2: nothing, 3: 0.0");

        assertThatThrownBy(() -> Preconditions.checkNonNull(null, new StringBuilder("otherObject"), Integer.valueOf(13), Long.valueOf(17), Boolean.TRUE))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null for: otherObject. More Arguments: [13, 17, true]");
    }

    @Test
    @DisplayName("Test argument expression check")
    public void testCheckArguments() {
        final var objMock = mock(Object.class);
        when(objMock.toString()).thenReturn("Object{checkArgument}");

        // no exceptions
        Preconditions.checkArgument(true);
        Preconditions.checkArgument(true, "#checkArgument1");

        // with exceptions
        assertThatThrownBy(() -> Preconditions.checkArgument(false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(null);

        assertThatThrownBy(() -> Preconditions.checkArgument(false, "#checkArgument2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("#checkArgument2");

        assertThatThrownBy(() -> Preconditions.checkArgument(false, "1: {}, 2: {}, 3: {}", "FIRST", "SECOND", 345.67))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1: FIRST, 2: SECOND, 3: 345.67");

        assertThatThrownBy(() -> Preconditions.checkArgument(false, objMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Illegal Argument for: Object{checkArgument}");

        assertThatThrownBy(() -> Preconditions.checkArgument(false, objMock, 1, 2, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Illegal Argument for: Object{checkArgument}. More Arguments: [1, 2, 3]");

        assertThatThrownBy(() -> Preconditions.checkArgument(false, objMock, "one", "two", "three"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Illegal Argument for: Object{checkArgument}. More Arguments: [one, two, three]");
    }

    @Test
    @DisplayName("Test state expression check")
    public void testCheckState() {
        final var objMock = mock(Object.class);
        when(objMock.toString()).thenReturn("Object{checkState}");

        // no exceptions
        Preconditions.checkState(true);
        Preconditions.checkState(true, "#checkState1");

        // with exceptions
        assertThatThrownBy(() -> Preconditions.checkState(false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(null);

        assertThatThrownBy(() -> Preconditions.checkState(false, "#checkState2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("#checkState2");

        assertThatThrownBy(() -> Preconditions.checkState(false, "4: {}, 5: {}, 6: {}", "FOUR", "FIVE", 6789.01))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("4: FOUR, 5: FIVE, 6: 6789.01");

        assertThatThrownBy(() -> Preconditions.checkState(false, objMock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Illegal State for: Object{checkState}");


        assertThatThrownBy(() -> Preconditions.checkState(false, objMock, 1, 2, 3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Illegal State for: Object{checkState}. More Arguments: [1, 2, 3]");

        assertThatThrownBy(() -> Preconditions.checkState(false, objMock, "one", "two", "three"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Illegal State for: Object{checkState}. More Arguments: [one, two, three]");
    }

    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Preconditions.class);
    }
}
