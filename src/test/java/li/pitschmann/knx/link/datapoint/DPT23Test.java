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
 * Test Class for {@link DPT23}
 *
 * @author PITSCHR
 */
public class DPT23Test extends AbstractEnumDataPointTypeTest {
    @Override
    protected Class<?> currentEnumClass() {
        return DPT23.class;
    }

    @Test
    @Override
    public void testIdAndDescription() {
        // assertThat(DPT23.AlarmReaction.NO_ALARM.getId()).isEqualTo("23.002");
        // assertThat(DPT23.AlarmReaction.NO_ALARM.getDescription()).isEqualTo("Alarm Reaction - No Alarm");
        //
        // assertThat(DPT23.ActionOnOff.OFF_ON.getId()).isEqualTo("23.001");
        // assertThat(DPT23.ActionOnOff.OFF_ON.getDescription()).isEqualTo("Action On/Off - Off/On");
    }

    @Test
    @Override
    public void testCompatibility() {
        // // failure
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("23.001", (byte[])
        // null)).isInstanceOf(IllegalArgumentException.class);
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("23.001", new
        // byte[0])).isInstanceOf(IllegalArgumentException.class);
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("23.001", new
        // byte[2])).isInstanceOf(IllegalArgumentException.class);
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("23.001",
        // -1)).isInstanceOf(IllegalArgumentException.class);
        // assertThatThrownBy(() -> ObsoleteDataPointContainer.toEnumValue("23.001",
        // 256)).isInstanceOf(IllegalArgumentException.class);
        //
        // // OK
        // assertThat((DataPointEnumValue<?>) ObsoleteDataPointContainer.toEnumValue("23.001", new byte[] { 0x01
        // })).isInstanceOf(DPT23Value.class);
        // assertThat((DataPointEnumValue<?>) ObsoleteDataPointContainer.toEnumValue("23.001", new
        // byte[1])).isInstanceOf(DPT23Value.class);
        // assertThat((DataPointEnumValue<?>) ObsoleteDataPointContainer.toEnumValue("23.001",
        // 0)).isInstanceOf(DPT23Value.class);
    }

    @Test
    @Override
    public void testOf() {
        // assertThat((DataPointEnumValue<?>)
        // ObsoleteDataPointContainer.toEnumValue(DPT23.ActionOnOff.OFF)).isInstanceOf(DPT23Value.class);
        // final DataPointEnumDescriptor<ActionOnOff> enumDescriptor =
        // ObsoleteDataPointContainer.getEnumDescriptor(DPT23.ActionOnOff.OFF);
        // assertThat(enumDescriptor.getId()).isEqualTo("23.001");
        // assertThat(enumDescriptor.getDescription()).isEqualTo("Action On/Off");
        //
        // assertThat((DataPointEnumValue<?>)
        // ObsoleteDataPointContainer.toEnumValue(DPT23.ActionUpDown.UP_DOWN)).isInstanceOf(DPT23Value.class);
        // final DataPointEnumDescriptor<ActionUpDown> enumDescriptor2 =
        // ObsoleteDataPointContainer.getEnumDescriptor(DPT23.ActionUpDown.UP_DOWN);
        // assertThat(enumDescriptor2.getId()).isEqualTo("23.003");
        // assertThat(enumDescriptor2.getDescription()).isEqualTo("Action Up/Down");
    }

    /**
     * Test constructor of {@link DPT23}
     */
    @Test
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT23.class);
    }
}