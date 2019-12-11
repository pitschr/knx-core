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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * KNX Addresses DIB to specify DIB for type {@link DescriptionType#KNX_ADDRESSES}
 * <p>
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length            | Description Type Code           |
 * | (1 octet)                   | (1 octet)                       |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | KNX Individual Address                                        |
 * | (2 octets)                                                    |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Additional Individual Address 1 (optional)                    |
 * | (2 octets)                                                    |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | Additional Individual Address 2 (optional)                    |
 * | (2 octets)                                                    |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | ... more ...                                                  |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * See: KNX Standard Core
 *
 * @author PITSCHR
 */
public final class KnxAddressesDIB extends AbstractDIB {
    /**
     * Minimum Structure Length for {@link KnxAddressesDIB}
     * <p>
     * 1 byte for Structure Length<br>
     * 1 byte for Description Type Code<br>
     * 2 bytes for KNX Individual Address<br>
     */
    private static final int STRUCTURE_MIN_LENGTH = 4;
    /**
     * Maximum Structure Length for {@link KnxAddressesDIB} is 254
     */
    private static final int STRUCTURE_MAX_LENGTH = 0xFE;
    private final IndividualAddress knxAddress;
    private final List<IndividualAddress> additionalAddresses;

    private KnxAddressesDIB(final byte[] rawData) {
        super(rawData);

        // rawData[0] -> length already covered in abstract class DIB
        // rawData[1] -> description type already covered in abstract class DIB

        // KNX Individual Address (mandatory)
        this.knxAddress = IndividualAddress.of(new byte[]{rawData[2], rawData[3]});
        // Additional Individual Addresses (optional)
        final var sizeOfAdditionalAddresses = (rawData.length - 4) / 2;
        final var tmp = new ArrayList<IndividualAddress>(sizeOfAdditionalAddresses);
        for (var i = 4; i < rawData.length; i += 2) {
            tmp.add(IndividualAddress.of(new byte[]{rawData[i], rawData[i + 1]}));
        }
        this.additionalAddresses = Collections.unmodifiableList(tmp);
    }

    /**
     * Builds a new {@link KnxAddressesDIB} instance
     *
     * @param bytes complete byte array for {@link KnxAddressesDIB}
     * @return a new immutable {@link KnxAddressesDIB}
     */
    public static KnxAddressesDIB of(final byte[] bytes) {
        return new KnxAddressesDIB(bytes);
    }

    @Override
    protected void validate(final byte[] rawData) {
        if (rawData.length < STRUCTURE_MIN_LENGTH || rawData.length > STRUCTURE_MAX_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_MIN_LENGTH, STRUCTURE_MAX_LENGTH, rawData.length, rawData);
        } else if (rawData.length % 2 != 0) {
            throw new KnxIllegalArgumentException(String.format("The size of 'rawData' must be divisible by two. Actual length is: %s. RawData: %s",
                    rawData.length, ByteFormatter.formatHexAsString(rawData)));
        }
    }


    public IndividualAddress getKnxAddress() {
        return this.knxAddress;
    }


    public List<IndividualAddress> getAdditionalAddresses() {
        return this.additionalAddresses;
    }

    @Override
    public String toString(boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("length", this.getLength() + " (" + ByteFormatter.formatHex(this.getLength()) + ")")
                .add("descriptionType", this.getDescriptionType())
                .add("knxAddress", this.knxAddress)
                .add("additionalAddresses", this.additionalAddresses);
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
