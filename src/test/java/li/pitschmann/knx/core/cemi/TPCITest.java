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
 * Verifies {@link TPCI} enum class
 *
 * @author PITSCHR
 */
public final class TPCITest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(TPCI.values()).hasSize(4);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(TPCI.valueOf(0x00)).isEqualTo(TPCI.UNNUMBERED_PACKAGE);
        assertThat(TPCI.valueOf(0x40)).isEqualTo(TPCI.NUMBERED_PACKAGE);
        assertThat(TPCI.valueOf(0x80)).isEqualTo(TPCI.UNNUMBERED_CONTROL_DATA);
        assertThat(TPCI.valueOf(0xC0)).isEqualTo(TPCI.NUMBERED_CONTROL_DATA);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> TPCI.valueOf(0xFF)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(TPCI.UNNUMBERED_PACKAGE.getFriendlyName()).isEqualTo("UDT unnumbered package");
        assertThat(TPCI.NUMBERED_PACKAGE.getFriendlyName()).isEqualTo("NDT numbered package");
        assertThat(TPCI.UNNUMBERED_CONTROL_DATA.getFriendlyName()).isEqualTo("UCD unnumbered control data");
        assertThat(TPCI.NUMBERED_CONTROL_DATA.getFriendlyName()).isEqualTo("NCD numbered control data");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        assertThat(TPCI.UNNUMBERED_PACKAGE).hasToString(
                "TPCI{name=UNNUMBERED_PACKAGE, friendlyName=UDT unnumbered package, code=0}"
        );

        assertThat(TPCI.NUMBERED_PACKAGE).hasToString(
                "TPCI{name=NUMBERED_PACKAGE, friendlyName=NDT numbered package, code=64}"
        );

        assertThat(TPCI.UNNUMBERED_CONTROL_DATA).hasToString(
                "TPCI{name=UNNUMBERED_CONTROL_DATA, friendlyName=UCD unnumbered control data, code=128}"
        );

        assertThat(TPCI.NUMBERED_CONTROL_DATA).hasToString(
                "TPCI{name=NUMBERED_CONTROL_DATA, friendlyName=NCD numbered control data, code=192}"
        );
    }
}
