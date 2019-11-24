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

import li.pitschmann.knx.link.datapoint.DPT16;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.utils.ByteFormatter;
import li.pitschmann.knx.utils.Bytes;
import org.junit.jupiter.api.Test;

import java.nio.charset.CharacterCodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        // Space:  12345678901234
        // Text : "              "
        this.assertValue(
                DPT16.ASCII,
                Bytes.padRight(new byte[0], (byte) 0x20, 14),
                "              "
        );
        // Space: 12345678901234
        // Text : Hello World!
        this.assertValue(
                DPT16.ASCII,
                new byte[]{
                        0x48, 0x65, 0x6C, 0x6C,
                        0x6F, 0x20, 0x57, 0x6F,
                        0x72, 0x6C, 0x64, 0x21
                },
                "Hello World!"
        );
        // Space: 12345678901234
        // Text : @
        this.assertValue(
                DPT16.ASCII,
                new byte[]{0x40},
                "@"
        );
        // Space: 12345678901234
        // Text : ÄÖÜäöü¡¿«»ßØ÷¤
        this.assertValue(
                DPT16.ISO_8859_1,
                new byte[]{
                        (byte) 0xC4, (byte) 0xD6, (byte) 0xDC, (byte) 0xE4,
                        (byte) 0xF6, (byte) 0xFC, (byte) 0xA1, (byte) 0xBF,
                        (byte) 0xAB, (byte) 0xBB, (byte) 0xDF, (byte) 0xD8,
                        (byte) 0xF7, (byte) 0xA4
                },
                "ÄÖÜäöü¡¿«»ßØ÷¤"
        );
    }

    private void assertValue(final DPT16 dpt, final byte[] textAsBytes, final String characters) {
        final var dptValue = new DPT16Value(dpt, characters);
        final var dptValueByByte = new DPT16Value(dpt, textAsBytes);

        // fills out the right padding with zero bytes (0x00)
        final var bytesWith14Length = Bytes.padRight(textAsBytes, (byte) 0x00, 14);

        // instance methods
        assertThat(dptValue.getCharacters()).isEqualTo(characters);
        assertThat(dptValue.toByteArray()).containsExactly(bytesWith14Length);
        assertThat(dptValue.toText()).isEqualTo(characters);

        // class methods
        assertThat(DPT16Value.toByteArray(characters, dpt.getCharset())).containsExactly(bytesWith14Length);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        final var anotherDPT = dpt == DPT16.ASCII ? DPT16.ISO_8859_1 : DPT16.ASCII;
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        if (dpt == DPT16.ISO_8859_1) {
            assertThat(dptValue).isNotEqualTo(new DPT16Value(anotherDPT, "abc"));
        } else {
            assertThat(dptValue).isNotEqualTo(new DPT16Value(anotherDPT, characters));
        }
        assertThat(dptValue).isNotEqualTo(new DPT16Value(dpt, characters.substring(1)));

        // toString
        final var toString = String.format("DPT16Value{dpt=%s, characters=%s, byteArray=%s}", dpt, characters, ByteFormatter.formatHexAsString(bytesWith14Length));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }

    /**
     * Special test with {@code null} and empty text/bytes. Those are valid as well.
     */
    @Test
    public void testWithNullAndEmpty() {
        final var dpt = DPT16.ASCII;
        final var emptyBytes = new byte[14];

        final var dptValueNull = new DPT16Value(dpt, (String) null);
        final var dptValueNullByByte = new DPT16Value(dpt, (byte[]) null);
        final var dptValueEmpty = new DPT16Value(dpt, "");
        final var dptValueEmptyByByte = new DPT16Value(dpt, new byte[0]);

        // instance methods
        assertThat(dptValueNull.getCharacters()).isEmpty();
        assertThat(dptValueNull.toByteArray()).containsExactly(emptyBytes);

        // class methods
        assertThat(DPT16Value.toByteArray(null, dpt.getCharset())).containsExactly(emptyBytes);
        assertThat(DPT16Value.toByteArray("", dpt.getCharset())).containsExactly(emptyBytes);

        // equals
        assertThat(dptValueNull).isEqualTo(dptValueNull);
        assertThat(dptValueNullByByte).isEqualTo(dptValueNull);
        assertThat(dptValueNullByByte).hasSameHashCodeAs(dptValueNull);
        assertThat(dptValueEmpty).isEqualTo(dptValueNull);
        assertThat(dptValueEmpty).hasSameHashCodeAs(dptValueNull);
        assertThat(dptValueEmptyByByte).isEqualTo(dptValueNull);
        assertThat(dptValueEmptyByByte).hasSameHashCodeAs(dptValueNull);

        // not equals
        final var anotherDPT = dpt == DPT16.ASCII ? DPT16.ISO_8859_1 : DPT16.ASCII;
        assertThat(dptValueNull).isNotEqualTo(null);
        assertThat(dptValueNull).isNotEqualTo(new Object());
        assertThat(dptValueNull).isNotEqualTo(new DPT16Value(anotherDPT, (byte[]) null));
        assertThat(dptValueNull).isNotEqualTo(new DPT16Value(anotherDPT, (String) null));
        assertThat(dptValueNull).isNotEqualTo(new DPT16Value(dpt, " "));

        // toString
        final var toString = String.format("DPT16Value{dpt=%s, characters=, byteArray=0x00 00 00 00 00 00 00 00 00 00 00 00 00 00}", dpt);
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
                .hasMessageStartingWith("The length of characters is too long");
        assertThatThrownBy(() -> DPT16.ASCII.toByteArray("123456789012345")).isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("The length of characters is too long");
    }
}
