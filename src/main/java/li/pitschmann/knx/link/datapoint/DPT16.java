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

import com.google.common.base.*;
import li.pitschmann.knx.link.datapoint.annotation.*;
import li.pitschmann.knx.link.datapoint.value.*;

import java.nio.charset.*;

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
public final class DPT16 extends AbstractDataPointType<DPT16Value> {
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
    @KnxDataPointType(id = "16.000", description = "ASCII Characters")
    public static final DPT16 ASCII = new DPT16("16.000", "ASCII Characters", StandardCharsets.US_ASCII);

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
    @KnxDataPointType(id = "16.001", description = "ISO 8859-1 Characters")
    public static final DPT16 ISO_8859_1 = new DPT16("16.001", "ISO 8859-1 Characters", StandardCharsets.ISO_8859_1);

    private final Charset charset;
    private final CharsetDecoder charsetDecoder;

    /**
     * Constructor for {@link DPT16}
     *
     * @param id
     * @param desc
     */
    private DPT16(final String id, final String desc, final Charset charset) {
        super(id, desc);

        Preconditions.checkNotNull(charset);
        this.charset = charset;
        this.charsetDecoder = charset.newDecoder();
    }

    public Charset getCharset() {
        return this.charset;
    }

    public CharsetDecoder getCharsetDecoder() {
        return this.charsetDecoder;
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
        return args.length == 1;
    }

    @Override
    protected DPT16Value parse(final String[] args) {
        return new DPT16Value(this, args[0]);
    }

    public DPT16Value toValue(final String text) {
        return new DPT16Value(this, text);
    }

    /**
     * Converts string representation to 14-octet array
     *
     * @param text up to 14 characters only!
     * @return 14-octet array
     */
    public byte[] toByteArray(final String text) {
        return DPT16Value.toByteArray(text, this.getCharset());
    }
}
