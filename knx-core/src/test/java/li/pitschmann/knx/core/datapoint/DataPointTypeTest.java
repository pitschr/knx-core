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
     * Test {@link DataPointType#toValue(byte, byte...)} with one parameter
     */
    @Test
    @DisplayName("Tests the DataPointType#toValue(byte, byte...) with one parameter")
    public void testToValueByteVararg() {
        final var dpt = mock(DataPointType.class);
        final var captor = ArgumentCaptor.forClass(byte[].class);

        // do real call because we want to test the implementation
        when(dpt.toValue(anyByte())).thenCallRealMethod();

        dpt.toValue((byte) 0x01);
        verify(dpt).toValue(captor.capture());
        assertThat(captor.getValue()).containsExactly(0x01);
    }

    /**
     * Test {@link DataPointType#toValue(byte, byte...)} with three parameters
     */
    @Test
    @DisplayName("Tests the DataPointType#toValue(byte, byte...) with three parameters")
    public void testToValueByteVarargWithThreeParameters() {
        final var dpt = mock(DataPointType.class);
        final var captor = ArgumentCaptor.forClass(byte[].class);

        // do real call because we want to test the implementation
        when(dpt.toValue(anyByte(), anyByte(), anyByte())).thenCallRealMethod();

        dpt.toValue((byte) 0x03, (byte) 0x04, (byte) 0x05);
        verify(dpt).toValue(captor.capture());
        assertThat(captor.getValue()).containsExactly(0x03, 0x04, 0x05);
    }

    /**
     * Test {@link DataPointType#toValue(String, String...)} with one parameter
     */
    @Test
    @DisplayName("Tests the DataPointType#toValue(String, String...) with one parameter")
    public void testToValueStringVararg() {
        final var dpt = mock(DataPointType.class);
        final var captor = ArgumentCaptor.forClass(String[].class);

        // do real call because we want to test the implementation
        when(dpt.toValue(anyString())).thenCallRealMethod();

        dpt.toValue("Foobar");
        verify(dpt).toValue(captor.capture());
        assertThat(captor.getValue()).containsExactly("Foobar");
    }

    /**
     * Test {@link DataPointType#toValue(String, String...)} with {@code null} as 1st parameter
     */
    @Test
    @DisplayName("Tests the DataPointType#toValue(String, String...) with null as 1st parameter")
    public void testToValueStringVarargWithNull() {
        final var dpt = mock(DataPointType.class);

        // do real call because we want to test the implementation
        when(dpt.toValue((String) null)).thenCallRealMethod();

        assertThatThrownBy(() -> dpt.toValue((String) null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Test {@link DataPointType#toValue(String, String...)} with three parameters
     */
    @Test
    @DisplayName("Tests the DataPointType#toValue(String, String...) with three parameters")
    public void testToValueStringVarargWithThreeParameters() {
        final var dpt = mock(DataPointType.class);
        final var captor = ArgumentCaptor.forClass(String[].class);

        // do real call because we want to test the implementation
        when(dpt.toValue(anyString(), anyString(), anyString())).thenCallRealMethod();

        dpt.toValue("Hello", "World", "!");
        verify(dpt).toValue(captor.capture());
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
