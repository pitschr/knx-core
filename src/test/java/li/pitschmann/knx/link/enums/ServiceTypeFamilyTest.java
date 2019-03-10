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

import li.pitschmann.knx.link.body.dib.ServiceTypeFamily;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link ServiceTypeFamily} enum class
 *
 * @author PITSCHR
 */
public final class ServiceTypeFamilyTest extends AbstractKnxByteEnumTest<ServiceTypeFamily> {
    @Override
    protected int numberOfElements() {
        return 4;
    }

    @Test
    @Override
    void validValueOf() {
        assertThat(ServiceTypeFamily.valueOf(0x02)).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceTypeFamily.valueOf(0x03)).isEqualTo(ServiceTypeFamily.DEVICE_MANAGEMENT);
        assertThat(ServiceTypeFamily.valueOf(0x04)).isEqualTo(ServiceTypeFamily.TUNNELING);
        assertThat(ServiceTypeFamily.valueOf(0x05)).isEqualTo(ServiceTypeFamily.ROUTING);
    }

    @Test
    @Override
    void friendlyName() {
        assertThat(ServiceTypeFamily.CORE.getFriendlyName()).isEqualTo("KNX/IP Core");
        assertThat(ServiceTypeFamily.DEVICE_MANAGEMENT.getFriendlyName()).isEqualTo("KNX device Management");
        assertThat(ServiceTypeFamily.TUNNELING.getFriendlyName()).isEqualTo("KNX/IP Tunneling");
        assertThat(ServiceTypeFamily.ROUTING.getFriendlyName()).isEqualTo("KNX/IP Routing");
    }

    @Test
    @Override
    public void testToString() {
        assertThat(ServiceTypeFamily.CORE).hasToString("ServiceTypeFamily{name=CORE, friendlyName=KNX/IP Core, code=2 (0x02)}");
        assertThat(ServiceTypeFamily.TUNNELING).hasToString("ServiceTypeFamily{name=TUNNELING, friendlyName=KNX/IP Tunneling, code=4 (0x04)}");
    }
}
