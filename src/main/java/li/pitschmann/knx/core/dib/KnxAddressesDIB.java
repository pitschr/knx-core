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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.MultiRawDataAware;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * KNX Addresses DIB to specify DIB for type {@link DescriptionType#KNX_ADDRESSES}
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
public final class KnxAddressesDIB implements MultiRawDataAware {
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
    private final IndividualAddress address;
    private final List<IndividualAddress> additionalAddresses;

    private KnxAddressesDIB(final byte[] bytes) {
        // bytes[0] -> length not relevant
        // bytes[1] -> description type not relevant

        // KNX Individual Address (mandatory)
        this.address = IndividualAddress.of(new byte[]{bytes[2], bytes[3]});
        // Additional Individual Addresses (optional)
        final var sizeOfAdditionalAddresses = (bytes.length - 4) / 2;
        final var tmp = new ArrayList<IndividualAddress>(sizeOfAdditionalAddresses);
        for (var i = 4; i < bytes.length; i += 2) {
            tmp.add(IndividualAddress.of(new byte[]{bytes[i], bytes[i + 1]}));
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
        Preconditions.checkArgument(bytes.length >= STRUCTURE_MIN_LENGTH && bytes.length <= STRUCTURE_MAX_LENGTH,
                "Incompatible structure length. Expected [{}..{}] but was: {}", STRUCTURE_MIN_LENGTH, STRUCTURE_MAX_LENGTH, bytes.length);
        Preconditions.checkArgument(bytes.length % 2 == 0,
                "Incompatible structure length. Length must be divisible by 2, but was: {}", bytes.length);
        return new KnxAddressesDIB(bytes);
    }

    public IndividualAddress getAddress() {
        return address;
    }

    public List<IndividualAddress> getAdditionalAddresses() {
        return additionalAddresses;
    }

    @Override
    public byte[] toByteArray() {
        // 4 bytes (Structure Length, Description Type + KNX Individual Address)
        // + N dynamic bytes for additional KNX Individual Addresses
        final var totalLength = 4 + additionalAddresses.size() * 2;

        final var addressAsBytes = address.toByteArray();

        final var bytes = new byte[totalLength];
        bytes[0] = (byte) totalLength;
        bytes[1] = DescriptionType.KNX_ADDRESSES.getCodeAsByte();
        bytes[2] = addressAsBytes[0];
        bytes[3] = addressAsBytes[1];
        if (!additionalAddresses.isEmpty()) {
            var pos = 4;
            for (var additionalAddress : additionalAddresses) {
                final var additionalAddressAsBytes = additionalAddress.toByteArray();
                bytes[pos++] = additionalAddressAsBytes[0];
                bytes[pos++] = additionalAddressAsBytes[1];
            }
        }

        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("address", address.getAddress())
                .add("additionalAddresses", additionalAddresses.stream().map(IndividualAddress::getAddress).collect(Collectors.toList()))
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof KnxAddressesDIB) {
            final var other = (KnxAddressesDIB) obj;
            return Objects.equals(this.address, other.address)
                    && Objects.equals(this.additionalAddresses, other.additionalAddresses);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, additionalAddresses);
    }

}
