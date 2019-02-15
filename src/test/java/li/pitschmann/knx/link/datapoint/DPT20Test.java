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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.test.TestHelpers;
import org.junit.jupiter.api.Test;

/**
 * Test Class for {@link DPT20}
 *
 * @author PITSCHR
 */
public class DPT20Test extends AbstractEnumDataPointTypeTest {
    @Override
    protected Class<?> currentEnumClass() {
        return DPT20.class;
    }

    @Test
    @Override
    public void testIdAndDescription() {
        // assertThat(DPT20.BuildingMode.BUILDING_IN_USE.getId()).isEqualTo("20.002");
        // assertThat(DPT20.BuildingMode.BUILDING_IN_USE.getDescription()).isEqualTo("Building Mode - Building in use");
        //
        // assertThat(DPT20.PBAction.INACTIVE.getId()).isEqualTo("20.606");
        // assertThat(DPT20.PBAction.INACTIVE.getDescription()).isEqualTo("PB Action - inactive (no message sent)");
    }

    @Test
    @Override
    public void testCompatibility() {
        // // failure
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("20.020", (byte[])
        // null)).isInstanceOf(IllegalArgumentException.class);
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("20.020", new
        // byte[0])).isInstanceOf(IllegalArgumentException.class);
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("20.020", new
        // byte[1])).isInstanceOf(KnxEnumNotFoundException.class);
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("20.020", new
        // byte[2])).isInstanceOf(IllegalArgumentException.class);
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("20.1000",
        // -1)).isInstanceOf(IllegalArgumentException.class);
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("20.1000",
        // 256)).isInstanceOf(IllegalArgumentException.class);
        //
        // // OK
        // assertThat((DataPointEnumValue<?>) ObsoleteDataPointContainer.toEnumValue("20.020", new byte[] { 0x01
        // })).isInstanceOf(DPT20Value.class);
        // assertThat((DataPointEnumValue<?>) ObsoleteDataPointContainer.toEnumValue("20.1000",
        // 0)).isInstanceOf(DPT20Value.class);
        // assertThat((DataPointEnumValue<?>) ObsoleteDataPointContainer.toEnumValue("20.1000",
        // 255)).isInstanceOf(DPT20Value.class);
    }

    @Test
    @Override
    public void testOf() {
        // assertThat((DataPointValue<?>)
        // ObsoleteDataPointContainer.toEnumValue(DPT20.ADAType.AIR_DAMPER)).isInstanceOf(DPT20Value.class);
        // final DataPointEnumDescriptor<ADAType> enumDescriptor =
        // ObsoleteDataPointContainer.getEnumDescriptor(DPT20.ADAType.AIR_DAMPER);
        // assertThat(enumDescriptor.getId()).isEqualTo("20.120");
        // assertThat(enumDescriptor.getDescription()).isEqualTo("ADA Type");
        //
        // assertThat((DataPointValue<?>)
        // ObsoleteDataPointContainer.toEnumValue(DPT20.SensorSelect.DIGITAL_INPUT_INVERTED)).isInstanceOf(DPT20Value.class);
        // final DataPointEnumDescriptor<SensorSelect> enumDescriptor2 =
        // ObsoleteDataPointContainer.getEnumDescriptor(DPT20.SensorSelect.DIGITAL_INPUT_INVERTED);
        // assertThat(enumDescriptor2.getId()).isEqualTo("20.017");
        // assertThat(enumDescriptor2.getDescription()).isEqualTo("Sensor Select");
    }

    /**
     * Test constructor of {@link DPT20}
     */
    @Test
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT20.class);
    }
}