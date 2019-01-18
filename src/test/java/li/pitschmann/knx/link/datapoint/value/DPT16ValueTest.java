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

package li.pitschmann.knx.link.datapoint.value;

import li.pitschmann.knx.link.datapoint.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.utils.*;
import org.junit.jupiter.api.*;

import java.nio.charset.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test {@link DPT16Value}
 *
 * @author PITSCHR
 */
public final class DPT16ValueTest {
    /**
     * Test {@link DPT16Value}
     */
    @Test
    public void test() {
        // Space: 12345678901234
        // Text : "             "
        this.assertValue(DPT16.ASCII, Bytes.padRight(new byte[0], (byte) 0x20, 14), "              ");

        // Space: 12345678901234
        // Text : Hello World!
        this.assertValue(DPT16.ASCII, new byte[]{0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x57, 0x6F, 0x72, 0x6C, 0x64, 0x21}, "Hello World!");

        // Space: 12345678901234
        // Text : @
        this.assertValue(DPT16.ASCII, new byte[]{0x40}, "@");

        // Space: 12345678901234
        // Text : ÄÖÜäöü¡¿«»ßØ÷¤
        this.assertValue(DPT16.ISO_8859_1, new byte[]{(byte) 0xC4, (byte) 0xD6, (byte) 0xDC, (byte) 0xE4, (byte) 0xF6, (byte) 0xFC, (byte) 0xA1,
                (byte) 0xBF, (byte) 0xAB, (byte) 0xBB, (byte) 0xDF, (byte) 0xD8, (byte) 0xF7, (byte) 0xA4}, "ÄÖÜäöü¡¿«»ßØ÷¤");
    }

    private void assertValue(final DPT16 dpt, final byte[] textAsBytes, final String text) {
        DPT16Value dptValue = new DPT16Value(dpt, text);
        DPT16Value dptValueByByte = new DPT16Value(dpt, textAsBytes);

        // fills out the right padding with zero bytes (0x00)
        byte[] bytesWith14Length = Bytes.padRight(textAsBytes, (byte) 0x00, 14);

        // instance methods
        assertThat(dptValue.getText()).isEqualTo(text);
        assertThat(dptValue.toByteArray()).containsExactly(bytesWith14Length);

        // class methods
        assertThat(DPT16Value.toByteArray(text, dpt.getCharset())).containsExactly(bytesWith14Length);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        DPT16 anotherDPT = dpt == DPT16.ASCII ? DPT16.ISO_8859_1 : DPT16.ASCII;
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        if (dpt == DPT16.ISO_8859_1) {
            assertThat(dptValue).isNotEqualTo(new DPT16Value(anotherDPT, "abc"));
        } else {
            assertThat(dptValue).isNotEqualTo(new DPT16Value(anotherDPT, text));
        }
        assertThat(dptValue).isNotEqualTo(new DPT16Value(dpt, text.substring(1)));

        // toString
        String toString = String.format("DPT16Value{dpt=%s, text=%s, byteArray=%s}", dpt, text, ByteFormatter.formatHexAsString(bytesWith14Length));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }

    /**
     * Special test with {@code null} and empty text/bytes. Those are valid as well.
     */
    @Test
    public void testWithNullAndEmpty() {
        DPT16 dpt = DPT16.ASCII;
        byte[] emptyBytes = new byte[14];

        DPT16Value dptValueNull = new DPT16Value(dpt, (String) null);
        DPT16Value dptValueNullByByte = new DPT16Value(dpt, (byte[]) null);
        DPT16Value dptValueEmpty = new DPT16Value(dpt, "");
        DPT16Value dptValueEmptyByByte = new DPT16Value(dpt, new byte[0]);

        // instance methods
        assertThat(dptValueNull.getText()).isEmpty();
        assertThat(dptValueNull.toByteArray()).containsExactly(emptyBytes);

        // class methods
        assertThat(DPT16Value.toByteArray(null, dpt.getCharset())).containsExactly(emptyBytes);

        // equals
        assertThat(dptValueNull).isEqualTo(dptValueNull);
        assertThat(dptValueNullByByte).isEqualTo(dptValueNull);
        assertThat(dptValueNullByByte).hasSameHashCodeAs(dptValueNull);
        assertThat(dptValueEmpty).isEqualTo(dptValueNull);
        assertThat(dptValueEmpty).hasSameHashCodeAs(dptValueNull);
        assertThat(dptValueEmptyByByte).isEqualTo(dptValueNull);
        assertThat(dptValueEmptyByByte).hasSameHashCodeAs(dptValueNull);

        // not equals
        DPT16 anotherDPT = dpt == DPT16.ASCII ? DPT16.ISO_8859_1 : DPT16.ASCII;
        assertThat(dptValueNull).isNotEqualTo(null);
        assertThat(dptValueNull).isNotEqualTo(new Object());
        assertThat(dptValueNull).isNotEqualTo(new DPT16Value(anotherDPT, (byte[]) null));
        assertThat(dptValueNull).isNotEqualTo(new DPT16Value(anotherDPT, (String) null));
        assertThat(dptValueNull).isNotEqualTo(new DPT16Value(dpt, " "));

        // toString
        String toString = String.format("DPT16Value{dpt=%s, text=, byteArray=0x00 00 00 00 00 00 00 00 00 00 00 00 00 00}", dpt);
        assertThat(dptValueNull).hasToString(toString);
        assertThat(dptValueNullByByte).hasToString(toString);
        assertThat(dptValueEmpty).hasToString(toString);
        assertThat(dptValueEmptyByByte).hasToString(toString);
    }

    /**
     * Test failures
     */
    @Test
    public void testFailures() {
        // ASCII and 'ä' as byte are not compatible
        assertThatThrownBy(() -> new DPT16Value(DPT16.ASCII, new byte[]{(byte) 'ä'})).isInstanceOf(KnxException.class)
                .hasCauseInstanceOf(CharacterCodingException.class);
        // ASCII and 'ä' as text are not compatible
        assertThatThrownBy(() -> new DPT16Value(DPT16.ASCII, "ä")).isInstanceOf(KnxException.class)
                .hasCauseInstanceOf(CharacterCodingException.class);
        // longer than 14 bytes are not accepted
        assertThatThrownBy(() -> new DPT16Value(DPT16.ASCII, new byte[15])).isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("The length of bytes is too long");
        // text longer than 14 characters are not accepted
        assertThatThrownBy(() -> new DPT16Value(DPT16.ASCII, "123456789012345")).isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("The length of text is too long");
        assertThatThrownBy(() -> DPT16.ASCII.toByteArray("123456789012345")).isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("The length of text is too long");
    }
}
