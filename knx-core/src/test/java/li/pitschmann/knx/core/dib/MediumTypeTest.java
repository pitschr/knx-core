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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link MediumType} enum class
 *
 * @author PITSCHR
 */
final class MediumTypeTest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(MediumType.values()).hasSize(4);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(MediumType.valueOf(0x02)).isEqualTo(MediumType.TP);
        assertThat(MediumType.valueOf(0x04)).isEqualTo(MediumType.PL110);
        assertThat(MediumType.valueOf(0x10)).isEqualTo(MediumType.RF);
        assertThat(MediumType.valueOf(0x20)).isEqualTo(MediumType.KNX_IP);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> MediumType.valueOf(0x00)).isInstanceOf(KnxEnumNotFoundException.class);
        assertThatThrownBy(() -> MediumType.valueOf(0xFF)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(MediumType.TP.getFriendlyName()).isEqualTo("Twisted Pair 1 (9600bit/s)");
        assertThat(MediumType.PL110.getFriendlyName()).isEqualTo("Power Line 110 (110 kHz)");
        assertThat(MediumType.RF.getFriendlyName()).isEqualTo("Radio Frequency (868 MHz)");
        assertThat(MediumType.KNX_IP.getFriendlyName()).isEqualTo("KNX IP");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        assertThat(MediumType.KNX_IP).hasToString(
                "MediumType{name=KNX_IP, friendlyName=KNX IP, code=32}"
        );
    }
}
