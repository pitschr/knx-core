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

package li.pitschmann.knx.link.body.dib;

import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link AbstractDIB}
 *
 * @author PITSCHR
 */
public final class AbstractDIBTest {
    private static final byte[] BYTES = new byte[]{ //
            0x02, // Structure Length
            0x01 // Description Type Code
    };

    /**
     * Tests {@link AbstractDIB}
     */
    @Test
    public void valueOf() {
        // new instance
        TestDIB abstractDIB = new TestDIB(BYTES);

        // compare
        assertThat(abstractDIB.getLength()).isEqualTo(2);
        assertThat(abstractDIB.getDescriptionType()).isEqualTo(DescriptionType.DEVICE_INFO);
    }

    /**
     * Tests {@link AbstractDIB} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> new TestDIB(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");

        // incorrect size of bytes
        assertThatThrownBy(() -> new TestDIB(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> new TestDIB(new byte[]{0x01})).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> new TestDIB(new byte[]{0x03, 0x00, 0x00, 0x00})).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");

    }

    /**
     * Test {@link AbstractDIB#toString()}
     */
    @Test
    public void testToString() {
        assertThat(new TestDIB(BYTES)).hasToString(String.format("TestDIB{length=2 (0x02), descriptionType=%s, rawData=%s}",
                DescriptionType.DEVICE_INFO, ByteFormatter.formatHexAsString(BYTES)));
    }

    /**
     * Test Class for {@link AbstractDIB}
     */
    private static final class TestDIB extends AbstractDIB {

        protected TestDIB(byte[] rawData) {
            super(rawData);
        }

    }
}
