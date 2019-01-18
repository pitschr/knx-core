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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.knx.link.datapoint.value.*;
import li.pitschmann.knx.link.exceptions.*;
import org.junit.jupiter.api.*;

import java.nio.charset.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test Class for {@link DPT4}
 *
 * @author PITSCHR
 */
public class DPT4Test extends AbstractDataPointTypeTest<DPT4, DPT4Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final DPT4 dpt = DPT4.ASCII;

        assertThat(dpt.getId()).isEqualTo("4.001");
        assertThat(dpt.getDescription()).isEqualTo("ASCII Character");
    }

    @Override
    @Test
    public void testCompatibility() {
        final DPT4 dpt = DPT4.ASCII;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"foo"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[]{"a", "b"})).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        // OK
        assertThat(dpt.toValue(new byte[]{'a'})).isInstanceOf(DPT4Value.class);
        assertThat(dpt.toValue(new byte[]{'z'})).isInstanceOf(DPT4Value.class);
        assertThat(dpt.toValue(new String[]{"b"})).isInstanceOf(DPT4Value.class);
        assertThat(dpt.toValue(new String[]{"y"})).isInstanceOf(DPT4Value.class);
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
        final DPT4 dptAscii = DPT4.ASCII;
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
        assertThatThrownBy(() -> dptAscii.toValue(new byte[]{(byte) 0xE4})).isInstanceOf(KnxException.class);

        /*
         * ISO_8859_1
         */
        final DPT4 dptIso = DPT4.ISO_8859_1;
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
     * Invalid Test {@link DPT4#toValue(byte[])}
     */
    @Test
    public void testOfInvalid() {
        // wrong dpt
        assertThat(DPT4.ASCII.toValue(new byte[]{(byte) 0x00})).isNotEqualTo(DPT4.ISO_8859_1.toValue(new byte[]{(byte) 0x00}));
        // wrong value
        assertThat(DPT4.ASCII.toValue(new byte[]{(byte) 0x00})).isNotEqualTo(DPT4.ASCII.toValue(new byte[]{(byte) 0x01}));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code byteValue} and {@code charValue}
     *
     * @param dpt
     * @param byteValue
     * @param charValue
     */
    private void assertDPT(final DPT4 dpt, final byte byteValue, final char charValue) {
        final DPT4Value dptValue = dpt.toValue(charValue);

        // assert base DPT
        this.assertBaseDPT(dpt, new byte[]{byteValue}, dptValue);
        // assert specific DPT4
        assertThat(dpt.toValue(new String[]{String.valueOf(charValue)})).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(charValue)).containsExactly(byteValue);
    }
}
