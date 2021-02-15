/*
 * Copyright (C) 2021 Pitschmann Christoph
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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.datapoint.BaseDataPointType;
import li.pitschmann.knx.core.datapoint.DPT1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test {@link AbstractDataPointValue}
 *
 * @author PITSCHR
 */
class AbstractDataPointValueTest {

    @Test
    @DisplayName("Test #getValueAsText(Object)")
    void testValuesAsText_Object() {
        assertThat(AbstractDataPointValue.getValueAsText(null)).isEqualTo("null");

        final var helloWorldObjectMock = mock(Object.class);
        when(helloWorldObjectMock.toString()).thenReturn("Hello World!");
        assertThat(AbstractDataPointValue.getValueAsText(helloWorldObjectMock)).isEqualTo("Hello World!");
    }

    @Test
    @DisplayName("Test #getValueAsText(double)")
    void testValuesAsText_double() {
        assertThat(AbstractDataPointValue.getValueAsText(-3.40282348E38)).isEqualTo("-340282348000000000000000000000000000000");
        assertThat(AbstractDataPointValue.getValueAsText(12345.678901234567)).isEqualTo("12345.678901");
        assertThat(AbstractDataPointValue.getValueAsText(3.40282348E38)).isEqualTo("340282348000000000000000000000000000000");

        assertThat(AbstractDataPointValue.getValueAsText(45678.90f)).isEqualTo("45678.898438"); // float is not precise
    }

    @Test
    @DisplayName("Test #getValueAsText(int)")
    void testValuesAsText_int() {
        assertThat(AbstractDataPointValue.getValueAsText(Integer.MIN_VALUE)).isEqualTo("-2147483648");
        assertThat(AbstractDataPointValue.getValueAsText(4711)).isEqualTo("4711");
        assertThat(AbstractDataPointValue.getValueAsText(Integer.MAX_VALUE)).isEqualTo("2147483647");
    }

    @Test
    @DisplayName("Test #getValueAsText(long)")
    void testValuesAsText_long() {
        assertThat(AbstractDataPointValue.getValueAsText(Long.MIN_VALUE)).isEqualTo("-9223372036854775808");
        assertThat(AbstractDataPointValue.getValueAsText(1234567890123456789L)).isEqualTo("1234567890123456789");
        assertThat(AbstractDataPointValue.getValueAsText(Long.MAX_VALUE)).isEqualTo("9223372036854775807");
    }

    @Test
    @DisplayName("Test #isBitSet(byte, int)")
    void testIsBitSet_Byte() {
        final var b = (byte) 0b0110_0011;
        assertThat(AbstractDataPointValue.isBitSet(b, 0)).isTrue();  // .... ...1
        assertThat(AbstractDataPointValue.isBitSet(b, 1)).isTrue();  // .... ..1.
        assertThat(AbstractDataPointValue.isBitSet(b, 2)).isFalse(); // .... .0..
        assertThat(AbstractDataPointValue.isBitSet(b, 3)).isFalse(); // .... 0...
        assertThat(AbstractDataPointValue.isBitSet(b, 4)).isFalse(); // ...0 ....
        assertThat(AbstractDataPointValue.isBitSet(b, 5)).isTrue();  // ..1. ....
        assertThat(AbstractDataPointValue.isBitSet(b, 6)).isTrue();  // .1.. ....
        assertThat(AbstractDataPointValue.isBitSet(b, 7)).isFalse(); // 0... ....
    }

    @Test
    @DisplayName("Test #isBitSet(byte[], int)")
    void testIsBitSet_Bytes() {
        final var bytes = new byte[]{0b0110_0011, (byte) 0b1101_1101};
        assertThat(AbstractDataPointValue.isBitSet(bytes, 0)).isTrue();   // .... ...1 (of bytes[1])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 1)).isFalse();  // .... ..0. (of bytes[1])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 2)).isTrue();   // .... .1.. (of bytes[1])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 3)).isTrue();   // .... 1... (of bytes[1])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 4)).isTrue();   // ...1 .... (of bytes[1])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 5)).isFalse();  // ..0. .... (of bytes[1])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 6)).isTrue();   // .1.. .... (of bytes[1])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 7)).isTrue();   // 1... .... (of bytes[1])

        assertThat(AbstractDataPointValue.isBitSet(bytes, 8)).isTrue();   // .... ...1 (of bytes[0])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 9)).isTrue();   // .... ..1. (of bytes[0])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 10)).isFalse(); // .... .0.. (of bytes[0])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 11)).isFalse(); // .... 0... (of bytes[0])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 12)).isFalse(); // ...0 .... (of bytes[0])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 13)).isTrue();  // ..1. .... (of bytes[0])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 14)).isTrue();  // .1.. .... (of bytes[0])
        assertThat(AbstractDataPointValue.isBitSet(bytes, 15)).isFalse(); // 0... .... (of bytes[0])
    }

    @Test
    @DisplayName("Test constructor and initialization")
    void testConstructorAndInitialization() {
        // OK
        final var value = new TestDataPointValue(DPT1.SWITCH);
        assertThat(value.getDPT()).isEqualTo(DPT1.SWITCH);

        // NOK
        assertThatThrownBy(() -> new TestDataPointValue(null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Test {@link AbstractDataPointValue} class
     *
     * @author PITSCHR
     */
    private static class TestDataPointValue extends AbstractDataPointValue<BaseDataPointType<?>> {
        public TestDataPointValue(final BaseDataPointType<?> dpt) {
            super(dpt);
        }

        @Override
        public byte[] toByteArray() {
            throw new UnsupportedOperationException();
        }
    }
}
