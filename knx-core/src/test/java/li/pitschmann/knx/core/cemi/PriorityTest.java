/*
 * KNX Link - A library for KNX Net/IP communication
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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link Priority} enum class
 *
 * @author PITSCHR
 */
public final class PriorityTest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(Priority.values()).hasSize(4);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(Priority.valueOf(0x00)).isEqualTo(Priority.SYSTEM);
        assertThat(Priority.valueOf(0x01)).isEqualTo(Priority.NORMAL);
        assertThat(Priority.valueOf(0x02)).isEqualTo(Priority.URGENT);
        assertThat(Priority.valueOf(0x03)).isEqualTo(Priority.LOW);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> Priority.valueOf(0x04)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(Priority.SYSTEM.getFriendlyName()).isEqualTo("System Priority");
        assertThat(Priority.NORMAL.getFriendlyName()).isEqualTo("Normal Priority");
        assertThat(Priority.URGENT.getFriendlyName()).isEqualTo("Urgent Priority");
        assertThat(Priority.LOW.getFriendlyName()).isEqualTo("Low Priority");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        assertThat(Priority.SYSTEM).hasToString("Priority{name=SYSTEM, friendlyName=System Priority, code=0}");
        assertThat(Priority.NORMAL).hasToString("Priority{name=NORMAL, friendlyName=Normal Priority, code=1}");
        assertThat(Priority.URGENT).hasToString("Priority{name=URGENT, friendlyName=Urgent Priority, code=2}");
        assertThat(Priority.LOW).hasToString("Priority{name=LOW, friendlyName=Low Priority, code=3}");

    }
}
