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
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Device Supported Service Families DIB to specify DIB for type
 * {@link DescriptionType#SUPPORTED_SERVICE_FAMILIES}
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Structure Length            | Description Type Code           |
 * | (1 octet)                   | (1 octet)                       |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Service Family ID           | Service Family version          |
 * | (1 octet)                   | (1 octet)                       |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Service Family ID           | Service Family version          |
 * | (1 octet)                   | (1 octet)                       |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * |                             |                                 |
 * | ... more ...                | ... more ...                    |
 * |                             |                                 |
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Service Family ID           | Service Family version          |
 * | (1 octet)                   | (1 octet)                       |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * <p>
 * The number of Service Family can be calculated using:<br>
 * {@code (Structure Length - 1 octet (Description Type Code)) / 2}
 * <p>
 * whereas the Service Family is a pair of ID and Version. The service
 * family IDs shall be the high octet of the {@link ServiceType}.
 * For example, for KNX/IP Tunneling the ID would be {@code 0x04}.
 * <p>
 * See: KNX Standard Core
 *
 * @author PITSCHR
 */
public final class SupportedServiceFamiliesDIB implements MultiRawDataAware {
    /**
     * Minimum Structure Length for {@link SupportedServiceFamiliesDIB}
     * <p>
     * 1 byte for Structure Length<br>
     * 1 byte for Description Type Code<br>
     */
    private static final int STRUCTURE_MIN_LENGTH = 2;
    /**
     * Maximum Structure Length for {@link SupportedServiceFamiliesDIB} is 254
     */
    private static final int STRUCTURE_MAX_LENGTH = 0xFE;
    private final List<ServiceTypeFamilyVersion> serviceFamilies;
    private final byte[] bytes;

    private SupportedServiceFamiliesDIB(final byte[] bytes) {
        this.bytes = bytes.clone();

        // bytes[0] -> length not relevant
        // bytes[1] -> description type not relevant

        // Service Type Family / Version
        final var sizeOfServiceFamilies = (bytes.length - 2) / 2;
        final var tmp = new ArrayList<ServiceTypeFamilyVersion>(sizeOfServiceFamilies);
        for (var i = 2; i < bytes.length; i += 2) {
            tmp.add(new ServiceTypeFamilyVersion(ServiceTypeFamily.valueOf(bytes[i]), Byte.toUnsignedInt(bytes[i + 1])));
        }
        this.serviceFamilies = Collections.unmodifiableList(tmp);
    }

    /**
     * Builds a new {@link SupportedServiceFamiliesDIB} instance
     *
     * @param bytes complete byte array for {@link SupportedServiceFamiliesDIB}
     * @return a new immutable {@link SupportedServiceFamiliesDIB}
     */
    public static SupportedServiceFamiliesDIB of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length >= STRUCTURE_MIN_LENGTH && bytes.length <= STRUCTURE_MAX_LENGTH,
                "Incompatible structure length. Expected [{}..{}] but was: {}", STRUCTURE_MIN_LENGTH, STRUCTURE_MAX_LENGTH, bytes.length);
        Preconditions.checkArgument(bytes.length % 2 == 0,
                "Incompatible structure length. Length must be divisible by 2, but was: {}", bytes.length);
        return new SupportedServiceFamiliesDIB(bytes);
    }

    public List<ServiceTypeFamilyVersion> getServiceFamilies() {
        return serviceFamilies;
    }

    /**
     * Returns if the {@link ServiceTypeFamily} exists in the current DIB
     *
     * @param serviceTypeFamily service type family to check for its existence
     * @return {@code true} if the service type family exists, otherwise {@code false}
     */
    public boolean hasServiceTypeFamily(final ServiceTypeFamily serviceTypeFamily) {
        for (final var family : this.serviceFamilies) {
            if (serviceTypeFamily == family.getFamily()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public byte[] toByteArray() {
        return bytes.clone();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("serviceFamilies", serviceFamilies)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof SupportedServiceFamiliesDIB) {
            final var other = (SupportedServiceFamiliesDIB) obj;
            return Objects.equals(this.serviceFamilies, other.serviceFamilies)
                    && Arrays.equals(this.bytes, other.bytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceFamilies, Arrays.hashCode(bytes));
    }

}
