/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
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
import li.pitschmann.knx.core.datapoint.DPT28;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Strings;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT28} (28.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Character 1)                   (Character 2)                 |
 * Encoding     | A   A   A   A   A   A   A   A   A   A   A   A   A   A   A   A |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character 3)                   (Character 4)                 |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              |                    ... variable length ...                    |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Character N)                   (Terminated by NULL (0x00)    |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:      N octets + 1 NULL-character termination (A<sub>N</sub>B<sub>1</sub>)
 * </pre>
 * <p>
 * This Datapoint Type shall be used to transmit Unicode strings, whereas the UTF-8
 * encoding scheme shall be used for Unicode Transformation to data contents for transmission.
 * <p>
 * Using UTF-8 the data length for a string (multiple characters) is also not fixed, but variable.
 * The string shall be terminated by the NULL- character (00h).
 * <p>
 * Example:<br>
 * "ABC 123 äöü 子老何" is encoded as follows: "0x41 42 43 20 31 32 33 20 C3 A4 C3 B6 C3 BC 20 E5 AD 90 E8 80 81 E4 BD 95 00"<br>
 * "0x20" for space character<br>
 * "0x00" for termination / null-character
 * "0x41 42 43" for "ABC"<br>
 * "0x31 32 33" for "123"<br>
 * "0xC3 A4 C3 B6 C3 BC" for "äöü"
 * "0xE5 AD 90 E8 80 81 E4 BD 95" for "子老何"
 *
 * @author PITSCHR
 */
public final class DPT28Value extends AbstractDataPointValue<DPT28> {
    private final String characters;

    public DPT28Value(final byte[] bytes) {
        this(
                toCharacters(bytes)
        );
    }

    public DPT28Value(final @Nullable String characters) {
        super(DPT28.UTF_8);

        if (characters == null || characters.trim().isEmpty()) {
            this.characters = "";
        } else {
            this.characters = characters;
        }
    }

    /**
     * Converts {@code bytes} to a string representation of characters
     *
     * @param bytes byte array to be decoded; may be {@code null} or empty
     * @return a string representation of characters, or empty string if {@code bytes} were {@code null} or empty
     * @throws KnxIllegalArgumentException if {@code bytes} could not be decoded
     */
    private static String toCharacters(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        } else {
            // removing 0x00 suffix if available
            final byte[] newBytes = Bytes.trimRight(bytes, (byte) 0x00);
            return new String(newBytes, StandardCharsets.UTF_8);
        }
    }

    public String getCharacters() {
        return characters;
    }

    @Override
    public byte[] toByteArray() {
        if (Strings.isNullOrEmpty(characters)) {
            return new byte[]{0x00}; // return NULL-character (termination)
        } else {
            // character plus NULL-character (for termination)
            return characters.concat("\0").getBytes(StandardCharsets.UTF_8);
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
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("characters", characters)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT28Value) {
            final var other = (DPT28Value) obj;
            return Objects.equals(this.characters, other.characters);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(characters);
    }
}
