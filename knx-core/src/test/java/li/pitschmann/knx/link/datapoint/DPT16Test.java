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

import li.pitschmann.knx.link.datapoint.value.DPT16Value;
import li.pitschmann.knx.link.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.link.exceptions.DataPointTypeIncompatibleSyntaxException;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.utils.Bytes;
import li.pitschmann.knx.utils.Bytes.FillDirection;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT16}
 *
 * @author PITSCHR
 */
public class DPT16Test extends AbstractDataPointTypeTest<DPT16, DPT16Value> {
    @Override
    @Test
    public void testIdAndDescription() {
        final var dpt = DPT16.ASCII;

        assertThat(dpt.getId()).isEqualTo("16.000");
        assertThat(dpt.getDescription()).isEqualTo("ASCII Characters");
    }

    @Override
    @Test
    public void testCompatibility() {
        final var dpt = DPT16.ASCII;

        // failures
        assertThatThrownBy(() -> dpt.toValue(new byte[15])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> dpt.toValue(new String[2])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> dpt.toValue("Hello World Overflow!")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(dpt.toValue(Bytes.fillByteArray(new byte[14], new byte[]{'a'}, FillDirection.LEFT_TO_RIGHT))).isInstanceOf(DPT16Value.class);
        assertThat(dpt.toValue(new byte[]{'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!', '!', '!'})).isInstanceOf(DPT16Value.class);
        assertThat(dpt.toValue("Hello World!!!")).isInstanceOf(DPT16Value.class);
    }

    /**
     * Tests the {@link DPT16#getCharset()} and {@link DPT16#getCharsetDecoder()}
     */
    @Test
    public void testCharset() {
        assertThat(DPT16.ASCII.getCharset()).isEqualTo(StandardCharsets.US_ASCII);
        assertThat(DPT16.ISO_8859_1.getCharset()).isEqualTo(StandardCharsets.ISO_8859_1);

        assertThat(DPT16.ASCII.getCharsetDecoder()).isInstanceOf(StandardCharsets.US_ASCII.newDecoder().getClass());
        assertThat(DPT16.ISO_8859_1.getCharsetDecoder()).isInstanceOf(StandardCharsets.ISO_8859_1.newDecoder().getClass());
    }

    @Override
    @Test
    public void testOf() {
        // --------
        // ASCII
        // --------
        final var dptAscii = DPT16.ASCII;
        // 0 characters
        this.assertDPT(dptAscii, new byte[0], "");
        // 5 characters
        this.assertDPT(dptAscii, new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, "01234");
        // 9 characters
        this.assertDPT(dptAscii,
                new byte[]{(byte) 0x61, (byte) 0x62, (byte) 0x63, (byte) 0x20, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x3f},
                "abc 123?");
        // 14 characters
        this.assertDPT(dptAscii, new byte[]{(byte) 0x41, (byte) 0x42, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48,
                (byte) 0x49, (byte) 0x4a, (byte) 0x4b, (byte) 0x4c, (byte) 0x4d}, "ABCDEFGHIJKLM");

        // character: 'ä' (not supported by ASCII)
        assertThatThrownBy(() -> dptAscii.toValue((byte) 0xe4)).isInstanceOf(KnxException.class);

        // --------
        // ISO_8859_1
        // --------
        final var dpt = DPT16.ISO_8859_1;
        // 0 characters
        this.assertDPT(dpt, new byte[0], "");
        // 5 characters
        this.assertDPT(dpt, new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, "01234");
        // 9 characters
        this.assertDPT(dpt, new byte[]{(byte) 0xe4, (byte) 0xf6, (byte) 0xfc, (byte) 0x20, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x3f},
                "äöü 123?");
        // 14 characters
        this.assertDPT(dpt, new byte[]{(byte) 0xc4, (byte) 0xd6, (byte) 0xdc, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48,
                (byte) 0x49, (byte) 0x4a, (byte) 0x4b, (byte) 0x4c, (byte) 0x4d}, "ÄÖÜDEFGHIJKLM");
    }

    /**
     * Invalid Test {@link DPT16}
     */
    @Test
    public void testOfInvalid() {
        // wrong dpt
        assertThat(DPT16.ASCII.toValue((byte) 0x00)).isNotEqualTo(DPT16.ISO_8859_1.toValue((byte) 0x00));
        // wrong value
        assertThat(DPT16.ASCII.toValue((byte) 0x00)).isNotEqualTo(DPT16.ASCII.toValue((byte) 0x01));
    }

    /**
     * Asserts the DPT for given arguments {@code dpt}, {@code bValueArray} and {@code strValue}
     *
     * @param dpt
     * @param bValueArray
     * @param strValue
     */
    private void assertDPT(final DPT16 dpt, final byte[] bValueArray, final String strValue) {
        final var dptValue = dpt.toValue(strValue);
        final var bValueArrayPadded = Bytes.padRight(bValueArray, (byte) 0x00, 14);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArrayPadded, dptValue);
        // assert specific DPT16
        assertThat(dpt.toValue(strValue)).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(strValue)).containsExactly(bValueArrayPadded);
    }
}
