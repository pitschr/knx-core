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
import li.pitschmann.knx.core.body.hpai.HPAI;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Strings;


/**
 * Body for Description Request
 * <p>
 * The {@link ServiceType#DESCRIPTION_REQUEST} frame shall be sent by the KNX client to the control endpoint of
 * the KNX Net/IP device to obtain a self-description of the KNX Net/IP device device. The KNX/IP body shall contain
 * the return address information of the KNX client’s control endpoint.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Control endpoint                                              |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class DescriptionRequestBody extends AbstractMultiRawData implements RequestBody, DescriptionChannelRelated {
    private static final DescriptionRequestBody DEFAULT = of(HPAI.useDefault());
    private final HPAI controlEndpoint;

    private DescriptionRequestBody(final byte[] bytes) {
        super(bytes);

        this.controlEndpoint = HPAI.of(bytes);
    }

    /**
     * Returns the default {@link DescriptionRequestBody} instance.
     * <p>
     * Per default the {@link HPAI#useDefault()} is used.
     *
     * @return re-usable immutable {@link DescriptionRequestBody}
     */
    public static DescriptionRequestBody useDefault() {
        return DEFAULT;
    }

    /**
     * Builds a new {@link DescriptionRequestBody} instance
     *
     * @param bytes complete byte array for {@link DescriptionRequestBody}
     * @return a new immutable {@link DescriptionRequestBody}
     */
    public static DescriptionRequestBody of(final byte[] bytes) {
        return new DescriptionRequestBody(bytes);
    }

    /**
     * Creates a new {@link DescriptionRequestBody} instance
     *
     * @param controlEndpoint
     * @return a new immutable {@link DescriptionRequestBody}
     */
    public static DescriptionRequestBody of(final HPAI controlEndpoint) {
        // validate
        if (controlEndpoint == null) {
            throw new KnxNullPointerException("controlEndpoint");
        }

        return of(controlEndpoint.getRawData());
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
        return ServiceType.DESCRIPTION_REQUEST;
    }


    public HPAI getControlEndpoint() {
        return this.controlEndpoint;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("controlEndpoint", this.controlEndpoint.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
