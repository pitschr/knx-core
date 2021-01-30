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
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Body for Description Request
 * <p>
 * The {@link ServiceType#DESCRIPTION_REQUEST} frame shall be sent
 * by the KNXnet/IP Client to the control endpoint of the KNXnet/IP Server
 * to obtain a self-description of the KNXnet/IP Server device.
 * <p>
 * The KNXnet/IP body shall contain the return address information
 * of the KNXnet/IP Client’s control endpoint.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | Control endpoint                                              |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class DescriptionRequestBody implements RequestBody, DescriptionChannelRelated {
    /**
     * Structure Length for {@link DescriptionRequestBody}
     * <p>
     * 8 bytes for HPAI<br>
     */
    private static final int STRUCTURE_LENGTH = HPAI.KNXNET_HPAI_LENGTH;
    private static final DescriptionRequestBody DEFAULT = of(HPAI.useDefault());
    private final HPAI controlEndpoint;

    private DescriptionRequestBody(final byte[] bytes) {
        this(
                // bytes[0..7] => control endpoint
                HPAI.of(bytes)
        );
    }

    private DescriptionRequestBody(final HPAI controlEndpoint) {
        Preconditions.checkNonNull(controlEndpoint, "Control Endpoint is required.");
        this.controlEndpoint = controlEndpoint;
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
        Preconditions.checkArgument(bytes.length == STRUCTURE_LENGTH,
                "Incompatible structure length. Expected '{}' but was: {}", STRUCTURE_LENGTH, bytes.length);
        return new DescriptionRequestBody(bytes);
    }

    /**
     * Creates a new {@link DescriptionRequestBody} instance
     *
     * @param controlEndpoint {@link HPAI} of control endpoint
     * @return a new immutable {@link DescriptionRequestBody}
     */
    public static DescriptionRequestBody of(final HPAI controlEndpoint) {
        return new DescriptionRequestBody(controlEndpoint);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DESCRIPTION_REQUEST;
    }

    public HPAI getControlEndpoint() {
        return controlEndpoint;
    }

    @Override
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        return controlEndpoint.getRawData();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("controlEndpoint", controlEndpoint)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof DescriptionRequestBody) {
            final var other = (DescriptionRequestBody) obj;
            return Objects.equals(this.controlEndpoint, other.controlEndpoint);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlEndpoint);
    }
}
