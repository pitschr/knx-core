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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT16;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

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
 * These Data Point Types are used to transmit strings of characters. The length is fixed
 * to 14 octets. The contents are filled starting from the most significant octet. Each
 * octet shall be encoded as specified for the chosen character set, as defined in clause 0.
 * <p>
 * If the string to be transmitted is smaller then 14 octets, unused trailing
 * octets in the character string shall be set to NULL (00h).
 * <p>
 * Example:<br>
 * "KNX is OK" is encoded as follows: "0x4B 4E 58 20 69 73 20 4F 4B 00 00 00 00 00"
 *
 * @author PITSCHR
 */
public final class DPT16Value extends AbstractDataPointValue<DPT16> {
    private final String characters;

    public DPT16Value(final DPT16 dpt, final byte[] bytes) {
        this(dpt, toCharacters(dpt, bytes));
    }

    public DPT16Value(final DPT16 dpt, final @Nullable String characters) {
        super(dpt);
        Preconditions.checkArgument(characters == null || characters.length() <= 14,
                "The length of characters is too long (expected up to 14 characters): {}", characters);
        this.characters = Objects.toString(characters, "");
    }

    /**
     * Converts {@code bytes} to a string representation of characters
     *
     * @param dpt the data point that has {@link Charset} and {@link java.nio.charset.CharsetDecoder}
     * @param bytes byte array to be decoded; may be {@code null} or empty
     * @return a string representation of characters, or empty string if {@code bytes} were {@code null} or empty
     * @throws KnxIllegalArgumentException if {@code bytes} could not be decoded
     */
    private static String toCharacters(final DPT16 dpt, final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        } else {
            // removing 0x00 suffix if available
            final byte[] newBytes = Bytes.trimRight(bytes, (byte) 0x00);
            try {
                final char[] chars = new char[newBytes.length];
                dpt.getCharsetDecoder().decode(ByteBuffer.wrap(newBytes)).get(chars);
                return new String(chars);
            } catch (final CharacterCodingException e) {
                throw new IllegalArgumentException(
                        String.format(
                                "Issue during decoding charset '%s' with: %s", //
                                dpt.getCharset(), //
                                ByteFormatter.formatHexAsString(newBytes)
                        )
                );
            }
        }
    }

    public String getCharacters() {
        return characters;
    }

    @Override
    public byte[] toByteArray() {
        if (Strings.isNullOrEmpty(characters)) {
            return new byte[14];
        } else {
            final var charSet = getDPT().getCharset();
            if (charSet.newEncoder().canEncode(characters)) {
                byte[] characterAsBytes = characters.getBytes(charSet);
                if (characterAsBytes.length < 14) {
                    characterAsBytes = Bytes.padRight(characterAsBytes, (byte) 0x00, 14);
                }
                return characterAsBytes;
            } else {
                throw new KnxException("Issue during decoding charset '{}' with: {}", charSet, characters);
            }
        }
    }

    /**
     * Returns the characters. It is synonym call for to {@link #getCharacters()}.
     *
     * @return characters
     */
    @Override
    public String toText() {
        return getCharacters();
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("characters", characters)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT16Value) {
            final var other = (DPT16Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.characters, other.characters);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), characters);
    }
}
