/*
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

import li.pitschmann.knx.core.datapoint.DPT20.Priority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DataPointEnum}
 *
 * @author PITSCHR
 */
class DataPointEnumTest {

    @Test
    @DisplayName("Test the methods of enum value")
    void testEnum() {
        final var priorityHigh = Priority.HIGH.of();
        assertThat(priorityHigh.getId()).isEqualTo("20.004 - HIGH");
        assertThat(priorityHigh.getDescription()).isEqualTo("High");
        assertThat(priorityHigh.getFullDescription()).isEqualTo("Priority - High");
        assertThat(priorityHigh.getValue()).isEqualTo(0);
        assertThat(priorityHigh.toByteArray()).containsExactly(0x00);
        assertThat(priorityHigh.toText()).isEqualTo("High");
        assertThat(priorityHigh.getDPT().getId()).isEqualTo("20.004");
        assertThat(priorityHigh.getDPT().getDescription()).isEqualTo("Priority");

        final var priorityMedium = Priority.MEDIUM.of();
        assertThat(priorityMedium.getId()).isEqualTo("20.004 - MEDIUM");
        assertThat(priorityMedium.getDescription()).isEqualTo("Medium");
        assertThat(priorityMedium.getFullDescription()).isEqualTo("Priority - Medium");
        assertThat(priorityMedium.getValue()).isEqualTo(1);
        assertThat(priorityMedium.toByteArray()).containsExactly(0x01);
        assertThat(priorityMedium.toText()).isEqualTo("Medium");
        assertThat(priorityMedium.getDPT().getId()).isEqualTo("20.004");
        assertThat(priorityMedium.getDPT().getDescription()).isEqualTo("Priority");

        assertThat(priorityHigh.getDPT()).isSameAs(priorityMedium.getDPT());
    }

    @Test
    @DisplayName("Test the enum interfaces")
    void testInterfaces() {
        assertThat(Priority.HIGH.getId()).isEqualTo("20.004 - HIGH");
        assertThat(Priority.HIGH.getDescription()).isEqualTo("High");
        assertThat(Priority.HIGH.getFullDescription()).isEqualTo("Priority - High");
        assertThat(Priority.HIGH.getValue()).isEqualTo(0);
        assertThat(Priority.HIGH.toByteArray()).containsExactly(0x00);
        assertThat(Priority.HIGH.toText()).isEqualTo("High");
        assertThat(Priority.HIGH.getDPT().getId()).isEqualTo("20.004");
        assertThat(Priority.HIGH.getDPT().getDescription()).isEqualTo("Priority");

        assertThat(Priority.MEDIUM.getId()).isEqualTo("20.004 - MEDIUM");
        assertThat(Priority.MEDIUM.getDescription()).isEqualTo("Medium");
        assertThat(Priority.MEDIUM.getFullDescription()).isEqualTo("Priority - Medium");
        assertThat(Priority.MEDIUM.getValue()).isEqualTo(1);
        assertThat(Priority.MEDIUM.toByteArray()).containsExactly(0x01);
        assertThat(Priority.MEDIUM.toText()).isEqualTo("Medium");
        assertThat(Priority.MEDIUM.getDPT().getId()).isEqualTo("20.004");
        assertThat(Priority.MEDIUM.getDPT().getDescription()).isEqualTo("Priority");

        assertThat(Priority.HIGH.getDPT()).isSameAs(Priority.MEDIUM.getDPT());
    }
}
