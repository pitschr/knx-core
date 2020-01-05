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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DataPointType}
 *
 * @author PITSCHR
 */
public class DataPointTypeTest {

    /**
     * Test {@link DataPointType#of(byte, byte...)} with one parameter
     */
    @Test
    @DisplayName("Tests the DataPointType#of(byte, byte...) with one parameter")
    public void testByteVararg() {
        final var dpt = mock(DataPointType.class);
        final var captor = ArgumentCaptor.forClass(byte[].class);

        // do real call because we want to test the implementation
        when(dpt.of(anyByte())).thenCallRealMethod();

        dpt.of((byte) 0x01);
        verify(dpt).of(captor.capture());
        assertThat(captor.getValue()).containsExactly(0x01);
    }

    /**
     * Test {@link DataPointType#of(byte, byte...)} with three parameters
     */
    @Test
    @DisplayName("Tests the DataPointType#of(byte, byte...) with three parameters")
    public void testByteVarargWithThreeParameters() {
        final var dpt = mock(DataPointType.class);
        final var captor = ArgumentCaptor.forClass(byte[].class);

        // do real call because we want to test the implementation
        when(dpt.of(anyByte(), anyByte(), anyByte())).thenCallRealMethod();

        dpt.of((byte) 0x03, (byte) 0x04, (byte) 0x05);
        verify(dpt).of(captor.capture());
        assertThat(captor.getValue()).containsExactly(0x03, 0x04, 0x05);
    }

    /**
     * Test {@link DataPointType#of(String, String...)} with one parameter
     */
    @Test
    @DisplayName("Tests the DataPointType#of(String, String...) with one parameter")
    public void testStringVararg() {
        final var dpt = mock(DataPointType.class);
        final var captor = ArgumentCaptor.forClass(String[].class);

        // do real call because we want to test the implementation
        when(dpt.of(anyString())).thenCallRealMethod();

        dpt.of("Foobar");
        verify(dpt).of(captor.capture());
        assertThat(captor.getValue()).containsExactly("Foobar");
    }

    /**
     * Test {@link DataPointType#of(String, String...)} with {@code null} as 1st parameter
     */
    @Test
    @DisplayName("Tests the DataPointType#of(String, String...) with null as 1st parameter")
    public void testStringVarargWithNull() {
        final var dpt = mock(DataPointType.class);

        // do real call because we want to test the implementation
        when(dpt.of((String) null)).thenCallRealMethod();

        assertThatThrownBy(() -> dpt.of((String) null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Test {@link DataPointType#of(String, String...)} with three parameters
     */
    @Test
    @DisplayName("Tests the DataPointType#of(String, String...) with three parameters")
    public void testStringVarargWithThreeParameters() {
        final var dpt = mock(DataPointType.class);
        final var captor = ArgumentCaptor.forClass(String[].class);

        // do real call because we want to test the implementation
        when(dpt.of(anyString(), anyString(), anyString())).thenCallRealMethod();

        dpt.of("Hello", "World", "!");
        verify(dpt).of(captor.capture());
        assertThat(captor.getValue()).containsExactly("Hello", "World", "!");
    }

    /**
     * Test {@link DataPointType#toByteArray(String, String...)}
     */
    @Test
    @DisplayName("Test DataPointType#toByteArray(String, String...)")
    public void testToByteArrayStringVararg() {
        // invalid cases - null as 1st parameter
        assertThatThrownBy(() -> DPT1.SWITCH.toByteArray(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DPT2.SWITCH_CONTROL.toByteArray(null, "true")).isInstanceOf(NullPointerException.class);

        // valid cases
        // 1 parameter
        assertThat(DPT1.SWITCH.toByteArray("false")).containsExactly(0x00);
        assertThat(DPT1.SWITCH.toByteArray("true")).containsExactly(0x01);

        // 2 parameters
        assertThat(DPT2.SWITCH_CONTROL.toByteArray("false")).containsExactly(0x00);
        assertThat(DPT2.SWITCH_CONTROL.toByteArray("true")).containsExactly(0x01);
        assertThat(DPT2.SWITCH_CONTROL.toByteArray("controlled", "false")).containsExactly(0x02);
        assertThat(DPT2.SWITCH_CONTROL.toByteArray("controlled", "true")).containsExactly(0x03);

        // 3 parameters
        // Saturday, 2013-08-17 04:10:45
        final var dayOfWeek = DayOfWeek.SATURDAY;
        final var date = LocalDate.of(2013, 8, 17);
        final var time = LocalTime.of(04, 10, 45);
        final var dateTimeBytes = DPT19.DATE_TIME.toByteArray(dayOfWeek, date, time);
        assertThat(DPT19.DATE_TIME.toByteArray("Saturday", "2013-08-17", "04:10:45")).containsExactly(dateTimeBytes);
    }
}
