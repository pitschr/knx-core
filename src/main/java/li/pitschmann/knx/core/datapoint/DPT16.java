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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT16Value;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Data Point Type 16 for 'Text' (14 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Character 1)                   (Character 2)                 |
 * Encoding    | A   A   A   A   A   A   A   A   A   A   A   A   A   A   A   A |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Character 3)                   (Character 4)                 |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Character 5)                   (Character 6)                 |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Character 7)                   (Character 8)                 |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Character 9)                   (Character 10)                |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Character 11)                  (Character 12)                |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Character 13)                  (Character 14)                |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
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
public final class DPT16 extends BaseDataPointType<DPT16Value> {
    /**
     * <strong>16.000</strong> ASCII
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Character 1)                   (Character 2)                 |
     * Encoding    | A   A   A   A   A   A   A   A   A   A   A   A   A   A   A   A |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 3)                   (Character 4)                 |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 5)                   (Character 6)                 |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 7)                   (Character 8)                 |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 9)                   (Character 10)                |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 11)                  (Character 12)                |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 13)                  (Character 14)                |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     14 octets (A<sub>112</sub>)
     * </pre>
     */
    @DataPoint({"16.000", "dpst-16-0"})
    public static final DPT16 ASCII = new DPT16("ASCII Characters", StandardCharsets.US_ASCII);

    /**
     * <strong>16.001</strong> ISO 8859-1 / Latin-1
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Character 1)                   (Character 2)                 |
     * Encoding    | A   A   A   A   A   A   A   A   A   A   A   A   A   A   A   A |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 3)                   (Character 4)                 |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 5)                   (Character 6)                 |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 7)                   (Character 8)                 |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 9)                   (Character 10)                |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 11)                  (Character 12)                |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Character 13)                  (Character 14)                |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     14 octets (A<sub>112</sub>)
     * </pre>
     */
    @DataPoint({"16.001", "dpt-16", "dpst-16-1"})
    public static final DPT16 ISO_8859_1 = new DPT16("ISO 8859-1 Characters", StandardCharsets.ISO_8859_1);

    private final Charset charset;
    private final CharsetDecoder charsetDecoder;
    private final CharsetEncoder charsetEncoder;

    /**
     * Constructor for {@link DPT16}
     *
     * @param desc    description for {@link DPT16}
     * @param charset the character encoding set for {@link DPT16}
     */
    private DPT16(final String desc,
                  final Charset charset) {
        super(desc);

        this.charset = Objects.requireNonNull(charset);
        this.charsetDecoder = charset.newDecoder();
        this.charsetEncoder = charset.newEncoder();
    }

    public Charset getCharset() {
        return charset;
    }

    public CharsetDecoder getCharsetDecoder() {
        return charsetDecoder;
    }

    public CharsetEncoder getCharsetEncoder() {
        return charsetEncoder;
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length <= 14;
    }

    @Override
    protected DPT16Value parse(final byte[] bytes) {
        return new DPT16Value(this, bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1 && args[0] != null && args[0].length() <= 14;
    }

    @Override
    protected DPT16Value parse(final String[] args) {
        return of(args[0]);
    }

    public DPT16Value of(final String text) {
        return new DPT16Value(this, text);
    }
}
