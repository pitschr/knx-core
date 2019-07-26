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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.knx.link.datapoint.DPT20.Priority;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DataPointTypeEnum}
 *
 * @author PITSCHR
 */
public class DataPointTypeEnumTest {

    /**
     * Test {@link DataPointTypeEnum} if default methods behaves correctly.
     */
    @Test
    public void test() {
        // using an enumeration that is using DataPointTypeEnum
        final var priorityValue = Priority.HIGH.toValue();
        assertThat(priorityValue).isNotNull();
        assertThat(priorityValue.getDPT().getId()).isEqualTo("20.004");
        assertThat(priorityValue.getDPT().getDescription()).isEqualTo("Priority");
        assertThat(priorityValue.getDescription()).isEqualTo("High");
        assertThat(priorityValue.getEnumField()).isEqualTo(Priority.HIGH);
        assertThat(priorityValue.getValue()).isEqualTo(0);

        // interface methods
        assertThat(Priority.HIGH.getId()).isEqualTo("20.004 - HIGH");
        assertThat(Priority.HIGH.getDescription()).isEqualTo("Priority - High");
        assertThat(Priority.HIGH.toByteArray()).containsExactly(0x00);

        assertThat(Priority.MEDIUM.getId()).isEqualTo("20.004 - MEDIUM");
        assertThat(Priority.MEDIUM.getDescription()).isEqualTo("Priority - Medium");
        assertThat(Priority.MEDIUM.toByteArray()).containsExactly(0x01);
    }
}