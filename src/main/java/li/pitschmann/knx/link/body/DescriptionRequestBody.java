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
import li.pitschmann.knx.link.body.hpai.HPAI;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;

import javax.annotation.Nonnull;

/**
 * Body for Description Request
 * <p>
 * The {@link ServiceType#DESCRIPTION_REQUEST} frame shall be sent by the KNX client to the control endpoint of
 * the KNX Net/IP router to obtain a self-description of the KNX Net/IP router device. The KNX/IP body shall contain
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
public final class DescriptionRequestBody extends AbstractMultiRawData implements RequestBody, ControlChannelRelated {
    private final HPAI controlEndpoint;

    private DescriptionRequestBody(final byte[] bytes) {
        super(bytes);

        this.controlEndpoint = HPAI.of(bytes);
    }

    /**
     * Builds a new {@link DescriptionRequestBody} instance
     *
     * @param bytes complete byte array for {@link DescriptionRequestBody}
     * @return immutable {@link DescriptionRequestBody}
     */
    public static DescriptionRequestBody valueOf(final byte[] bytes) {
        return new DescriptionRequestBody(bytes);
    }

    /**
     * Creates a new {@link DescriptionRequestBody} instance.
     * <p>
     * Per default the {@link HPAI#useDefault()} is used.
     *
     * @return immutable {@link DescriptionRequestBody}
     */
    public static DescriptionRequestBody create() {
        return create(HPAI.useDefault());
    }

    /**
     * Creates a new {@link DescriptionRequestBody} instance
     *
     * @param controlEndpoint
     * @return immutable {@link DescriptionRequestBody}
     */
    public static DescriptionRequestBody create(final HPAI controlEndpoint) {
        // validate
        if (controlEndpoint == null) {
            throw new KnxNullPointerException("controlEndpoint");
        }

        return valueOf(controlEndpoint.getRawData());
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
    @Nonnull
    public ServiceType getServiceType() {
        return ServiceType.DESCRIPTION_REQUEST;
    }

    public HPAI getControlEndpoint() {
        return this.controlEndpoint;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("controlEndpoint", this.controlEndpoint.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
