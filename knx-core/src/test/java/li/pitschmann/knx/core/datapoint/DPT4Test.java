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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT4Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import li.pitschmann.knx.core.exceptions.KnxException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT4}
 *
 * @author PITSCHR
 */
public class DPT4Test implements DPTTest {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT4.ASCII;

        assertThat(dpt.getId()).isEqualTo("4.001");
        assertThat(dpt.getDescription()).isEqualTo("ASCII Character");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT4.ASCII;

        // failures
        assertThatThrownBy(() -> dpt.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.of("foo")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("a", "b")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.of("0x80")).isInstanceOf(KnxException.class)
                .hasMessage("Issue during decoding charset 'US-ASCII' with value: 0x80");

        // OK
        assertThat(dpt.of((byte) 'a')).isInstanceOf(DPT4Value.class);
        assertThat(dpt.of((byte) 'z')).isInstanceOf(DPT4Value.class);
        assertThat(dpt.of('a')).isInstanceOf(DPT4Value.class);
        assertThat(dpt.of('z')).isInstanceOf(DPT4Value.class);
        assertThat(dpt.of("a")).isInstanceOf(DPT4Value.class);
        assertThat(dpt.of("z")).isInstanceOf(DPT4Value.class);
        assertThat(dpt.of("0x50")).isInstanceOf(DPT4Value.class);
        assertThat(dpt.of("0x5F")).isInstanceOf(DPT4Value.class);
    }

    @Test
    public void testCharset() {
        assertThat(DPT4.ASCII.getCharset()).isEqualTo(StandardCharsets.US_ASCII);
        assertThat(DPT4.ISO_8859_1.getCharset()).isEqualTo(StandardCharsets.ISO_8859_1);

        assertThat(DPT4.ASCII.getCharsetDecoder()).isInstanceOf(StandardCharsets.US_ASCII.newDecoder().getClass());
        assertThat(DPT4.ISO_8859_1.getCharsetDecoder()).isInstanceOf(StandardCharsets.ISO_8859_1.newDecoder().getClass());
    }

    @Override
    @Test
    public void testOf() {
        /*
         * ASCII
         */
        final var dptAscii = DPT4.ASCII;
        // character: NULL
        this.assertDPT(dptAscii, (byte) 0x00, '\u0000');
        // character: DEL
        this.assertDPT(dptAscii, (byte) 0x7F, '\u007F');
        // character: A
        this.assertDPT(dptAscii, (byte) 0x41, 'A');
        // character: z
        this.assertDPT(dptAscii, (byte) 0x7A, 'z');
        // character: '9'
        this.assertDPT(dptAscii, (byte) 0x39, '9');

        // character: 'ä' (not supported by ASCII)
        assertThatThrownBy(() -> dptAscii.of((byte) 0xE4)).isInstanceOf(KnxException.class);

        /*
         * ISO_8859_1
         */
        final var dptIso = DPT4.ISO_8859_1;
        // character: NULL
        this.assertDPT(dptIso, (byte) 0x00, '\u0000');
        // character: DEL
        this.assertDPT(dptIso, (byte) 0x7F, '\u007F');
        // character: A
        this.assertDPT(dptIso, (byte) 0x41, 'A');
        // character: z
        this.assertDPT(dptIso, (byte) 0x7A, 'z');
        // character: '9'
        this.assertDPT(dptIso, (byte) 0x39, '9');
        // character: 'ä'
        this.assertDPT(dptIso, (byte) 0xE4, 'ä');
    }

    /**
     * Invalid Test {@link DPT4#of(byte[])}
     */
    @Test
    public void testOfInvalid() {
        // wrong dpt
        assertThat(DPT4.ASCII.of((byte) 0x00)).isNotEqualTo(DPT4.ISO_8859_1.of((byte) 0x00));
        // wrong value
        assertThat(DPT4.ASCII.of((byte) 0x00)).isNotEqualTo(DPT4.ASCII.of((byte) 0x01));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code byteValue} and {@code charValue}
     *
     * @param dpt       data point type
     * @param byteValue byte value
     * @param charValue character value
     */
    private void assertDPT(final DPT4 dpt, final byte byteValue, final char charValue) {
        final var dptValue = dpt.of(charValue);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{byteValue}, dptValue);
        // assert specific DPT4
        assertThat(dpt.of(String.valueOf(charValue))).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(charValue)).containsExactly(byteValue);
    }
}
