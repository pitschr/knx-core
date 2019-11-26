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

import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Device Supported Service Families DIB to specify DIB for type {@link DescriptionType#SUPPORTED_SERVICE_FAMILIES}
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
 * whereas the Service Family is a pair of ID and Version. The service family IDs shall be the high octet of the
 * {@link ServiceType}. For example, for KNX/IP Tunneling the ID would be {@code 0x04}.
 * <p>
 * See: KNX Standard Core
 *
 * @author PITSCHR
 */
public final class SupportedDeviceFamiliesDIB extends AbstractDIB {
    /**
     * Minimum Structure Length for {@link SupportedDeviceFamiliesDIB}
     * <p>
     * 1 byte for Structure Length<br>
     * 1 byte for Description Type Code<br>
     */
    private static final int STRUCTURE_MIN_LENGTH = 2;
    /**
     * Maximum Structure Length for {@link SupportedDeviceFamiliesDIB} is 254
     */
    private static final int STRUCTURE_MAX_LENGTH = 0xFE;
    private final List<ServiceTypeFamilyVersion> serviceFamilies;

    private SupportedDeviceFamiliesDIB(final @Nonnull byte[] rawData) {
        super(rawData);

        // rawData[0] -> length already covered in abstract class DIB
        // rawData[1] -> description type already covered in abstract class DIB

        // Service Type Family / Version
        final var sizeOfServiceFamilies = (rawData.length - 2) / 2;
        final var tmp = new ArrayList<ServiceTypeFamilyVersion>(sizeOfServiceFamilies);
        for (var i = 2; i < rawData.length; i += 2) {
            tmp.add(new ServiceTypeFamilyVersion(ServiceTypeFamily.valueOf(rawData[i]), Bytes.toUnsignedInt(rawData[i + 1])));
        }
        this.serviceFamilies = Collections.unmodifiableList(tmp);
    }

    /**
     * Builds a new {@link SupportedDeviceFamiliesDIB} instance
     *
     * @param bytes complete byte array for {@link SupportedDeviceFamiliesDIB}
     * @return a new immutable {@link SupportedDeviceFamiliesDIB}
     */
    @Nonnull
    public static SupportedDeviceFamiliesDIB of(final @Nonnull byte[] bytes) {
        return new SupportedDeviceFamiliesDIB(bytes);
    }

    @Override
    protected void validate(final @Nonnull byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length < STRUCTURE_MIN_LENGTH || rawData.length > STRUCTURE_MAX_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_MIN_LENGTH, STRUCTURE_MAX_LENGTH, rawData.length, rawData);
        } else if (rawData.length % 2 != 0) {
            throw new KnxIllegalArgumentException(String.format("The size of 'rawData' must be divisible by two. Actual length is: %s. RawData: %s",
                    rawData.length, ByteFormatter.formatHexAsString(rawData)));
        }
    }

    @Nonnull
    public List<ServiceTypeFamilyVersion> getServiceFamilies() {
        return this.serviceFamilies;
    }

    /**
     * Returns if the {@link ServiceTypeFamily} exists in the current DIB
     *
     * @param serviceTypeFamily
     * @return {@code true} if the service type family exists, otherwise {@code false}
     */
    public boolean hasServiceTypeFamily(final @Nonnull ServiceTypeFamily serviceTypeFamily) {
        for (final var family : this.serviceFamilies) {
            if (serviceTypeFamily == family.getFamily()) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public String toString(boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("length", this.getLength() + " (" + ByteFormatter.formatHex(this.getLength()) + ")")
                .add("descriptionType", this.getDescriptionType())
                .add("serviceFamilies", this.serviceFamilies);
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }

}