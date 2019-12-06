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

import li.pitschmann.knx.core.datapoint.DPT4;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nullable;
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
        // character
        try {
            this.character = dpt.getCharsetDecoder().decode(ByteBuffer.wrap(new byte[]{b})).get();
        } catch (CharacterCodingException e) {
            throw new KnxException(String.format("Issue during decoding charset '%s' with value: %s", dpt.getCharset(), ByteFormatter.formatHex(b)),
                    e);
        }
    }

    public DPT4Value(final DPT4 dpt, final char character) {
        super(dpt);
        this.character = character;
    }

    /**
     * Converts character to byte array
     *
     * @param character
     * @return byte array
     */
    public static byte[] toByteArray(final char character) {
        return new byte[]{(byte) character};
    }

    public char getCharacter() {
        return this.character;
    }


    @Override
    public byte[] toByteArray() {
        return toByteArray(this.character);
    }


    @Override
    public String toText() {
        return String.format("char '%c'", getCharacter());
    }


    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("character", this.character)
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
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
        return Objects.hash(this.getDPT(), this.character);
    }

}
