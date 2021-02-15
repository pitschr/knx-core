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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DataPointEnumValue;
import li.pitschmann.knx.core.exceptions.KnxDataPointTypeNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DataPointRegistry}
 *
 * @author PITSCHR
 */
class DataPointRegistryTest {

    /**
     * Test {@link DataPointRegistry#getDataPointType(String)} for normal/static data point types
     */
    @Test
    void testGetDataPointType() {
        final DPT1 dpt1 = DataPointRegistry.getDataPointType("1.001");
        assertThat(dpt1).isEqualTo(DPT1.SWITCH);
        assertThat(DataPointRegistry.<DPT1>getDataPointType("1.001")).isEqualTo(DPT1.SWITCH);

        final DPT8 dpt8 = DataPointRegistry.getDataPointType("8.003");
        assertThat(dpt8).isEqualTo(DPT8.DELTA_TIME_10MS);
        assertThat(DataPointRegistry.<DPT8>getDataPointType("8.003")).isEqualTo(DPT8.DELTA_TIME_10MS);

        final DPT14 dpt14 = DataPointRegistry.getDataPointType("14.000");
        assertThat(dpt14).isEqualTo(DPT14.ACCELERATION);
        assertThat(DataPointRegistry.<DPT14>getDataPointType("14.000")).isEqualTo(DPT14.ACCELERATION);
    }

    /**
     * Test {@link DataPointRegistry#getDataPointType(String)} for enumerated data point types
     * <p>
     * {@link DPTEnum} is a wrapper class which is created by {@link DataPointRegistry} during class loading based
     * on {@link DataPoint} and {@link DataPointEnumValue} annotations. Therefore, we have no direct
     * access to the {@link DPTEnum} instance, however, due the nature of enumeration it will be a single instance and
     * therefore it will share same hash code and {@link #equals(Object)} will match.
     */
    @Test
    void testGetDataPointTypeEnum() {
        // check by assignment
        final DPTEnum<DPT20.LightApplicationMode> dpt20 = DataPointRegistry.getDataPointType("20.005");
        assertThat(dpt20.getId()).isEqualTo("20.005");
        assertThat(dpt20.getDescription()).isEqualTo("Light Application Mode");
        assertThat(dpt20.of(2).getEnum()).isEqualTo(DPT20.LightApplicationMode.NIGHT_ROUND);
        assertThat(dpt20.of(2).getValue()).isEqualTo(2);

        // direct check
        assertThat(DataPointRegistry.getDataPointType("20.005").getId()).isEqualTo("20.005");
        assertThat(DataPointRegistry.getDataPointType("20.005").getDescription()).isEqualTo("Light Application Mode");
        assertThat(DataPointRegistry.<DPTEnum<DPT20.LightApplicationMode>>getDataPointType("20.005").of(2).getEnum()).isEqualTo(DPT20.LightApplicationMode.NIGHT_ROUND);
        assertThat(DataPointRegistry.<DPTEnum<DPT20.LightApplicationMode>>getDataPointType("20.005").of(2).getValue()).isEqualTo(2);
        assertThat(DataPointRegistry.getDataPointType(DPT20.LightApplicationMode.PRESENCE_SIMULATION).getEnum()).isEqualTo(DPT20.LightApplicationMode.PRESENCE_SIMULATION);
        assertThat(DataPointRegistry.getDataPointType(DPT20.LightApplicationMode.PRESENCE_SIMULATION).getValue()).isEqualTo(1);

        // should match
        final DPTEnum<DPT20.LightApplicationMode> dpt20Created = new DPTEnum<>("20.005", "Light Application Mode");
        assertThat(dpt20).isEqualTo(dpt20Created);
        assertThat(dpt20).hasSameHashCodeAs(dpt20Created);
    }

    /**
     * Tests the non-existing data point type using {@link DataPointRegistry#getDataPointType(String)}
     */
    @Test
    void testGetDataPointTypeFailure() {
        assertThatThrownBy(() -> DataPointRegistry.getDataPointType(TestEnum.UNKNOWN)).isInstanceOf(KnxEnumNotFoundException.class)
                .hasMessage("Could not find enum data point type for: UNKNOWN");

        assertThatThrownBy(() -> DataPointRegistry.getDataPointType("UNKNOWN")).isInstanceOf(KnxDataPointTypeNotFoundException.class)
                .hasMessage("Could not find data point type id: UNKNOWN");
    }

    /**
     * Tries to register bad / not well-configured DPT classes
     */
    @Test
    void registerBadClasses() {
        // exception thrown because of clearly a wrong configuration
        assertThatThrownBy(() -> DataPointRegistry.registerDataPointType(DPTWithDuplicatedId.class))
                .isInstanceOf(AssertionError.class)
                .hasMessage("Please check the DPT implementation! Data Point Type key is already registered: 1.001");
        assertThatThrownBy(() -> DataPointRegistry.registerDataPointType(DPTWithWrongFieldType.class))
                .isInstanceOf(AssertionError.class)
                .hasMessage("Field is not an instance of DataPointType: li.pitschmann.knx.core.datapoint.DataPointRegistryTest$DPTWithWrongFieldType#WRONG_TYPE");
        assertThatThrownBy(() -> DataPointRegistry.registerDataPointType(DPTEnumWithDuplicatedId.class))
                .isInstanceOf(AssertionError.class)
                .hasMessage("Please check the DPT implementation! Data Point Enum key is already registered: 20.001");
        assertThatThrownBy(() -> DataPointRegistry.registerDataPointType(DPTEnumWithTwoSameValues.class))
                .isInstanceOf(KnxException.class)
                .hasMessage("Could not register enum field: BAR");

        // no exception thrown (data point type simply ignored)
        DataPointRegistry.registerDataPointType(DPTEnumWithoutValue.class);
        DataPointRegistry.registerDataPointType(DPTEnumWithoutInterface.class);
        DataPointRegistry.registerDataPointType(DPTEnumWithWrongInterface.class);
        DataPointRegistry.registerDataPointType(DPTEnumWithoutAnnotation.class);
        DataPointRegistry.registerDataPointType(DPTWithUnsupportedFields.class);
    }

    /**
     * Test constructor of {@link DataPointRegistry}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(DataPointRegistry.class);
    }

    /**
     * Test Enumeration Class for {@link #testGetDataPointTypeFailure()} test method.
     */
    private enum TestEnum implements DataPointEnum<TestEnum> {
        UNKNOWN
    }

    /*
     * DATA POINT TYPE TEST CLASSES
     */

    /*
     * Failing because '20.001' is already registered in DPT20 class
     */
    private static final class DPTEnumWithDuplicatedId {
        @DataPoint(value = "20.001", description = "DPT with already existing id.")
        public enum Empty implements DataPointEnum<Empty> {
            EMPTY
        }
    }

    /*
     * Failing because '1.001' is already registered in DPT1 class
     */
    private static final class DPTWithDuplicatedId {
        @DataPoint(value = "1.001", description = "")
        public static final DataPointType DATA_POINT_TYPE = mock(DataPointType.class);
    }

    /*
     * Failing because field should be a DataPointType, but is an
     * instance of String
     */
    private static final class DPTWithWrongFieldType {
        @DataPoint(value = "9999.003", description = "")
        public static final String WRONG_TYPE = "";
    }

    /*
     * Failing because we define value '0' twice times
     */
    private static final class DPTEnumWithTwoSameValues {
        @DataPoint(value = "", description = "DPT with two same values")
        public enum Number implements DataPointEnum<Number> {
            @DataPointEnumValue(value = 0, description = "foo")
            FOO, //
            @DataPointEnumValue(value = 0, description = "bar")
            BAR
        }
    }

    /*
     * Ignored because we do not have a value annotation defined
     */
    private static final class DPTEnumWithoutValue {
        @DataPoint(value = "9999.000", description = "DPT without value")
        public enum Empty implements DataPointEnum<Empty> {
            EMPTY
        }
    }

    /*
     * Ignored because we have no data point type enum interface defined (no interface)
     */
    private static final class DPTEnumWithoutInterface {
        @DataPoint(value = "9999.001", description = "DPT without interface")
        public enum Empty {
            EMPTY
        }
    }

    /*
     * Ignored because we have no data point type enum interface defined (wrong interface)
     */
    private static final class DPTEnumWithWrongInterface {
        @DataPoint(value = "9999.002", description = "DPT with wrong interface")
        public enum Empty implements Serializable {
            EMPTY
        }
    }

    /*
     * Ignored because we have no data point type enum annotation defined
     */
    private static final class DPTEnumWithoutAnnotation {
        @SuppressWarnings("unused")
        public enum Empty implements DataPointEnum<Empty> {
            EMPTY
        }
    }

    /*
     * Ignored because fields which haves all criteria are subject to be recognized
     * as data point types: public, static, final and {@link DataPoint} annotation
     */
    private static final class DPTWithUnsupportedFields {
        public static final Object NO_ANNOTATION = null; // no annotation
        @DataPoint(value = "9999.102", description = "")
        public static Object NO_FINAL = null; // no final
        @DataPoint(value = "9999.101", description = "")
        public final Object NO_STATIC = null; // no static
        @DataPoint(value = "9999.100", description = "")
        public Object NO_STATIC_FINAL = null; // no static, no final

        private DPTWithUnsupportedFields() {
            // dummy invoke to avoid automatic clean up by IDE
            NO_ANNOTATION.toString();
        }
    }
}

