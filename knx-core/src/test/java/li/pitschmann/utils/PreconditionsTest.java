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

package li.pitschmann.utils;

import li.pitschmann.knx.test.TestHelpers;
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

        // no exceptions
        assertThat(Preconditions.checkNonNull(objMock)).isSameAs(objMock);
        assertThat(Preconditions.checkNonNull(objMock, "This is not null.")).isSameAs(objMock);

        // with exceptions
        assertThatThrownBy(() -> Preconditions.checkNonNull(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(null);

        assertThatThrownBy(() -> Preconditions.checkNonNull(null, "#checkNonNull"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("#checkNonNull");
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

        assertThatThrownBy(() -> Preconditions.checkArgument(false, "1: {}, 2: %s, 3: %.2f", "FIRST", "SECOND", 345.67))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1: FIRST, 2: SECOND, 3: 345.67");

        assertThatThrownBy(() -> Preconditions.checkArgument(false, objMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Illegal Argument for: Object{checkArgument}. More Arguments: []");

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

        assertThatThrownBy(() -> Preconditions.checkState(false, "4: {}, 5: %s, 6: %.2f", "FOUR", "FIVE", 6789.01))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("4: FOUR, 5: FIVE, 6: 6789.01");

        assertThatThrownBy(() -> Preconditions.checkState(false, objMock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Illegal State for: Object{checkState}. More Arguments: []");

        assertThatThrownBy(() -> Preconditions.checkState(false, objMock, "one", "two", "three"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Illegal State for: Object{checkState}. More Arguments: [one, two, three]");
    }

    @Test
    @DisplayName("Test format using #toErrorMessage(..)")
    public void testToErrorMessage() {
        // standard formatting
        assertThat(Preconditions.toErrorMessage("My Message")).isEqualTo("My Message");
        assertThat(Preconditions.toErrorMessage("{} {}!", "Hello", "World")).isEqualTo("Hello World!");
        assertThat(Preconditions.toErrorMessage("%s %s!", "Hello", "Earth")).isEqualTo("Hello Earth!");
        assertThat(Preconditions.toErrorMessage("1+2 = %d", 1 + 2)).isEqualTo("1+2 = 3");
        assertThat(Preconditions.toErrorMessage("My Message", "Hallo", "Erde")).isEqualTo("My Message (Arguments: [Hallo, Erde])");

        // customized formatting
        final var bytes = new byte[]{(byte) 0xFF, (byte) 0xCE, 0x56};
        assertThat(Preconditions.toErrorMessage("Bytes: {}", bytes)).isEqualTo("Bytes: 0xFF CE 56");
        assertThat(Preconditions.toErrorMessage("Null: {}", (Object) null)).isEqualTo("Null: <null>");
        assertThat(Preconditions.toErrorMessage("Null: {}", (Object[]) null)).isEqualTo("Null: <null>");
        assertThat(Preconditions.toErrorMessage("Null: {} {}", null, null)).isEqualTo("Null: <null> <null>");
    }

    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Preconditions.class);
    }
}
