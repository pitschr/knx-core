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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.CEMIAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

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
public final class RoutingIndicationBody implements RequestBody, ResponseBody, CEMIAware, MulticastChannelRelated {
    /**
     * Minimum Structure Length for {@link RoutingIndicationBody} including {@link CEMI}
     * <p>
     * 11 bytes minimum for {@link CEMI}<br>
     */
    private static final int STRUCTURE_WITH_CEMI_MIN_LENGTH = 11;
    /**
     * Maximum Structure Length for {@link RoutingIndicationBody} including {@link CEMI}
     */
    private static final int STRUCTURE_WITH_CEMI_MAX_LENGTH = 255;
    private final CEMI cemi;

    private RoutingIndicationBody(final byte[] bytes) {
        this(
                // byte[0..255] => CEMI
                CEMI.of(bytes)
        );
    }

    private RoutingIndicationBody(final CEMI cemi) {
        Preconditions.checkNonNull(cemi, "CEMI is required.");
        this.cemi = cemi;
    }

    /**
     * Builds a new {@link RoutingIndicationBody} instance
     *
     * @param bytes complete byte array for {@link RoutingIndicationBody}
     * @return a new immutable {@link RoutingIndicationBody}
     */
    public static RoutingIndicationBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length >= STRUCTURE_WITH_CEMI_MIN_LENGTH && bytes.length <= STRUCTURE_WITH_CEMI_MAX_LENGTH,
                "Incompatible structure length. Expected [{}..{}] but was: {}", STRUCTURE_WITH_CEMI_MIN_LENGTH, STRUCTURE_WITH_CEMI_MAX_LENGTH, bytes.length);
        return new RoutingIndicationBody(bytes);
    }

    /**
     * Creates a new {@link RoutingIndicationBody} instance
     *
     * @param cemi the {@link CEMI} to be sent
     * @return a new immutable {@link RoutingIndicationBody}
     */
    public static RoutingIndicationBody of(final CEMI cemi) {
        return new RoutingIndicationBody(cemi);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ROUTING_INDICATION;
    }

    @Override
    public CEMI getCEMI() {
        return cemi;
    }

    @Override
    public byte[] toByteArray() {
        return this.cemi.toByteArray();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("cemi", cemi)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof RoutingIndicationBody) {
            final var other = (RoutingIndicationBody) obj;
            return Objects.equals(this.cemi, other.cemi);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cemi);
    }
}
