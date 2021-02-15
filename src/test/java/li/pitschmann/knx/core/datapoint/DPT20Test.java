/*
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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class for {@link DPT20}
 *
 * @author PITSCHR
 */
class DPT20Test {
    @Test
    @DisplayName("DPT20 Constructor not instantiable")
    void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DPT20.class);
    }

    @Test
    @DisplayName("ActuatorConnectType.SENSOR")
    void testActuatorConnectType_Sensor() {
        final var valueSensor = DPT20.ActuatorConnectType.SENSOR;

        // short access (via of())
        assertThat(valueSensor.ordinal()).isEqualTo(0);
        assertThat(valueSensor.getId()).isEqualTo("20.020 - SENSOR");
        assertThat(valueSensor.getDescription()).isEqualTo("Sensor Connection");
        assertThat(valueSensor.getFullDescription()).isEqualTo("Actuator Connect Type - Sensor Connection");
        assertThat(valueSensor.getValue()).isEqualTo(1);
        assertThat(valueSensor.toByteArray()).containsExactly(0x01);
        assertThat(valueSensor.toText()).isEqualTo("Sensor Connection");

        // access vial DPTEnumValue
        assertThat(valueSensor.of().getValue()).isEqualTo(1);
        assertThat(valueSensor.of().getEnum()).isSameAs(DPT20.ActuatorConnectType.SENSOR);

        // access via DPTEnum (value)
        assertThat(valueSensor.getDPT().of(1).getEnum()).isEqualTo(DPT20.ActuatorConnectType.SENSOR);
    }

    @Test
    @DisplayName("ActuatorConnectType.CONTROLLER")
    void testActuatorConnectType_Controller() {
        final var valueSensor = DPT20.ActuatorConnectType.CONTROLLER;

        // short access (via of())
        assertThat(valueSensor.ordinal()).isEqualTo(1);
        assertThat(valueSensor.getId()).isEqualTo("20.020 - CONTROLLER");
        assertThat(valueSensor.getDescription()).isEqualTo("Controller Connection");
        assertThat(valueSensor.getFullDescription()).isEqualTo("Actuator Connect Type - Controller Connection");
        assertThat(valueSensor.getValue()).isEqualTo(2);
        assertThat(valueSensor.toByteArray()).containsExactly(0x02);
        assertThat(valueSensor.toText()).isEqualTo("Controller Connection");

        // access vial DPTEnumValue
        assertThat(valueSensor.of().getValue()).isEqualTo(2);
        assertThat(valueSensor.of().getEnum()).isSameAs(DPT20.ActuatorConnectType.CONTROLLER);

        // access via DPTEnum (value)
        assertThat(valueSensor.getDPT().of(2).getEnum()).isEqualTo(DPT20.ActuatorConnectType.CONTROLLER);
    }
}
