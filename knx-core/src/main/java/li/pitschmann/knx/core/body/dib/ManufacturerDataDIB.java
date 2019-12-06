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

package li.pitschmann.knx.core.body.dib;

import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.Bytes;


import java.util.Arrays;

/**
 * Manufacturer Data DIB to specify DIB for type {@link DescriptionType#MANUFACTURER_DATA}
 * <p>
 * The KNX manufacturer ID shall be added to clearly identify the manufacturer. This information is not necessarily
 * encoded in the KNX device KNX Serial Number (6 octets).
 * <p>
 * The manufacturer data DIB may contain any manufacturer specific data.
 * <p>
 * <strong>Experimental! Not used yet / no test available!</strong>
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length            | Description Type Code           |
 * | (1 octet)                   | (1 octet)                       |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | KNX Manufacturer ID                                           |
 * | (2 octets)                                                    |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Any manufacturer specific data                                |
 * | (up to 252 octets)                                            |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * <p>
 * The length of manufacturer specific data is the structure length minus 3 octets. Maximum 252 octets.
 * <p>
 * See: KNX Standard Core
 *
 * @author PITSCHR
 */
public final class ManufacturerDataDIB extends AbstractDIB {
    /**
     * Minimum Structure Length for {@link ManufacturerDataDIB}
     * <p>
     * 1 byte for Structure Length<br>
     * 1 byte for Description Type Code<br>
     * 2 bytes for KNX Manufacturer ID<br>
     */
    private static final int STRUCTURE_MIN_LENGTH = 4;
    /**
     * Maximum Structure Length for {@link ManufacturerDataDIB}
     */
    private static final int STRUCTURE_MAX_LENGTH = 0xFF;
    private final int manufacturerId;
    private final byte[] manufacturerSpecificData;

    private ManufacturerDataDIB(final byte[] rawData) {
        super(rawData);

        // rawData[0] -> length already covered in abstract class DIB
        // rawData[1] -> description type already covered in abstract class DIB

        // manufacturer id
        this.manufacturerId = Bytes.toUnsignedInt(rawData[2], rawData[3]);

        // manufacturer specific data
        if (rawData.length > STRUCTURE_MIN_LENGTH) {
            this.manufacturerSpecificData = Arrays.copyOfRange(rawData, STRUCTURE_MIN_LENGTH, rawData.length);
        } else {
            this.manufacturerSpecificData = new byte[0];
        }
    }

    /**
     * Builds a new {@link ManufacturerDataDIB} instance
     *
     * @param bytes complete byte array for {@link ManufacturerDataDIB}
     * @return a new immutable {@link ManufacturerDataDIB}
     */
    public static ManufacturerDataDIB of(final byte[] bytes) {
        return new ManufacturerDataDIB(bytes);
    }

    public int getManufacturerId() {
        return this.manufacturerId;
    }


    public byte[] getManufacturerSpecificData() {
        return this.manufacturerSpecificData.clone();
    }

    @Override
    protected void validate(final byte[] rawData) {
        if (rawData.length < STRUCTURE_MIN_LENGTH || rawData.length > STRUCTURE_MAX_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_MIN_LENGTH, STRUCTURE_MAX_LENGTH, rawData.length, rawData);
        }
    }
}
