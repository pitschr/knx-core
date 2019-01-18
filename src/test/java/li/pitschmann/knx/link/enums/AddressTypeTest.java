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

package li.pitschmann.knx.link.enums;

import li.pitschmann.knx.link.body.address.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Verifies {@link AddressType} enum class
 *
 * @author PITSCHR
 */
public final class AddressTypeTest extends AbstractKnxByteEnumTest<AddressType> {
    @Override
    protected int numberOfElements() {
        return 2;
    }

    @Test
    @Override
    void validValueOf() {
        assertThat(AddressType.valueOf(0x00)).isEqualTo(AddressType.INDIVIDUAL);
        assertThat(AddressType.valueOf(0x01)).isEqualTo(AddressType.GROUP);
    }

    @Test
    @Override
    void friendlyName() {
        assertThat(AddressType.INDIVIDUAL.getFriendlyName()).isEqualTo("Individual Address");
        assertThat(AddressType.GROUP.getFriendlyName()).isEqualTo("Group Address");
    }

    @Test
    @Override
    public void testToString() {
        assertThat(AddressType.INDIVIDUAL).hasToString("AddressType{name=INDIVIDUAL, friendlyName=Individual Address, code=0 (0x00)}");
        assertThat(AddressType.GROUP).hasToString("AddressType{name=GROUP, friendlyName=Group Address, code=1 (0x01)}");
    }
}
