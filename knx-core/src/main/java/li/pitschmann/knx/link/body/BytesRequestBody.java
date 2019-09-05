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

package li.pitschmann.knx.link.body;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.AbstractMultiRawData;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A custom RequestBody for non-KNX-specified responses. It may be used for customized bodies
 * which are not specified in KNX or may be used for testing purposes (e.g. corrupted packets).
 * <p/>
 * It may hold 0 and up to 250 bytes. Keep in mind, that KNX body can hold up to 256 bytes in
 * total whereas 6 bytes is already reserved for KNX header (see: {@link li.pitschmann.knx.link.header.Header})
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Byte 1                      | Byte 2                          |
 * | (1 octet)                   | (1 octet)                       |
 * +- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+
 * |                                                               |
 * |                                                               |
 * | ... (more, max 250 octets in total) ...                       |
 * |                                                               |
 * |                                                               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class BytesRequestBody extends AbstractMultiRawData implements RequestBody {
    /**
     * Structure Length for {@link BytesRequestBody}
     * <p>
     * 0 ... 250 bytes<br>
     * 6 bytes is reserved for KNX header<br>
     */
    private static final int STRUCTURE_LENGTH = 250;
    private final ServiceType serviceType;

    private BytesRequestBody(final @Nonnull ServiceType serviceType, final @Nonnull byte[] bytes) {
        super(bytes);
        this.serviceType = Objects.requireNonNull(serviceType);
    }

    /**
     * Creates a new {@link BytesRequestBody} instance
     *
     * @param serviceType the service type
     * @param bytes       complete byte array for {@link ConnectionStateRequestBody}
     * @return a new immutable {@link BytesRequestBody}
     */
    @Nonnull
    public static BytesRequestBody of(final @Nonnull ServiceType serviceType, final @Nonnull byte[] bytes) {
        if (serviceType == null) {
            throw new KnxNullPointerException("serviceType");
        } else if (bytes == null) {
            throw new KnxNullPointerException("bytes");
        }
        return new BytesRequestBody(serviceType, bytes);
    }

    @Nonnull
    @Override
    public ServiceType getServiceType() {
        return serviceType;
    }

    @Override
    protected void validate(final @Nonnull byte[] rawData) {
        if (rawData.length > STRUCTURE_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData", 0, STRUCTURE_LENGTH, rawData.length, rawData);
        }
    }

    @Nonnull
    @Override
    public String toString(final boolean inclRawData) {
        final var h = MoreObjects.toStringHelper(this)
                .add("serviceType", this.serviceType);
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
