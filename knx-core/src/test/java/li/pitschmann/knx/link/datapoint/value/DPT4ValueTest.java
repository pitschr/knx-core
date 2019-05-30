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

import li.pitschmann.knx.link.datapoint.DPT4;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT4Value}
 *
 * @author PITSCHR
 */
public final class DPT4ValueTest {
    /**
     * Test {@link DPT4Value}
     */
    @Test
    public void test() {
        this.assertValue(DPT4.ISO_8859_1, (byte) 0x41, 'A');
        this.assertValue(DPT4.ISO_8859_1, (byte) 0x5A, 'Z');
        this.assertValue(DPT4.ISO_8859_1, (byte) 0xE4, 'ä');
        this.assertValue(DPT4.ISO_8859_1, (byte) 0xF6, 'ö');
    }

    /**
     * Test {@link DPT4Value} failures
     */
    @Test
    public void testFailures() {
        // step code must be between 0..7
        assertThatThrownBy(() -> this.assertValue(DPT4.ASCII, (byte) 0xE4, 'ä')).isInstanceOf(KnxException.class)
                .hasMessage("Issue during decoding charset 'US-ASCII' with value: 0xE4");
    }

    private void assertValue(final DPT4 dpt, final byte b, final char character) {
        final var dptValue = new DPT4Value(dpt, character);
        final var dptValueByByte = new DPT4Value(dpt, b);

        // instance methods
        assertThat(dptValue.getCharacter()).isEqualTo(character);
        assertThat(dptValue.toByteArray()).containsExactly(b);

        // class methods
        assertThat(DPT4Value.toByteArray(character)).containsExactly(b);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT4Value(DPT4.ASCII, character));
        assertThat(dptValue).isNotEqualTo(new DPT4Value(dpt, (char) (character + 1))); // next char

        // toString
        final var toString = String.format("DPT4Value{dpt=%s, character=%s, byteArray=%s}", dpt, character, ByteFormatter.formatHex(b));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }
}
