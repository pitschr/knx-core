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

import li.pitschmann.knx.link.datapoint.DPT20;
import li.pitschmann.knx.link.datapoint.DPTEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPTEnumValue}
 *
 * @author PITSCHR
 */
public final class DPTEnumValueTest {
    /**
     * General DPT
     * <p>
     * Id and description is irrelevant for this test
     */
    private static final DPTEnum<DPT20.Priority> DPT_ENUM = new DPTEnum<>("123.456", "foobar");

    /**
     * Test {@link DPTEnumValue}
     */
    @Test
    public void test() {
        final var priorityHigh = new DPTEnumValue<DPT20.Priority>(DPT_ENUM, DPT20.Priority.HIGH, 0, "high");

        // instance methods
        assertThat(priorityHigh.getDPT()).isEqualTo(DPT_ENUM);
        assertThat(priorityHigh.getEnumField()).isEqualTo(DPT20.Priority.HIGH);
        assertThat(priorityHigh.getValue()).isEqualTo(0);
        assertThat(priorityHigh.getDescription()).isEqualTo("high");
        assertThat(priorityHigh.toByteArray()).containsExactly(0x00);
        assertThat(priorityHigh.toText()).isEqualTo("high");

        // equals (here we only compare DPT and enum field
        assertThat(priorityHigh).isEqualTo(priorityHigh);
        assertThat(priorityHigh).isEqualTo(new DPTEnumValue<>(DPT_ENUM, DPT20.Priority.HIGH, 0, "high"));
        assertThat(priorityHigh).isEqualTo(new DPTEnumValue<>(DPT_ENUM, DPT20.Priority.HIGH, 1, "nothigh"));
        assertThat(priorityHigh).isEqualTo(new DPTEnumValue<>(new DPTEnum<>("123.456", "foobar"), DPT20.Priority.HIGH, 0, "high"));
        assertThat(priorityHigh).hasSameHashCodeAs(priorityHigh);
        assertThat(priorityHigh).hasSameHashCodeAs(new DPTEnumValue<>(DPT_ENUM, DPT20.Priority.HIGH, 0, "high"));

        // not equals
        assertThat(priorityHigh).isNotEqualTo(null);
        // not equals because enum field is different
        assertThat(priorityHigh).isNotEqualTo(new DPTEnumValue<>(DPT_ENUM, DPT20.Priority.MEDIUM, 0, "high"));
        assertThat(priorityHigh).isNotEqualTo(new DPTEnumValue<>(DPT_ENUM, DPT20.Priority.MEDIUM, 1, "nothigh"));
        // not equals because DPT is different
        assertThat(priorityHigh).isNotEqualTo(new DPTEnumValue<>(new DPTEnum<>("456.123", "foobar"), DPT20.Priority.HIGH, 0, "high"));

        // toString
        final var toString = String.format("DPTEnumValue{dpt=%s, enumField=%s.HIGH, value=0, description=high, byteArray=0x00}", DPT_ENUM,
                DPT20.Priority.class.getName());
        assertThat(priorityHigh).hasToString(toString);
    }

    /**
     * Test {@link DPT9Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPTEnumValue<>(DPT_ENUM, DPT20.Priority.HIGH, -1, "high")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new DPTEnumValue<>(DPT_ENUM, DPT20.Priority.HIGH, 256, "high")).isInstanceOf(IllegalArgumentException.class);
    }
}
