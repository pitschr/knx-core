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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Body for Search Request
 * <p>
 * The {@link ServiceType#SEARCH_REQUEST} frame shall be sent by a
 * KNXnet/IP Client via multicast to the discovery endpoints of any
 * listening KNXnet/IP Server. As communication with the discovery
 * endpoint shall be connectionless and stateless, the KNXnet/IP
 * Client’s discovery endpoint address information shall be included
 * in the KNXnet/IP body.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Discovery endpoint                                            |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class SearchRequestBody implements RequestBody, MulticastChannelRelated {
    /**
     * Structure Length for {@link SearchRequestBody}
     * <p>
     * 8 bytes for HPAI<br>
     */
    private static final int STRUCTURE_LENGTH = HPAI.KNXNET_HPAI_LENGTH;
    private static final SearchRequestBody DEFAULT = of(HPAI.useDefault());
    private final HPAI discoveryEndpoint;

    private SearchRequestBody(final byte[] bytes) {
        this(
                // bytes[0..7] => discovery endpoint
                HPAI.of(bytes)
        );
    }

    private SearchRequestBody(final HPAI discoveryEndpoint) {
        Preconditions.checkNonNull(discoveryEndpoint, "Discovery Endpoint is required.");
        this.discoveryEndpoint = discoveryEndpoint;
    }

    /**
     * Builds a new {@link SearchRequestBody} instance
     *
     * @param bytes complete byte array for {@link SearchRequestBody}
     * @return a new immutable {@link SearchRequestBody}
     */
    public static SearchRequestBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new SearchRequestBody(bytes);
    }

    /**
     * Returns a default {@link SearchRequestBody} instance.
     * <p>
     * Per default the {@link HPAI#useDefault()} is used.
     *
     * @return a new immutable {@link SearchRequestBody}
     */
    public static SearchRequestBody useDefault() {
        return DEFAULT;
    }

    /**
     * Creates a new {@link SearchRequestBody} instance
     *
     * @param discoveryEndpoint {@link HPAI} of discovery endpoint
     * @return a new immutable {@link SearchRequestBody}
     */
    public static SearchRequestBody of(final HPAI discoveryEndpoint) {
        return new SearchRequestBody(discoveryEndpoint);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.SEARCH_REQUEST;
    }

    public HPAI getDiscoveryEndpoint() {
        return discoveryEndpoint;
    }

    @Override
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        return discoveryEndpoint.getRawData();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("discoveryEndpoint", discoveryEndpoint)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof SearchRequestBody) {
            final var other = (SearchRequestBody) obj;
            return Objects.equals(this.discoveryEndpoint, other.discoveryEndpoint);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(discoveryEndpoint);
    }
}
