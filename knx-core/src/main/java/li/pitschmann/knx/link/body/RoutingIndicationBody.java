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
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;

import javax.annotation.Nonnull;

/**
 * Body for Routing Indication
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * |                                                               |
 * |                           cEMI frame                          |
 * |                                                               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class RoutingIndicationBody extends AbstractMultiRawData implements RequestBody, ResponseBody, MulticastChannelRelated {
    /**
     * Minimum Structure Length for {@link RoutingIndicationBody} including {@link CEMI}
     * <p>
     * 11 bytes minimum for {@link CEMI}<br>
     */
    private static final int STRUCTURE_WITH_CEMI_MIN_LENGTH = 11;
    /**
     * Maximum Structure Length for {@link RoutingIndicationBody} including {@link CEMI}
     */
    private static final int STRUCTURE_WITH_CEMI_MAX_LENGTH = 0xFF;
    private final CEMI cemi;

    private RoutingIndicationBody(final @Nonnull byte[] bytes) {
        super(bytes);
        this.cemi = CEMI.of(bytes);
    }

    /**
     * Builds a new {@link RoutingIndicationBody} instance
     *
     * @param bytes complete byte array for {@link RoutingIndicationBody}
     * @return a new immutable {@link RoutingIndicationBody}
     */
    @Nonnull
    public static RoutingIndicationBody of(final @Nonnull byte[] bytes) {
        return new RoutingIndicationBody(bytes);
    }

    /**
     * Creates a new {@link RoutingIndicationBody} instance
     *
     * @param cemi
     * @return a new immutable {@link RoutingIndicationBody}
     */
    @Nonnull
    public static RoutingIndicationBody of(final @Nonnull CEMI cemi) {
        // validate
        if (cemi == null) {
            throw new KnxNullPointerException("cemi");
        }
        return of(cemi.getRawData());
    }

    @Override
    protected void validate(final @Nonnull byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length < STRUCTURE_WITH_CEMI_MIN_LENGTH || rawData.length > STRUCTURE_WITH_CEMI_MAX_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_WITH_CEMI_MIN_LENGTH, STRUCTURE_WITH_CEMI_MAX_LENGTH, rawData.length,
                    rawData);
        }
    }

    @Nonnull
    @Override
    public ServiceType getServiceType() {
        return ServiceType.ROUTING_INDICATION;
    }

    @Nonnull
    public CEMI getCEMI() {
        return this.cemi;
    }

    @Nonnull
    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("cemi", this.cemi.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}