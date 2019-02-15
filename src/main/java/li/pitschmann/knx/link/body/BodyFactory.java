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

import li.pitschmann.knx.link.exceptions.KnxUnknownBodyException;
import li.pitschmann.knx.link.header.Header;
import li.pitschmann.knx.link.header.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Factory for {@link Body}. The proper body will be picked up based on {@link ServiceType} code.
 *
 * @author PITSCHR
 */
public final class BodyFactory {
    private static final Logger LOG = LoggerFactory.getLogger(BodyFactory.class);

    private BodyFactory() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Creates a {@link Body} for given {@code headerAndBodyBytes}. The appropriate {@link Header} will be parsed by
     * given {@code headerAndBodyBytes} as well.
     *
     * @param headerAndBodyBytes
     * @return an instance of {@link Body} or {@link KnxUnknownBodyException} in case the {@link ServiceType} (by header
     * info) is not supported.
     * @throws KnxUnknownBodyException in case the body is not known
     */
    public static <T extends Body> T valueOf(final byte[] headerAndBodyBytes) {
        final Header header = Header.valueOf(headerAndBodyBytes);
        return valueOf(header, Arrays.copyOfRange(headerAndBodyBytes, Header.KNXNET_HEADER_LENGTH, header.getTotalLength()));
    }

    /**
     * Creates a {@link Body} for given {@code bodyBytes}. The {@link Header} which contains the {@link ServiceType} is
     * used to create a proper Body instance.
     *
     * @param header    to distinguish which body instance should be used
     * @param bodyBytes
     * @return an instance of {@link Body} or {@link KnxUnknownBodyException} in case the {@link ServiceType} (by header
     * info) is not supported.
     * @throws KnxUnknownBodyException in case the body is not known
     */
    public static <T extends Body> T valueOf(final Header header, final byte[] bodyBytes) {
        return valueOf(header.getServiceType(), bodyBytes);
    }

    /**
     * Creates a {@link Body} for given {@code bodyBytes}. The {@link ServiceType} is used to create a proper Body
     * instance.
     *
     * @param serviceType to distinguish which body instance should be used
     * @param bodyBytes
     * @return an instance of {@link Body} or {@link KnxUnknownBodyException} in case the {@link ServiceType} is not
     * supported.
     * @throws KnxUnknownBodyException in case the body is not known
     */
    @SuppressWarnings("unchecked")
    public static <T extends Body> T valueOf(final ServiceType serviceType, final byte[] bodyBytes) {
        // try to find the correct body
        if (serviceType == ServiceType.TUNNELING_REQUEST) {
            return (T) TunnellingRequestBody.valueOf(bodyBytes);
        } else if (serviceType == ServiceType.TUNNELING_ACK) {
            return (T) TunnellingAckBody.valueOf(bodyBytes);
        } else if (serviceType == ServiceType.CONNECTIONSTATE_REQUEST) {
            return (T) ConnectionStateRequestBody.valueOf(bodyBytes);
        } else if (serviceType == ServiceType.CONNECTIONSTATE_RESPONSE) {
            return (T) ConnectionStateResponseBody.valueOf(bodyBytes);
        } else if (serviceType == ServiceType.DISCONNECT_REQUEST) {
            return (T) DisconnectRequestBody.valueOf(bodyBytes);
        } else if (serviceType == ServiceType.DISCONNECT_RESPONSE) {
            return (T) DisconnectResponseBody.valueOf(bodyBytes);
        } else if (serviceType == ServiceType.DESCRIPTION_REQUEST) {
            return (T) DescriptionRequestBody.valueOf(bodyBytes);
        } else if (serviceType == ServiceType.DESCRIPTION_RESPONSE) {
            return (T) DescriptionResponseBody.valueOf(bodyBytes);
        } else if (serviceType == ServiceType.CONNECT_REQUEST) {
            return (T) ConnectRequestBody.valueOf(bodyBytes);
        } else if (serviceType == ServiceType.CONNECT_RESPONSE) {
            return (T) ConnectResponseBody.valueOf(bodyBytes);
        } else {
            LOG.error("Unknown Body for ServiceType: {}", serviceType);
            throw new KnxUnknownBodyException(bodyBytes);
        }
    }
}
