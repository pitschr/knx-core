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

package li.pitschmann.knx.core.enums;

import li.pitschmann.knx.core.body.cemi.TPCI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link TPCI} enum class
 *
 * @author PITSCHR
 */
public final class TPCITest extends AbstractKnxByteEnumTest<TPCI> {
    @Override
    protected int numberOfElements() {
        return 4;
    }

    @Test
    @Override
    void validValueOf() {
        assertThat(TPCI.valueOf(0x00)).isEqualTo(TPCI.UNNUMBERED_PACKAGE);
        assertThat(TPCI.valueOf(0x40)).isEqualTo(TPCI.NUMBERED_PACKAGE);
        assertThat(TPCI.valueOf(0x80)).isEqualTo(TPCI.UNNUMBERED_CONTROL_DATA);
        assertThat(TPCI.valueOf(0xC0)).isEqualTo(TPCI.NUMBERED_CONTROL_DATA);
    }

    @Test
    @Override
    void friendlyName() {
        assertThat(TPCI.UNNUMBERED_PACKAGE.getFriendlyName()).isEqualTo("UDT unnumbered package");
        assertThat(TPCI.NUMBERED_PACKAGE.getFriendlyName()).isEqualTo("NDT numbered package");
        assertThat(TPCI.UNNUMBERED_CONTROL_DATA.getFriendlyName()).isEqualTo("UCD unnumbered control data");
        assertThat(TPCI.NUMBERED_CONTROL_DATA.getFriendlyName()).isEqualTo("NCD numbered control data");
    }

    @Test
    @Override
    void testToString() {
        assertThat(TPCI.UNNUMBERED_PACKAGE).hasToString("TPCI{name=UNNUMBERED_PACKAGE, friendlyName=UDT unnumbered package, code=0 (0x00)}");
        assertThat(TPCI.NUMBERED_CONTROL_DATA)
                .hasToString("TPCI{name=NUMBERED_CONTROL_DATA, friendlyName=NCD numbered control data, code=192 (0xC0)}");
    }
}
