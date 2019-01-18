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

import li.pitschmann.test.*;
import li.pitschmann.utils.Bytes.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test cases for {@link ByteFormatter} class
 *
 * @author PITSCHR
 */
public class BytesTest {
    /**
     * Test {@link Bytes#toByteArrayWithCapacity(byte[], int)}
     */
    @Test
    public void testToByteArrayWithCapacity() {
        // null
        assertThat(Bytes.toByteArrayWithCapacity(null, 1)).containsExactly(0x00);
        assertThat(Bytes.toByteArrayWithCapacity(null, 2)).containsExactly(0x00, 0x00);
        assertThat(Bytes.toByteArrayWithCapacity(null, 4)).containsExactly(0x00, 0x00, 0x00, 0x00);
        // same capacity
        assertThat(Bytes.toByteArrayWithCapacity(new byte[]{0x00, 0x00, 0x00, 0x00}, 4)).containsExactly(0x00, 0x00, 0x00, 0x00);
        assertThat(Bytes.toByteArrayWithCapacity(new byte[]{0x11, 0x22, 0x33, 0x44}, 4)).containsExactly(0x11, 0x22, 0x33, 0x44);
        // array byte is bigger than capacity - success
        assertThat(Bytes.toByteArrayWithCapacity(new byte[]{0x00, 0x00, 0x00, 0x00}, 2)).containsExactly(0x00, 0x00);
        assertThat(Bytes.toByteArrayWithCapacity(new byte[]{0x00, 0x00, 0x55, 0x66}, 2)).containsExactly(0x55, 0x66);
        // array byte is bigger than capacity - fail
        assertThatThrownBy(() -> Bytes.toByteArrayWithCapacity(new byte[]{0x01, 0x00}, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Bytes.toByteArrayWithCapacity(new byte[]{0x01, 0x00, 0x00}, 2)).isInstanceOf(IllegalArgumentException.class);
        // array byte is smaller than capacity
        assertThat(Bytes.toByteArrayWithCapacity(new byte[0], 1)).containsExactly(0x00);
        assertThat(Bytes.toByteArrayWithCapacity(new byte[]{0x11, 0x22, 0x33}, 4)).containsExactly(0x00, 0x11, 0x22, 0x33);
    }

    /**
     * Test {@link Bytes#fillByteArray(byte[], byte[], li.pitschmann.utils.Bytes.FillDirection)}
     */
    @Test
    public void fillByteArray() {
        // fill from left to right
        assertThat(Bytes.fillByteArray(new byte[2], new byte[]{0x11, 0x22}, FillDirection.LEFT_TO_RIGHT)).containsExactly(0x11, 0x22);
        assertThat(Bytes.fillByteArray(new byte[4], new byte[]{0x11, 0x22}, FillDirection.LEFT_TO_RIGHT)).containsExactly(0x11, 0x22, 0x00, 0x00);
        // fill from right to left
        assertThat(Bytes.fillByteArray(new byte[2], new byte[]{0x33, 0x44}, FillDirection.RIGHT_TO_LEFT)).containsExactly(0x33, 0x44);
        assertThat(Bytes.fillByteArray(new byte[4], new byte[]{0x33, 0x44}, FillDirection.RIGHT_TO_LEFT)).containsExactly(0x00, 0x00, 0x33, 0x44);

        // error when template array is smaller than bytes
        assertThatThrownBy(() -> Bytes.fillByteArray(new byte[1], new byte[]{0x55, 0x66}, FillDirection.LEFT_TO_RIGHT))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Length of bytes cannot exceed the template array capacity.");
        // error when no fill direction defined
        assertThatThrownBy(() -> Bytes.fillByteArray(new byte[3], new byte[]{0x55, 0x66}, null)).isInstanceOf(UnsupportedOperationException.class);
    }

    /**
     * Test {@link Bytes#toSignedShort(byte, byte...)}  and {@link Bytes#toSignedShort(byte[])}}
     */
    @Test
    public void testToSignedShort() {
        // zero
        assertThat(Bytes.toSignedShort((byte) 0x00)).isZero();
        assertThat(Bytes.toSignedShort((byte) 0x00, (byte) 0x00)).isZero();
        assertThat(Bytes.toSignedShort(new byte[]{(byte) 0x00, (byte) 0x00})).isZero();
        // 0xff, 0x7f
        assertThat(Bytes.toSignedShort((byte) 0xff)).isEqualTo((short) -1);
        assertThat(Bytes.toSignedShort((byte) 0x7f)).isEqualTo((short) 127);
        // max
        assertThat(Bytes.toSignedShort((byte) 0x7f, (byte) 0xff)).isEqualTo(Short.MAX_VALUE);
        assertThat(Bytes.toSignedShort(new byte[]{(byte) 0x7f, (byte) 0xff})).isEqualTo(Short.MAX_VALUE);
        // min
        assertThat(Bytes.toSignedShort((byte) 0x80, (byte) 0x00)).isEqualTo(Short.MIN_VALUE);
        assertThat(Bytes.toSignedShort(new byte[]{(byte) 0x80, (byte) 0x00})).isEqualTo(Short.MIN_VALUE);
    }

    /**
     * Test {@link Bytes#toUnsignedShort(byte, byte...)} and {@link Bytes#toUnsignedShort(byte[])}
     */
    @Test
    public void testToUnsignedShort() {
        // zero
        assertThat(Bytes.toUnsignedShort((byte) 0x00)).isZero();
        assertThat(Bytes.toUnsignedShort((byte) 0x00, (byte) 0x00)).isZero();
        assertThat(Bytes.toUnsignedShort(new byte[]{(byte) 0x00, (byte) 0x00})).isZero();
        // max
        assertThat(Bytes.toUnsignedShort((byte) 0x7f, (byte) 0xff)).isEqualTo(Short.MAX_VALUE);
        assertThat(Bytes.toUnsignedShort(new byte[]{(byte) 0x7f, (byte) 0xff})).isEqualTo(Short.MAX_VALUE);
        // fail because it exceeds Short#MAX_VALUE
        assertThatThrownBy(() -> Bytes.toUnsignedShort((byte) 0x80, (byte) 0x00)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds Short#MAX_VALUE");
        assertThatThrownBy(() -> Bytes.toUnsignedShort(new byte[]{(byte) 0x80, (byte) 0x00})).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds Short#MAX_VALUE");
    }

    /**
     * Test {@link Bytes#toSignedInt(byte, byte...)} and {@link Bytes#toSignedInt(byte[])}
     */
    @Test
    public void testToSignedInt() {
        // zero
        assertThat(Bytes.toSignedInt((byte) 0x00)).isZero();
        assertThat(Bytes.toSignedInt((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00)).isZero();
        assertThat(Bytes.toSignedInt(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00})).isZero();
        // max
        assertThat(Bytes.toSignedInt((byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff)).isEqualTo(Integer.MAX_VALUE);
        assertThat(Bytes.toSignedInt(new byte[]{(byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff})).isEqualTo(Integer.MAX_VALUE);
        // min
        assertThat(Bytes.toSignedInt((byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00)).isEqualTo(Integer.MIN_VALUE);
        assertThat(Bytes.toSignedInt(new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00})).isEqualTo(Integer.MIN_VALUE);
    }

    /**
     * Test {@link Bytes#toUnsignedInt(byte, byte...)}, {@link Bytes#toUnsignedInt(byte, byte, byte, byte)}
     * and {@link Bytes#toUnsignedInt(byte[])}
     */
    @Test
    public void testToUnsignedInt() {
        // zero
        assertThat(Bytes.toUnsignedInt((byte) 0x00)).isZero();
        assertThat(Bytes.toUnsignedInt((byte) 0x00, (byte) 0x00)).isZero();
        assertThat(Bytes.toUnsignedInt((byte) 0x00, (byte) 0x00, (byte) 0x00)).isZero();
        assertThat(Bytes.toUnsignedInt((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00)).isZero();
        assertThat(Bytes.toUnsignedInt(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00})).isZero();
        // 0xff
        assertThat(Bytes.toUnsignedInt((byte) 0xff)).isEqualTo(255);
        assertThat(Bytes.toUnsignedInt((byte) 0x00, (byte) 0xff)).isEqualTo(255);
        assertThat(Bytes.toUnsignedInt(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff})).isEqualTo(255);
        // 0xff ff
        assertThat(Bytes.toUnsignedInt((byte) 0xff, (byte) 0xff)).isEqualTo(65535);
        assertThat(Bytes.toUnsignedInt((byte) 0x00, (byte) 0xff, (byte) 0xff)).isEqualTo(65535);
        assertThat(Bytes.toUnsignedInt(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xff})).isEqualTo(65535);
        // 0xff ff ff
        assertThat(Bytes.toUnsignedInt((byte) 0xff, (byte) 0xff, (byte) 0xff)).isEqualTo(16777215);
        assertThat(Bytes.toUnsignedInt((byte) 0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff)).isEqualTo(16777215);
        assertThat(Bytes.toUnsignedInt(new byte[]{(byte) 0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff})).isEqualTo(16777215);
        // 0xff ff ff ff
        assertThat(Bytes.toUnsignedInt((byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff)).isEqualTo(Integer.MAX_VALUE);
        assertThat(Bytes.toUnsignedInt(new byte[]{(byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff})).isEqualTo(Integer.MAX_VALUE);

        // fail because it exceeds Integer#MAX_VALUE
        assertThatThrownBy(() -> Bytes.toUnsignedInt((byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds Integer#MAX_VALUE");
        assertThatThrownBy(() -> Bytes.toUnsignedInt(new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00}))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("exceeds Integer#MAX_VALUE");
    }

    /**
     * Test {@link Bytes#toSignedLong(byte, byte...)}, {@link Bytes#toSignedLong(byte, byte, byte, byte, byte, byte, byte, byte)}
     * and {@link Bytes#toSignedLong(byte[])}
     */
    @Test
    public void testToSignedLong() {
        // zero
        assertThat(Bytes.toSignedLong((byte) 0x00)).isZero();
        assertThat(Bytes.toSignedLong((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00))
                .isZero();
        assertThat(Bytes.toSignedLong(
                new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}))
                .isZero();
        // max
        assertThat(Bytes.toSignedLong((byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff))
                .isEqualTo(Long.MAX_VALUE);
        assertThat(Bytes.toSignedLong((byte) 0x00, (byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff)).isEqualTo(Long.MAX_VALUE);
        assertThat(Bytes.toSignedLong(
                new byte[]{(byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}))
                .isEqualTo(Long.MAX_VALUE);
        // min
        assertThat(Bytes.toSignedLong((byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00))
                .isEqualTo(Long.MIN_VALUE);
        assertThat(Bytes.toSignedLong((byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00)).isEqualTo(Long.MIN_VALUE);
        assertThat(Bytes.toSignedLong(
                new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}))
                .isEqualTo(Long.MIN_VALUE);
        assertThat(Bytes.toSignedLong(new byte[]{(byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00})).isEqualTo(Long.MIN_VALUE);
    }

    /**
     * Test {@link Bytes#toUnsignedLong(byte, byte...)}, {@link Bytes#toUnsignedLong(byte, byte, byte, byte, byte, byte, byte, byte)}
     * and {@link Bytes#toUnsignedLong(byte[])}
     */
    @Test
    public void testToUnsignedLong() {
        // zero
        assertThat(Bytes.toUnsignedLong((byte) 0x00)).isZero();
        assertThat(Bytes.toUnsignedLong((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00))
                .isZero();
        assertThat(Bytes.toUnsignedLong(
                new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}))
                .isZero();
        // max
        assertThat(Bytes.toUnsignedLong( //
                (byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, //
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff //
        )).isEqualTo(Long.MAX_VALUE);
        assertThat(Bytes.toUnsignedLong( //
                (byte) 0x00, (byte) 0x7f, (byte) 0xff, (byte) 0xff, //
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, //
                (byte) 0xff //
        )).isEqualTo(Long.MAX_VALUE);
        assertThat(Bytes.toUnsignedLong(new byte[]{ //
                (byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, //
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff} //
        )).isEqualTo(Long.MAX_VALUE);
        assertThat(Bytes.toUnsignedLong(new byte[]{ //
                (byte) 0x00, (byte) 0x7f, (byte) 0xff, (byte) 0xff, //
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, //
                (byte) 0xff} //
        )).isEqualTo(Long.MAX_VALUE);

        // fail because it exceeds Long#MAX_VALUE
        assertThatThrownBy(() -> Bytes.toUnsignedLong((byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("exceeds Long#MAX_VALUE");
    }

    /**
     * Test {@link Bytes#toByteArray(String)}
     */
    @Test
    public void testHexStringToByteArray() {
        // zero bytes
        assertThat(Bytes.toByteArray("")).isEmpty();

        // valid entries
        assertThat(Bytes.toByteArray("00")).containsExactly(0x00);
        assertThat(Bytes.toByteArray("FF")).containsExactly(0xFF);
        assertThat(Bytes.toByteArray("0000")).containsExactly(0x00, 0x00);
        assertThat(Bytes.toByteArray("FFFF")).containsExactly(0xFF, 0xFF);
        assertThat(Bytes.toByteArray("00FF")).containsExactly(0x00, 0xFF);
        assertThat(Bytes.toByteArray("FF00")).containsExactly(0xFF, 0x00);
        assertThat(Bytes.toByteArray("0123456789ABCDEF")).containsExactly(0x01, 0x23, 0x45, 0x67, 0x89, 0xAB, 0xCD, 0xEF);
        assertThat(Bytes.toByteArray("fedcba9876543210")).containsExactly(0xfe, 0xdc, 0xba, 0x98, 0x76, 0x54, 0x32, 0x10);
        assertThat(Bytes.toByteArray("0x00")).containsExactly(0x00);
        assertThat(Bytes.toByteArray("0xFF")).containsExactly(0xFF);
        assertThat(Bytes.toByteArray("0x0000")).containsExactly(0x00, 0x00);
        assertThat(Bytes.toByteArray("0xFFFF")).containsExactly(0xFF, 0xFF);
        assertThat(Bytes.toByteArray("0x00 FF")).containsExactly(0x00, 0xFF);
        assertThat(Bytes.toByteArray("0xFF 00")).containsExactly(0xFF, 0x00);

        // null
        assertThatThrownBy(() -> Bytes.toByteArray((String) null)).isInstanceOf(NullPointerException.class);
        // invalid format
        assertThatThrownBy(() -> Bytes.toByteArray("0")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Bytes.toByteArray("000")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Bytes.toByteArray("-1")).isInstanceOf(IllegalArgumentException.class);
        // invalid characters (only 0-9, A-F are accepted)
        assertThatThrownBy(() -> Bytes.toByteArray("  ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Bytes.toByteArray("GG")).isInstanceOf(IllegalArgumentException.class);
        // incomplete hex string
        assertThatThrownBy(() -> Bytes.toByteArray("0x")).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test {@link Bytes#trimRight(byte[])} and {@link Bytes#trimRight(byte[], byte)}
     */
    @Test
    public void testTrimRight() {
        // empty
        assertThat(Bytes.trimRight(new byte[0])).isEmpty();
        // no trim because 0x00 is not present
        assertThat(Bytes.trimRight(new byte[]{0x11, 0x22, 0x33})).containsExactly(0x11, 0x22, 0x33);
        // trim multiple 0x00 on right side
        assertThat(Bytes.trimRight(new byte[]{0x11, 0x22, 0x33, 0x00, 0x00, 0x00, 0x00})).containsExactly(0x11, 0x22, 0x33);
        // trim last 0x00 on right side
        assertThat(Bytes.trimRight(new byte[]{0x11, 0x22, 0x33, 0x00, 0x00, 0x44, 0x00})).containsExactly(0x11, 0x22, 0x33, 0x00, 0x00, 0x44);
        // trim last 0x00 on right side
        assertThat(Bytes.trimRight(new byte[]{0x11, 0x22, 0x33, 0x00, 0x00, 0x44, 0x55})).containsExactly(0x11, 0x22, 0x33, 0x00, 0x00, 0x44,
                0x55);

        // empty
        assertThat(Bytes.trimRight(new byte[0], (byte) 0x11)).isEmpty();
        // no trim because 0x11 is not present
        assertThat(Bytes.trimRight(new byte[2], (byte) 0x11)).containsExactly(new byte[2]);
        // trim 0x11 on most right side
        assertThat(Bytes.trimRight(new byte[]{0x00, 0x00, 0x11}, (byte) 0x11)).containsExactly(0x00, 0x00);
    }

    /**
     * Test {@link Bytes#padRight(byte[], byte, int)}
     */
    @Test
    public void testPadRight() {
        byte[] bytes = new byte[]{0x11, 0x22, 0x33};

        // given capacity is smaller
        assertThatThrownBy(() -> Bytes.padRight(bytes, (byte) 0x44, 1)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Capacity cannot be smaller than 3 (actual: 1)");

        // fill with 0x00 - given capacity equals
        assertThat(Bytes.padRight(new byte[0], (byte) 0x00, 0)).isEmpty();
        assertThat(Bytes.padRight(bytes, (byte) 0x00, 3)).containsExactly(bytes);
        // fill with 0x00 - given capacity is larger
        assertThat(Bytes.padRight(new byte[0], (byte) 0x00, 3)).containsExactly(new byte[3]);
        assertThat(Bytes.padRight(bytes, (byte) 0x00, 5)).containsExactly(0x11, 0x22, 0x33, 0x00, 0x00);

        // fill with 0x44 - given capacity equals
        assertThat(Bytes.padRight(new byte[0], (byte) 0x44, 0)).isEmpty();
        assertThat(Bytes.padRight(bytes, (byte) 0x44, 3)).containsExactly(bytes);
        // fill with 0x44 - given capacity is larger
        assertThat(Bytes.padRight(new byte[0], (byte) 0x44, 3)).containsExactly(0x44, 0x44, 0x44);
        assertThat(Bytes.padRight(bytes, (byte) 0x44, 5)).containsExactly(0x11, 0x22, 0x33, 0x44, 0x44);
    }

    /**
     * Test {@link Bytes#toByte(boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)}
     */
    @Test
    public void testToByte() {
        // 0000 0000
        assertThat(Bytes.toByte(false, false, false, false, false, false, false, false)).isEqualTo((byte) 0x00);
        // 0000 1111
        assertThat(Bytes.toByte(false, false, false, false, true, true, true, true)).isEqualTo((byte) 0x0F);
        // 1111 0000
        assertThat(Bytes.toByte(true, true, true, true, false, false, false, false)).isEqualTo((byte) 0xF0);
        // 1111 1111
        assertThat(Bytes.toByte(true, true, true, true, true, true, true, true)).isEqualTo((byte) 0xFF);

        // 1010 0101
        assertThat(Bytes.toByte(true, false, true, false, false, true, false, true)).isEqualTo((byte) 0xA5);
        // 0110 1011
        assertThat(Bytes.toByte(false, true, true, false, true, false, true, true)).isEqualTo((byte) 0x6B);
        // 0001 0110
        assertThat(Bytes.toByte(false, false, false, true, false, true, true, false)).isEqualTo((byte) 0x16);
        // 1001 1100
        assertThat(Bytes.toByte(true, false, false, true, true, true, false, false)).isEqualTo((byte) 0x9C);
    }

    /**
     * Test {@link Bytes#toByte(boolean, boolean...)}
     */
    @Test
    public void testToByteVararg() {
        // 0000 0000
        assertThat(Bytes.toByte(false)).isEqualTo((byte) 0x00);
        // 0000 0001
        assertThat(Bytes.toByte(true)).isEqualTo((byte) 0x01);
        // 0000 1111
        assertThat(Bytes.toByte(true, true, true, true)).isEqualTo((byte) 0x0F);

        // 0110 1011
        assertThat(Bytes.toByte(true, true, false, true, false, true, true)).isEqualTo((byte) 0x6B);
        // 0001 0110
        assertThat(Bytes.toByte(true, false, true, true, false)).isEqualTo((byte) 0x16);
        // 0011 1100
        assertThat(Bytes.toByte(true, true, true, true, false, false)).isEqualTo((byte) 0x3C);
        // 1000 0000
        assertThat(Bytes.toByte(true, new boolean[7])).isEqualTo((byte) 0x80);

        // failures
        assertThatThrownBy(() -> Bytes.toByte(true, new boolean[8])).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("You can provide only up to 8 booleans for byte (actual: 9)");
    }

    /**
     * Test {@link Bytes#toByteArray(boolean...)}
     */
    @Test
    public void testToByteArray() {
        // ------------------------
        // Complete bytes
        // ------------------------
        // 0-byte
        assertThat(Bytes.toByteArray()).isEmpty();
        // 1-byte
        assertThat(Bytes.toByteArray(true, false, false, true, true, true, false, false)).containsExactly((byte) 0x9C);
        assertThat(Bytes.toByteArray(false, true, true, false, true, false, true, true)).containsExactly((byte) 0x6B);
        // 2-bytes
        assertThat(Bytes.toByteArray(//
                true, false, false, true, true, true, false, false, // 0x9C
                false, false, false, true, false, true, true, false // 0x16
        )).containsExactly(0x9C, 0x16);
        assertThat(Bytes.toByteArray(//
                true, false, true, false, false, true, false, true, // 0xA5
                false, true, true, false, true, false, true, true // 0x6B
        )).containsExactly(0xA5, 0x6B);
        // 3-bytes
        assertThat(Bytes.toByteArray(//
                false, true, true, false, true, false, true, true, // 0x6B
                true, false, true, false, false, true, false, true, // 0xA5
                false, false, false, true, false, true, true, false // 0x16
        )).containsExactly(0x6B, 0xA5, 0x16);
        // 4-bytes
        assertThat(Bytes.toByteArray(//
                true, true, true, true, false, false, false, false, // 0xF0
                true, false, false, true, true, true, false, false, // 0x9C
                false, false, false, false, true, true, true, true, // 0x0F
                true, true, true, true, true, true, true, true // 0xFF
        )).containsExactly(0xF0, 0x9C, 0x0F, 0xFF);

        // ------------------------
        // In-Complete bytes
        // ------------------------
        // 1-byte
        assertThat(Bytes.toByteArray(/* false, false, false, false, false, false, */true, false)).containsExactly(0x02);
        assertThat(Bytes.toByteArray(/* false, false, false, false, */true, false, true, false)).containsExactly(0x0A);
        assertThat(Bytes.toByteArray(/* false, false, */true, false, true, false, true, false)).containsExactly(0x2A);
        assertThat(Bytes.toByteArray(/* false, */ true, true, false, true, false, true, false)).containsExactly(0x6A);
        // 2-bytes
        assertThat(Bytes.toByteArray( //
                /* false, false, false, false, false, false, false, */true, // 0x01
                false, true, true, false, true, false, true, false // 0x6A
        )).containsExactly(0x01, 0x6A);
        assertThat(Bytes.toByteArray( //
                /* false, */true, false, false, false, false, false, false, // 0x40
                true, false, false, true, true, true, false, false // 0x9C
        )).containsExactly(0x40, 0x9C);
        // 3-bytes
        assertThat(Bytes.toByteArray( //
                /* false, false, false, false, */ true, false, true, true, // 0x0B
                true, true, true, true, true, true, true, true, // 0xFF
                true, false, false, true, true, true, false, false // 0x9C
        )).containsExactly(0x0B, 0xFF, 0x9C);
    }

    /**
     * Test {@link Bytes#toInt(boolean, boolean...)}
     */
    @Test
    public void testToInt() {
        // one bit
        assertThat(Bytes.toInt(false)).isEqualTo(0);
        assertThat(Bytes.toInt(true)).isEqualTo(1);
        // two bits
        assertThat(Bytes.toInt(false, false)).isEqualTo(0);
        assertThat(Bytes.toInt(false, true)).isEqualTo(1);
        assertThat(Bytes.toInt(true, false)).isEqualTo(2);
        assertThat(Bytes.toInt(true, true)).isEqualTo(3);
        // three bits
        assertThat(Bytes.toInt(false, false, false)).isEqualTo(0);
        assertThat(Bytes.toInt(true, false, false)).isEqualTo(4);
        // four bits
        assertThat(Bytes.toInt(true, false, false, false)).isEqualTo(8);
        assertThat(Bytes.toInt(true, false, true, false)).isEqualTo(10);
        assertThat(Bytes.toInt(true, true, true, true)).isEqualTo(15);
        // twelve bits (0010 0111 0011 = 0x0273 = 627)
        assertThat(Bytes.toInt( //
                /* false, false, */true, false, //
                false, true, true, true, //
                false, false, true, true //
        )).isEqualTo(627);
    }

    /**
     * Test {@link Bytes#concat(byte[]...)}
     */
    @Test
    public void testConcat() {
        byte[] bytes1 = new byte[]{1, 2, 3, 4, 5};
        byte[] bytes2 = new byte[]{6, 7, 8, 9};
        byte[] bytes3 = new byte[]{0};

        assertThat(Bytes.concat(bytes1, bytes2, bytes3)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
    }

    /**
     * Test constructor of {@link Bytes}
     */
    @Test
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Bytes.class);
    }
}
