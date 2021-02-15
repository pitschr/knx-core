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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link BroadcastType} enum class
 *
 * @author PITSCHR
 */
final class BroadcastTypeTest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(BroadcastType.values()).hasSize(2);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(BroadcastType.valueOf(0x00)).isEqualTo(BroadcastType.SYSTEM);
        assertThat(BroadcastType.valueOf(0x01)).isEqualTo(BroadcastType.NORMAL);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> BroadcastType.valueOf(-1)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(BroadcastType.SYSTEM.getFriendlyName()).isEqualTo("System Broadcast");
        assertThat(BroadcastType.NORMAL.getFriendlyName()).isEqualTo("Normal Broadcast");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        assertThat(BroadcastType.SYSTEM).hasToString("BroadcastType{name=SYSTEM, friendlyName=System Broadcast, code=0}");
        assertThat(BroadcastType.NORMAL).hasToString("BroadcastType{name=NORMAL, friendlyName=Normal Broadcast, code=1}");
    }
}
