/*
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
import li.pitschmann.knx.core.datapoint.DPT4;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT4} (4.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Character)                   |
 * Encoding     | A   A   A   A   A   A   A   A |
 *              +---+---+---+---+---+---+---+---+
 * Format:     1 octet (A<sub>8</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT4Value extends AbstractDataPointValue<DPT4> {
    private final char character;

    public DPT4Value(final DPT4 dpt, final byte b) {
        super(dpt);

        // no need to check if it can be encoded
        this.character = toCharacter(dpt, b);
    }

    public DPT4Value(final DPT4 dpt, final char character) {
        super(dpt);

        Preconditions.checkArgument(dpt.getCharsetEncoder().canEncode(character),
                "The given character cannot be encoded by DPT '{}': {}", dpt.getId(), character);
        this.character = character;
    }

    /**
     * Private class to convert from byte to a character using a charset decoder
     * that is defined in the {@link DPT4}
     *
     * @param dpt the data point type with corresponding {@link java.nio.charset.CharsetDecoder}
     * @param b   byte to be decoded
     * @return a character
     */
    private static char toCharacter(final DPT4 dpt, final byte b) {
        try {
            return dpt.getCharsetDecoder().decode(ByteBuffer.wrap(new byte[]{b})).get();
        } catch (CharacterCodingException e) {
            throw new KnxIllegalArgumentException(
                    String.format("Issue during decoding charset '%s' with value: %s",
                            dpt.getCharset(), ByteFormatter.formatHex(b)), e);
        }
    }

    /**
     * Returns the character
     *
     * @return char
     */
    public char getCharacter() {
        return character;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{(byte) character};
    }

    @Override
    public String toText() {
        return "char '" + character + "'";
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", getDPT().getId())
                .add("character", character)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT4Value) {
            final var other = (DPT4Value) obj;
            return Objects.equals(this.getDPT(), other.getDPT()) //
                    && Objects.equals(this.character, other.character);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDPT(), character);
    }

}
