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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import li.pitschmann.knx.link.datapoint.DPT16;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT16} (16.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Character 1)                   (Character 2)                 |
 * Encoding     | A   A   A   A   A   A   A   A   A   A   A   A   A   A   A   A |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character 3)                   (Character 4)                 |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character 5)                   (Character 6)                 |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character 7)                   (Character 8)                 |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character 9)                   (Character 10)                |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character 11)                  (Character 12)                |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character 13)                  (Character 14)                |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     14 octets (A<sub>112</sub>)
 * </pre>
 * <p>
 * These Data Point Types are used to transmit strings of textual characters. The length is fixed to 14 octets. The
 * contents are filled starting from the most significant octet. Each octet shall be encoded as specified for the chosen
 * character set, as defined in clause 0. If the string to be transmitted is smaller then 14 octets, unused trailing
 * octets in the character string shall be set to NULL (00h).
 * <p>
 * Example:<br>
 * "KNX is OK" is encoded as follows: "0x4B 4E 58 20 69 73 20 4F 4B 00 00 00 00 00"
 *
 * @author PITSCHR
 */
public final class DPT16Value extends AbstractDataPointValue<DPT16> {
    private final String text;
    private final byte[] byteArray;

    public DPT16Value(final DPT16 dpt, final byte[] bytes) {
        super(dpt);

        // text
        if (bytes == null || bytes.length == 0) {
            this.text = "";
            this.byteArray = new byte[14];
        } else if (bytes.length > 14) {
            throw new IllegalArgumentException(
                    String.format("The length of bytes is too long (expected up to 14 bytes): %s", ByteFormatter.formatHexAsString(bytes)));
        } else {
            // removing 0x00 suffix if available
            byte[] newBytes = Bytes.trimRight(bytes, (byte) 0x00);
            try {
                char[] chars = new char[newBytes.length];
                dpt.getCharsetDecoder().decode(ByteBuffer.wrap(newBytes)).get(chars);
                this.text = new String(chars);
            } catch (CharacterCodingException e) {
                throw new KnxException(String.format("Issue during decoding charset '%s' with value: %s (original: %s)", dpt.getCharset(),
                        ByteFormatter.formatHexAsString(newBytes), ByteFormatter.formatHexAsString(bytes)), e);
            }
            // byte array must be 14-bytes, pad 0x00 on right side
            this.byteArray = Bytes.padRight(newBytes, (byte) 0x00, 14);
        }
    }

    public DPT16Value(final DPT16 dpt, final String text) {
        super(dpt);
        Preconditions.checkArgument(text == null || text.length() <= 14,
                String.format("The length of text is too long (expected up to 14 characters): %s", text));
        this.text = Objects.toString(text, "");
        this.byteArray = toByteArray(text, dpt.getCharset());
    }

    /**
     * Converts text to byte array
     *
     * @param text    may not be longer than 14 characters
     * @param charset
     * @return byte array
     */
    public static byte[] toByteArray(final String text, final Charset charset) {
        if (Strings.isNullOrEmpty(text)) {
            return new byte[14];
        } else if (text.length() > 14) {
            throw new IllegalArgumentException(String.format("The length of text is too long (expected up to 14 chars): %s", text));
        } else if (charset.newEncoder().canEncode(text)) {
            byte[] textAsBytes = text.getBytes(charset);
            if (textAsBytes.length < 14) {
                textAsBytes = Bytes.padRight(textAsBytes, (byte) 0x00, 14);
            }
            return textAsBytes;
        } else {
            throw new KnxException(String.format("The text '%s' contains a character which is not compatible with charset '%s'.", text, charset),
                    new CharacterCodingException());
        }
    }

    public String getText() {
        return this.text;
    }

    @Override
    public byte[] toByteArray() {
        return this.byteArray.clone();
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(DPT16Value.class)
                .add("dpt", this.getDPT())
                .add("text", this.text)
                .add("byteArray", ByteFormatter.formatHexAsString(this.byteArray))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT16Value) {
            final DPT16Value other = (DPT16Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.text, other.text);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDPT(), this.text);
    }
}
