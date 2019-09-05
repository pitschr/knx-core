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

package li.pitschmann.utils;

import li.pitschmann.knx.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for {@link ByteFormatter} class
 *
 * @author PITSCHR
 */
public class ByteFormatterTest {
    /**
     * Tests the {@link ByteFormatter#formatHex(byte)}
     */
    @Test
    public void formatHex() {
        assertThat(ByteFormatter.formatHex((byte) 0x00)).isEqualTo("0x00");
        assertThat(ByteFormatter.formatHex((byte) 0x20)).isEqualTo("0x20");
        assertThat(ByteFormatter.formatHex((byte) 0xff)).isEqualTo("0xFF");
    }

    /**
     * Tests the {@link ByteFormatter#formatHex(int)}
     */
    @Test
    public void formatHexInt() {
        // positive numbers
        assertThat(ByteFormatter.formatHex(0)).isEqualTo("0x00");
        assertThat(ByteFormatter.formatHex(32)).isEqualTo("0x20");
        assertThat(ByteFormatter.formatHex(4711)).isEqualTo("0x12 67");
        assertThat(ByteFormatter.formatHex(Integer.MAX_VALUE)).isEqualTo("0x7F FF FF FF");

        // negative number
        assertThatThrownBy(() -> ByteFormatter.formatHex(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests the {@link ByteFormatter#formatHex(byte[])}
     */
    @Test
    public void formatHexArray() {
        // null bytes
        assertThat(ByteFormatter.formatHex(null)).isEmpty();
        assertThat(ByteFormatter.formatHex(new byte[0])).isEmpty();

        // one byte
        assertThat(ByteFormatter.formatHex(new byte[]{0x00})).containsExactly("0x00");
        assertThat(ByteFormatter.formatHex(new byte[]{0x20})).containsExactly("0x20");
        assertThat(ByteFormatter.formatHex(new byte[]{(byte) 0xff})).containsExactly("0xFF");

        // two bytes
        assertThat(ByteFormatter.formatHex(new byte[]{0x00, 0x00})).containsExactly("0x00", "0x00");
        assertThat(ByteFormatter.formatHex(new byte[]{0x10, 0x20})).containsExactly("0x10", "0x20");
        assertThat(ByteFormatter.formatHex(new byte[]{(byte) 0xff, (byte) 0xff})).containsExactly("0xFF", "0xFF");

        // three bytes
        assertThat(ByteFormatter.formatHex(new byte[]{0x10, 0x20, 0x40})).containsExactly("0x10", "0x20", "0x40");
        assertThat(ByteFormatter.formatHex(new byte[]{(byte) 0xff, 0x00, 0x00})).containsExactly("0xFF", "0x00", "0x00");
    }

    /**
     * Tests the {@link ByteFormatter#formatHexAsString(byte[])} which also indirectly covers
     * {@link ByteFormatter#formatHexAsString(byte[], CharSequence)}
     */
    @Test
    public void formatHexArrayAsString() {
        // null bytes
        assertThat(ByteFormatter.formatHexAsString(null)).isEmpty();
        assertThat(ByteFormatter.formatHexAsString(new byte[0])).isEmpty();

        // one byte
        assertThat(ByteFormatter.formatHexAsString(new byte[]{0x00})).isEqualTo("0x00");
        assertThat(ByteFormatter.formatHexAsString(new byte[]{0x40})).isEqualTo("0x40");
        assertThat(ByteFormatter.formatHexAsString(new byte[]{(byte) 0xff})).isEqualTo("0xFF");

        // two bytes
        assertThat(ByteFormatter.formatHexAsString(new byte[]{0x00, 0x00})).isEqualTo("0x00 00");
        assertThat(ByteFormatter.formatHexAsString(new byte[]{0x10, 0x20})).isEqualTo("0x10 20");
        assertThat(ByteFormatter.formatHexAsString(new byte[]{(byte) 0xff, (byte) 0xff})).isEqualTo("0xFF FF");

        // three bytes
        assertThat(ByteFormatter.formatHexAsString(new byte[]{0x10, 0x20, 0x40})).isEqualTo("0x10 20 40");
        assertThat(ByteFormatter.formatHexAsString(new byte[]{(byte) 0xff, 0x00, 0x00})).isEqualTo("0xFF 00 00");

        // four bytes
        assertThat(ByteFormatter.formatHexAsString(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})).isEqualTo("0xFF FF FF FF");
    }

    /**
     * Test constructor of {@link ByteFormatter}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(ByteFormatter.class);
    }
}
