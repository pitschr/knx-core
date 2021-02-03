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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT4Value;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Data Point Typ 4 for 'Character' (1 Octet)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | 0   (Character)               |
 * Encoding    | r   A   A   A   A   A   A   A |
 *             +---+---+---+---+---+---+---+---+
 * Format:     1 octet (A<sub>8</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT4 extends BaseRangeDataPointType<DPT4Value, Integer> {
    /**
     * <strong>4.001</strong> ASCII Character
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | 0   (Character)               |
     * Encoding    | r   A   A   A   A   A   A   A |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 octet (A<sub>8</sub>)
     * Range:      A = [0 .. 127]
     * </pre>
     */
    @DataPoint({"4.001", "dpst-4-1"})
    public static final DPT4 ASCII = new DPT4("ASCII Character", 0, 127, StandardCharsets.US_ASCII);

    /**
     * <strong>4.002</strong> ISO 8895-1 Character
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Character)                   |
     * Encoding    | A   A   A   A   A   A   A   A |
     *             +---+---+---+---+---+---+---+---+
     * Format:     1 octet (A<sub>8</sub>)
     * Range:      A = [0 .. 255]
     * </pre>
     */
    @DataPoint({"4.002", "dpt-4", "dpst-4-2"})
    public static final DPT4 ISO_8859_1 = new DPT4("ISO 8895-1 Character", 0, 255, StandardCharsets.ISO_8859_1);

    private final Charset charset;
    private final CharsetDecoder charsetDecoder;
    private final CharsetEncoder charsetEncoder;

    /**
     * Constructor for {@link DPT4}
     *
     * @param description description for {@link DPT4}
     * @param lowerValue  the lower value for {@link DPT4}
     * @param upperValue  the upper value for {@link DPT4}
     * @param charset     the character encoding set for {@link DPT4}
     */
    private DPT4(final String description,
                 final int lowerValue,
                 final int upperValue,
                 final Charset charset) {
        super(description, lowerValue, upperValue, null);

        this.charset = charset;
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
        return bytes.length == 1;
    }

    @Override
    protected DPT4Value parse(final byte[] bytes) {
        return new DPT4Value(this, bytes[0]);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1 && args[0] != null && args[0].length() == 1;
    }

    @Override
    protected DPT4Value parse(final String[] args) {
        return new DPT4Value(this, args[0].charAt(0));
    }

    public DPT4Value of(final char character) {
        return new DPT4Value(this, character);
    }
}
