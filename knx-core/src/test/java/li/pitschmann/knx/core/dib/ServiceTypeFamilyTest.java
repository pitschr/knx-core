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

import li.pitschmann.knx.core.AbstractKnxByteEnumTest;
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
        return 7;
    }

    @Test
    @Override
    public void validValueOf() {
        assertThat(ServiceTypeFamily.valueOf(0x02)).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceTypeFamily.valueOf(0x03)).isEqualTo(ServiceTypeFamily.DEVICE_MANAGEMENT);
        assertThat(ServiceTypeFamily.valueOf(0x04)).isEqualTo(ServiceTypeFamily.TUNNELING);
        assertThat(ServiceTypeFamily.valueOf(0x05)).isEqualTo(ServiceTypeFamily.ROUTING);
        assertThat(ServiceTypeFamily.valueOf(0x06)).isEqualTo(ServiceTypeFamily.REMOTE_LOGGING);
        assertThat(ServiceTypeFamily.valueOf(0x07)).isEqualTo(ServiceTypeFamily.REMOTE_CONFIGURATION_AND_DIAGNOSIS);
        assertThat(ServiceTypeFamily.valueOf(0x08)).isEqualTo(ServiceTypeFamily.OBJECT_SERVER);
    }

    @Test
    @Override
    public void friendlyName() {
        assertThat(ServiceTypeFamily.CORE.getFriendlyName()).isEqualTo("KNX/IP Core");
        assertThat(ServiceTypeFamily.DEVICE_MANAGEMENT.getFriendlyName()).isEqualTo("KNX device Management");
        assertThat(ServiceTypeFamily.TUNNELING.getFriendlyName()).isEqualTo("KNX/IP Tunneling");
        assertThat(ServiceTypeFamily.ROUTING.getFriendlyName()).isEqualTo("KNX/IP Routing");
        assertThat(ServiceTypeFamily.REMOTE_LOGGING.getFriendlyName()).isEqualTo("KNX/IP Remote Logging");
        assertThat(ServiceTypeFamily.REMOTE_CONFIGURATION_AND_DIAGNOSIS.getFriendlyName()).isEqualTo("KNX/IP Remote Configuration and Diagnosis");
        assertThat(ServiceTypeFamily.OBJECT_SERVER.getFriendlyName()).isEqualTo("KNX/IP Object Server");
    }

    @Test
    @Override
    public void testToString() {
        assertThat(ServiceTypeFamily.CORE).hasToString("ServiceTypeFamily{name=CORE, friendlyName=KNX/IP Core, code=2 (0x02)}");
        assertThat(ServiceTypeFamily.TUNNELING).hasToString("ServiceTypeFamily{name=TUNNELING, friendlyName=KNX/IP Tunneling, code=4 (0x04)}");
    }
}
