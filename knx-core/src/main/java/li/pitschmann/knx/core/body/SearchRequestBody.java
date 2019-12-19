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

import li.pitschmann.knx.core.AbstractMultiRawData;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.utils.Strings;

/**
 * Body for Search Request
 * <p/>
 * The {@link ServiceType#SEARCH_REQUEST} frame shall be sent by a KNXnet/IP Client
 * via multicast to the discovery endpoints of any listening KNXnet/IP Server. As
 * communication with the discovery endpoint shall be connectionless and stateless,
 * the KNXnet/IP Client’s discovery endpoint address information shall be included
 * in the KNXnet/IP body.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Discovery endpoint                                            |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class SearchRequestBody extends AbstractMultiRawData implements RequestBody, MulticastChannelRelated {
    private static final SearchRequestBody DEFAULT = of(HPAI.useDefault());
    private final HPAI discoveryEndpoint;

    private SearchRequestBody(final byte[] bytes) {
        super(bytes);

        this.discoveryEndpoint = HPAI.of(bytes);
    }

    /**
     * Builds a new {@link SearchRequestBody} instance
     *
     * @param bytes complete byte array for {@link SearchRequestBody}
     * @return a new immutable {@link SearchRequestBody}
     */
    public static SearchRequestBody of(final byte[] bytes) {
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
        // validate
        if (discoveryEndpoint == null) {
            throw new KnxNullPointerException("discoveryEndpoint");
        }
        return of(discoveryEndpoint.getRawData());
    }

    @Override
    protected void validate(final byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length != 8) {
            // 8 bytes for HPAI
            throw new KnxNumberOutOfRangeException("rawData", 8, 8, rawData.length, rawData);
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.SEARCH_REQUEST;
    }

    public HPAI getDiscoveryEndpoint() {
        return this.discoveryEndpoint;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("discoveryEndpoint", this.discoveryEndpoint.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
