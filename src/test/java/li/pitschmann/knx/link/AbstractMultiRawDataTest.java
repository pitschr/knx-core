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

import li.pitschmann.knx.link.exceptions.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for {@link AbstractMultiRawData}
 *
 * @author PITSCHR
 */
public class AbstractMultiRawDataTest {
    /**
     * Tests the {@link AbstractMultiRawData#equals(Object)} and {@link AbstractMultiRawData#hashCode()} methods.
     */
    @Test
    public void testEquals() {
        TestMultiRawData testClass1 = new TestMultiRawData(new byte[]{0x11, 0x12});
        TestMultiRawData testClass2 = new TestMultiRawData(new byte[]{0x11, 0x12});
        TestMultiRawData testClass3 = new TestMultiRawData(new byte[]{0x22, 0x12});
        AnotherTestMultiRawData anotherTestClass = new AnotherTestMultiRawData(new byte[]{0x11, 0x12});

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
    private static final class TestMultiRawData extends AbstractMultiRawData {
        protected TestMultiRawData(byte[] rawData) {
            super(rawData);
        }

        @Override
        protected void validate(byte[] rawData) throws KnxException {
            // NO-OP
        }

        @Override
        public String toString(boolean inclRawData) {
            return "TestMultiRawData{" + this.getRawDataAsHexString() + "}";
        }
    }

    /**
     * Another Test class
     *
     * @author PITSCHR
     */
    private static final class AnotherTestMultiRawData extends AbstractMultiRawData {
        protected AnotherTestMultiRawData(byte[] rawData) {
            super(rawData);
        }

        @Override
        protected void validate(byte[] rawData) throws KnxException {
            // NO-OP
        }

        @Override
        public String toString(boolean inclRawData) {
            return "AnotherTestMultiRawData{" + this.getRawDataAsHexString() + "}";
        }
    }
}
