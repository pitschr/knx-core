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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT1Value;
import li.pitschmann.knx.core.datapoint.value.DPT2Value;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link AbstractDataPointType}
 *
 * @author PITSCHR
 */
public abstract class AbstractDataPointTypeTest<D extends AbstractDataPointType<DV>, DV extends DataPointValue<?>> implements DPTTest {
    /**
     * General DPT test for {@link AbstractDataPointType#toValue(byte[])}.
     */
    @Test
    public void testGeneralCompatability() {
        final var dpt = DPT1.SWITCH;

        // general failures
        assertThatThrownBy(() -> dpt.toValue((byte[]) null)).isInstanceOf(KnxNullPointerException.class);
        assertThatThrownBy(() -> dpt.toValue(new byte[256])).isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    public void testTestDataPointTypeCompatibility() {
        final var dpt = new TestDataPointType();

        // parse OK
        assertThat(dpt.toValue(new String[]{"0xaa", "0xbb"})).isInstanceOf(DataPointValue.class);
        assertThat(dpt.toValue(new String[]{"0xaa", "0xbb", "0xcc", "0xdd"})).isInstanceOf(DataPointValue.class);

        // parse failures
        assertThatThrownBy(() -> dpt.toValue((String[]) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"xx", "yy"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"a", "b", "c"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
    }

    @Test
    public void testTestDataPointTypeCompatibilityNoStringSyntax() {
        final var dpt = new TestDataPointTypeNoStringSyntax();

        // parse (unsupported!)
        assertThatThrownBy(() -> dpt.toValue(new String[]{"0xaa", "0xbb"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
    }

    /**
     * Tests {@link AbstractDataPointType#getUnit()} and {@link AbstractDataPointType#getDescription()}
     */
    @Test
    public void testUnitAndDescriptions() {
        final var dptWithoutUnit = new TestDataPointType();
        assertThat(dptWithoutUnit.getUnit()).isNotNull().isEmpty(); // should not be null
        assertThat(dptWithoutUnit.getDescription()).isEqualTo("Description");

        final var dptWithUnit = new TestDataPointType("unit");
        assertThat(dptWithUnit.getUnit()).isEqualTo("unit");
        assertThat(dptWithUnit.getDescription()).isEqualTo("Description (unit)");
    }

    /**
     * Tests {@link DataPointType#equals(Object)} and {@link DataPointType#hashCode()}
     */
    @Test
    public void testEqualsAndHashCode() {
        // test equals
        assertThat(DPT1.ACK).isEqualTo(DPT1.ACK);
        assertThat(DPT1.ACK).isNotEqualTo(DPT1.ALARM);
        assertThat(DPT1.ACK).isNotEqualTo(null);

        // test hash code
        assertThat(DPT1.ACK.hashCode()).isEqualTo(DPT1.ACK.hashCode());
        assertThat(DPT1.ACK.hashCode()).isNotEqualTo(DPT1.ALARM.hashCode());
    }

    /**
     * Tests {@link DataPointType#toString()}
     */
    @Test
    public void testToString() {
        assertThat(DPT1.ACK.toString()).hasToString("DPT1{id=1.016, description=Acknowledge}");
    }

    /**
     * Asserts the {@link AbstractDataPointType} for given arguments {@code dpt}, {@code bValueArray} and
     * {@code dptValue}
     *
     * @param dpt
     * @param bValueArray
     * @param dptValue
     */
    protected void assertBaseDPT(final D dpt, final byte[] bValueArray, final DV dptValue) {
        // create by #of(byte[])
        final var baseOfValue = dpt.toValue(bValueArray);
        assertThat(baseOfValue).isEqualTo(dptValue);
        assertThat(dptValue.getDPT()).isEqualTo(dpt);

        // create by #toByteArray(..)
        assertThat(baseOfValue.toByteArray()).containsExactly(bValueArray);
    }

    /**
     * Completely implemented Test Data Point Type
     */
    private static class TestDataPointType extends AbstractDataPointType<DataPointValue<?>> {

        public TestDataPointType() {
            super("ID", "Description");
        }

        public TestDataPointType(final String unit) {
            super("ID", "Description", unit);
        }

        @Override
        protected boolean isCompatible(byte[] bytes) {
            if (bytes.length == 5) {
                return false;
            } else if (bytes.length == 3) {
                throw new IllegalArgumentException("Test Exception isCompatible(byte[])");
            }
            return true;
        }

        @Override
        protected boolean isCompatible(String[] args) {
            if (args.length == 2) {
                return false;
            } else if (args.length == 3) {
                throw new IllegalArgumentException("Test Exception isCompatible(String[])");
            }
            return true;
        }

        @Override
        protected DataPointValue<?> parse(byte[] bytes) {
            return new DPT1Value(DPT1.SWITCH, true);
        }

        @Override
        protected DataPointValue<?> parse(String[] args) {
            if (args.length == 2) {
                return new DPT1Value(DPT1.SWITCH, true);
            } else if (args.length == 4) {
                throw new IllegalArgumentException("Test exception parse(String[])");
            }
            return new DPT2Value(DPT2.SWITCH_CONTROL, false, true);
        }

    }

    /**
     * Test Data Point Type ( {@link #parse(String[])} is not implemented )
     */
    private static class TestDataPointTypeNoStringSyntax extends AbstractDataPointType<DataPointValue<?>> {
        public TestDataPointTypeNoStringSyntax() {
            super("ID w/o String Syntax", "Description w/o String Syntax");
        }

        @Override
        protected boolean isCompatible(byte[] bytes) {
            return false;
        }

        @Override
        protected boolean isCompatible(String[] args) {
            return true;
        }

        @Override
        protected DataPointValue<?> parse(byte[] bytes) {
            return null;
        }
    }
}
