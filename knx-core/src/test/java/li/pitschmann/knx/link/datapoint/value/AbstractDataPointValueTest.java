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

package li.pitschmann.knx.link.datapoint.value;

import li.pitschmann.knx.link.datapoint.AbstractDataPointType;
import li.pitschmann.knx.link.datapoint.DPT1;
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
public class AbstractDataPointValueTest {
    /**
     * Test {@link AbstractDataPointValue}
     */
    @Test
    public void test() {
        TestDataPointValue value1 = new TestDataPointValue(DPT1.SWITCH);
        TestDataPointValue value2 = new TestDataPointValue(DPT1.SWITCH);

        // values
        assertThat(value1.getDPT()).isEqualTo(DPT1.SWITCH);

        // equals + hashcode not implemented
        assertThat(value1).isNotEqualTo(null);
        assertThat(value1).isEqualTo(value1);
        assertThat(value2).isNotEqualTo(value1);
        assertThat(value2.hashCode()).isNotEqualTo(value1.hashCode());
    }

    /**
     * Test {@link AbstractDataPointValue#getValueAsText(Object)} methods.
     */
    @Test
    public void testValuesAsText() {
        assertThat(AbstractDataPointValue.getValueAsText(null)).isEqualTo("null");
        assertThat(AbstractDataPointValue.getValueAsText(12345.678901234567d)).isEqualTo("12345.678901");
        assertThat(AbstractDataPointValue.getValueAsText(45678.90f)).isEqualTo("45678.898438"); // float is not precise
        assertThat(AbstractDataPointValue.getValueAsText(1234567890)).isEqualTo("1234567890");
        assertThat(AbstractDataPointValue.getValueAsText(1234567890123456789L)).isEqualTo("1234567890123456789");

        final var helloWorldObjectMock = mock(Object.class);
        when(helloWorldObjectMock.toString()).thenReturn("Hello World!");
        assertThat(AbstractDataPointValue.getValueAsText(helloWorldObjectMock)).isEqualTo("Hello World!");
    }

    /**
     * Test failures of {@link AbstractDataPointValue}
     */
    @Test
    public void testFailures() {
        assertThatThrownBy(() -> new TestDataPointValue(null)).isInstanceOf(NullPointerException.class);
    }

    /**
     * Test {@link AbstractDataPointValue} class
     *
     * @author PITSCHR
     */
    private static class TestDataPointValue extends AbstractDataPointValue<AbstractDataPointType<?>> {
        public TestDataPointValue(AbstractDataPointType<?> dpt) {
            super(dpt);
        }

        @Override
        public byte[] toByteArray() {
            throw new UnsupportedOperationException();
        }
    }
}
