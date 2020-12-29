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

import li.pitschmann.knx.core.datapoint.value.DPT1Value;
import li.pitschmann.knx.core.datapoint.value.DPT2Value;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
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
 * Tests for {@link BaseDataPointType}
 *
 * @author PITSCHR
 */
public class BaseDataPointTypeTest {

    /**
     * General DPT test for {@link BaseDataPointType#of(byte[])}.
     */
    @Test
    public void testGeneralCompatibility() {
        final var dpt = DPT1.SWITCH;

        // general failures
        assertThatThrownBy(() -> dpt.of((byte[]) null)).isInstanceOf(KnxNullPointerException.class);
        assertThatThrownBy(() -> dpt.of(new byte[256])).isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    public void testTestDataPointTypeCompatibility() {
        final var dpt = new TestDataPointType();

        // parse OK
        assertThat(dpt.of(new String[]{"0xaa", "0xbb"})).isInstanceOf(DataPointValue.class);
        assertThat(dpt.of(new String[]{"0xaa", "0xbb", "0xcc", "0xdd"})).isInstanceOf(DataPointValue.class);

        // parse failures
        assertThatThrownBy(() -> dpt.of((String[]) null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> dpt.of(new String[0])).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> dpt.of(new String[]{"xx", "yy"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
    }

    @Test
    public void testTestDataPointTypeCompatibilityNoStringSyntax() {
        final var dpt = new TestDataPointTypeNoStringSyntax();

        // parse (unsupported!)
        assertThatThrownBy(() -> dpt.of(new String[]{"0xaa", "0xbb"})).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
    }

    /**
     * Tests {@link BaseDataPointType#getUnit()} and {@link BaseDataPointType#getDescription()}
     */
    @Test
    public void testUnitAndDescriptions() {
        final var dptWithoutUnit = new TestDataPointType();
        assertThat(dptWithoutUnit.getUnit()).isNotNull().isEmpty(); // should not be null
        assertThat(dptWithoutUnit.getDescription()).isEqualTo("Description");

        final var dptWithUnit = new TestDataPointType("unit");
        assertThat(dptWithUnit.getUnit()).isEqualTo("unit");
        assertThat(dptWithUnit.getDescription()).isEqualTo("Description (unit)");
    }

    /**
     * Test {@link BaseDataPointType#of(byte, byte...)} with one parameter
     */
    @Test
    @DisplayName("Tests the #of(byte, byte...) with one parameter")
    public void testByteVararg() {
        final var dpt = mock(BaseDataPointType.class);
        final var captor = ArgumentCaptor.forClass(byte[].class);

        // do real call because we want to test the implementation
        when(dpt.of(anyByte())).thenCallRealMethod();

        dpt.of((byte) 0x01);
        verify(dpt).of(captor.capture());
        assertThat(captor.getValue()).containsExactly(0x01);
    }

    /**
     * Test {@link BaseDataPointType#of(byte, byte...)} with three parameters
     */
    @Test
    @DisplayName("Tests the #of(byte, byte...) with three parameters")
    public void testByteVarargWithThreeParameters() {
        final var dpt = mock(BaseDataPointType.class);
        final var captor = ArgumentCaptor.forClass(byte[].class);

        // do real call because we want to test the implementation
        when(dpt.of(anyByte(), anyByte(), anyByte())).thenCallRealMethod();

        dpt.of((byte) 0x03, (byte) 0x04, (byte) 0x05);
        verify(dpt).of(captor.capture());
        assertThat(captor.getValue()).containsExactly(0x03, 0x04, 0x05);
    }

    /**
     * Test {@link BaseDataPointType#of(String, String...)} with {@code null} as 1st parameter
     */
    @Test
    @DisplayName("Tests the #of(String, String...) with null as 1st parameter")
    public void testStringVarargWithNull() {
        final var dpt = mock(BaseDataPointType.class);

        // do real call because we want to test the implementation
        when(dpt.of((String) null)).thenCallRealMethod();

        assertThatThrownBy(() -> dpt.of((String) null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Test {@link BaseDataPointType#of(String, String...)} with one parameter
     */
    @Test
    @DisplayName("Tests the #of(String, String...) with one parameter")
    public void testStringVararg() {
        final var dpt = mock(BaseDataPointType.class);
        final var captor = ArgumentCaptor.forClass(String[].class);

        // do real call because we want to test the implementation
        when(dpt.of(anyString())).thenCallRealMethod();

        dpt.of("Foobar");
        verify(dpt).of(captor.capture());
        assertThat(captor.getValue()).containsExactly("Foobar");
    }

    /**
     * Test {@link BaseDataPointType#of(String, String...)} with three parameters
     */
    @Test
    @DisplayName("Tests the #of(String, String...) with three parameters")
    public void testStringVarargWithThreeParameters() {
        final var dpt = mock(BaseDataPointType.class);
        final var captor = ArgumentCaptor.forClass(String[].class);

        // do real call because we want to test the implementation
        when(dpt.of(anyString(), anyString(), anyString())).thenCallRealMethod();

        dpt.of("Hello", "World", "!");
        verify(dpt).of(captor.capture());
        assertThat(captor.getValue()).containsExactly("Hello", "World", "!");
    }


    /**
     * Test {@link BaseDataPointType#toByteArray(String, String...)}
     */
    @Test
    @DisplayName("Test #toByteArray(String, String...)")
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

    /**
     * Completely implemented Test Data Point Type
     */
    private static class TestDataPointType extends BaseDataPointType<DataPointValue> {

        public TestDataPointType() {
            this(null);
        }

        public TestDataPointType(final String unit) {
            super("Description", unit);
        }

        @Override
        protected boolean isCompatible(byte[] bytes) {
            if (bytes.length == 5) {
                return false;
            } else if (bytes.length == 3) {
                throw new IllegalArgumentException("Test Exception isCompatible(byte[])");
            }
            return true;
        }

        @Override
        protected boolean isCompatible(String[] args) {
            return args.length != 2 && args.length != 4;
        }

        @Override
        protected DataPointValue parse(byte[] bytes) {
            return new DPT1Value(DPT1.SWITCH, true);
        }

        @Override
        protected DataPointValue parse(String[] args) {
            if (args.length == 2) {
                return new DPT1Value(DPT1.SWITCH, true);
            } else if (args.length == 4) {
                throw new IllegalArgumentException("Test exception parse(String[])");
            }
            return new DPT2Value(DPT2.SWITCH_CONTROL, false, true);
        }

    }

    /**
     * Test Data Point Type ( {@link BaseDataPointType#parse(String[])} is not implemented )
     */
    private static class TestDataPointTypeNoStringSyntax extends BaseDataPointType<DataPointValue> {
        public TestDataPointTypeNoStringSyntax() {
            super("ID w/o String Syntax", "Description w/o String Syntax");
        }

        @Override
        protected boolean isCompatible(byte[] bytes) {
            return false;
        }

        @Override
        protected boolean isCompatible(String[] args) {
            return true;
        }

        @Override
        protected DataPointValue parse(byte[] bytes) {
            return null;
        }
    }
}
