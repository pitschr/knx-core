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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * A custom ResponseBody for non-KNX-specified requests and responses. It may be used for
 * customized bodies which are not specified in KNX or may be used for testing purposes
 * (e.g. corrupted packets).
 * <p>
 * It may hold 0 and up to 250 bytes. Keep in mind, that KNX body can hold up to 256 bytes in
 * total whereas 6 bytes is already reserved for KNX header
 * <p>
 * See: {@link li.pitschmann.knx.core.header.Header}
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
public final class BytesBody implements Body {
    /**
     * Structure Length for {@link BytesBody}
     * <p>
     * 0 ... 250 bytes<br>
     * 6 bytes is reserved for KNX header<br>
     */
    private static final int STRUCTURE_LENGTH = 250;
    private final ServiceType serviceType;
    private final byte[] bytes;

    private BytesBody(final ServiceType serviceType, final byte[] bytes) {
        Preconditions.checkNonNull(serviceType, "Service type is required.");
        Preconditions.checkNonNull(bytes, "Bytes is required.");
        Preconditions.checkArgument(bytes.length <= STRUCTURE_LENGTH,
                "Length of bytes may not exceed '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);

        this.serviceType = serviceType;
        this.bytes = bytes.clone();

    }

    /**
     * Creates a new {@link BytesBody} instance
     *
     * @param serviceType the service type
     * @param bytes       complete byte array
     * @return a new immutable {@link BytesBody}
     */
    public static BytesBody of(final ServiceType serviceType, final byte[] bytes) {
        return new BytesBody(serviceType, bytes);
    }

    @Override
    public ServiceType getServiceType() {
        return serviceType;
    }


    @Override
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        return bytes.clone();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("serviceType", serviceType)
                .add("bytes", ByteFormatter.formatHexAsString(bytes))
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof BytesBody) {
            final var other = (BytesBody) obj;
            return Objects.equals(this.serviceType, other.serviceType)
                    && Arrays.equals(this.bytes, other.bytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceType, Arrays.hashCode(bytes));
    }
}
