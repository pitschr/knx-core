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

package li.pitschmann.knx.link;

import li.pitschmann.knx.link.exceptions.KnxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link AbstractSingleRawData}
 *
 * @author PITSCHR
 */
public class AbstractSingleRawDataTest {
    @Test
    public void testEquals() {
        TestSingleRawData testClass1 = new TestSingleRawData((byte) 0x11);
        TestSingleRawData testClass2 = new TestSingleRawData((byte) 0x11);
        TestSingleRawData testClass3 = new TestSingleRawData((byte) 0x22);
        AnotherTestSingleRawData anotherTestClass = new AnotherTestSingleRawData((byte) 0x11);

        // equals
        assertThat(testClass1).isEqualTo(testClass1);
        assertThat(testClass1).isEqualTo(testClass2);
        assertThat(testClass1).hasSameHashCodeAs(testClass2);
        assertThat(testClass1).hasToString(testClass2.toString());

        // not equals
        assertThat(testClass1).isNotEqualTo(null);
        assertThat(testClass1).isNotEqualTo(testClass3);
        assertThat(testClass1).isNotEqualTo(anotherTestClass);
    }

    /**
     * Test class
     *
     * @author PITSCHR
     */
    private static final class TestSingleRawData extends AbstractSingleRawData {
        protected TestSingleRawData(byte rawData) {
            super(rawData);
        }

        @Override
        protected void validate(byte rawData) throws KnxException {
            // NO-OP
        }

        @Override
        public String toString() {
            return "TestSingleRawData{" + this.getRawDataAsHexString() + "}";
        }
    }

    /**
     * Another Test class
     *
     * @author PITSCHR
     */
    private static final class AnotherTestSingleRawData extends AbstractSingleRawData {
        protected AnotherTestSingleRawData(byte rawData) {
            super(rawData);
        }

        @Override
        protected void validate(byte rawData) throws KnxException {
            // NO-OP
        }

        @Override
        public String toString() {
            return "AnotherTestSingleRawData{" + this.getRawDataAsHexString() + "}";
        }
    }
}
