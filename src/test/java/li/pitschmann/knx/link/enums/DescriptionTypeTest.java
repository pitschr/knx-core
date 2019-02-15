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

import li.pitschmann.knx.link.body.dib.DescriptionType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link DescriptionType} enum class
 *
 * @author PITSCHR
 */
public final class DescriptionTypeTest extends AbstractKnxByteEnumTest<DescriptionType> {
    @Override
    protected int numberOfElements() {
        return 6;
    }

    @Test
    @Override
    void validValueOf() {
        assertThat(DescriptionType.valueOf(0x01)).isEqualTo(DescriptionType.DEVICE_INFO);
        assertThat(DescriptionType.valueOf(0x02)).isEqualTo(DescriptionType.SUPPORTED_SERVICE_FAMILIES);
        assertThat(DescriptionType.valueOf(0x03)).isEqualTo(DescriptionType.IP_CONFIG);
        assertThat(DescriptionType.valueOf(0x04)).isEqualTo(DescriptionType.IP_CURRENT_CONFIG);
        assertThat(DescriptionType.valueOf(0x05)).isEqualTo(DescriptionType.KNX_ADDRESSES);
        assertThat(DescriptionType.valueOf(0xFE)).isEqualTo(DescriptionType.MANUFACTURER_DATA);
    }

    @Test
    @Override
    void friendlyName() {
        assertThat(DescriptionType.DEVICE_INFO.getFriendlyName()).isEqualTo("Device information");
        assertThat(DescriptionType.SUPPORTED_SERVICE_FAMILIES.getFriendlyName()).isEqualTo("Supported Service Families");
        assertThat(DescriptionType.IP_CONFIG.getFriendlyName()).isEqualTo("IP Configuration");
        assertThat(DescriptionType.IP_CURRENT_CONFIG.getFriendlyName()).isEqualTo("IP Current Configuration");
        assertThat(DescriptionType.KNX_ADDRESSES.getFriendlyName()).isEqualTo("KNX addresses");
        assertThat(DescriptionType.MANUFACTURER_DATA.getFriendlyName()).isEqualTo("Manufacturer Data");
    }

    @Test
    @Override
    public void testToString() {
        assertThat(DescriptionType.DEVICE_INFO).hasToString("DescriptionType{name=DEVICE_INFO, friendlyName=Device information, code=1 (0x01)}");
    }
}
