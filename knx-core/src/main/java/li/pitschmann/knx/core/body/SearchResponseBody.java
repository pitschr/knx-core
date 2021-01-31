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
import li.pitschmann.knx.core.dib.DeviceInformationDIB;
import li.pitschmann.knx.core.dib.SupportedServiceFamiliesDIB;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.net.HPAI;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Body for Search Response
 * <p>
 * The {@link ServiceType#SEARCH_RESPONSE} frame shall be sent by
 * the KNXnet/IP Server as an answer to a received {@link ServiceType#SEARCH_REQUEST}
 * frame. It shall be addressed to the KNXnet/IP Client’s discovery
 * endpoint using the HPAI included in the received {@link ServiceType#SEARCH_REQUEST}
 * frame.
 * <p>
 * The HPAI of the KNXnet/IP Server’s own control endpoint shall be
 * carried in the KNXnet/IP body of the {@link ServiceType#SEARCH_RESPONSE}
 * frame along with the description of the device hardware and the
 * supported service families. If the KNXnet/IP Server supports more
 * than one KNX connection, the KNXnet/IP Server shall announce each
 * of its own control endpoints in a single {@link ServiceType#SEARCH_RESPONSE}
 * frame.
 * <p>
 * At least two DIB structures shall be returned with information about
 * the device capabilities on: {@link DeviceInformationDIB} and
 * {@link SupportedServiceFamiliesDIB}.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | HPAI                                                          |
 * | control endpoint                                              |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | DIB                                                           |
 * | device hardware                                               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | DIB                                                           |
 * | supported service families                                    |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class SearchResponseBody implements ResponseBody, MulticastChannelRelated {
    /**
     * Structure Length for {@link SearchResponseBody}
     * <p>
     * 2 bytes for control endpoint<br>
     * 54 bytes for device information<br>
     * min. 2 bytes for supported service families<br>
     */
    private static final int STRUCTURE_MIN_LENGTH = 2 + 54 + 2;
    /**
     * Maximum Structure Length for {@link SearchResponseBody} is 254
     */
    private static final int STRUCTURE_MAX_LENGTH = 254;
    private final HPAI controlEndpoint;
    private final DeviceInformationDIB deviceInformation;
    private final SupportedServiceFamiliesDIB supportedServiceFamilies;

    private SearchResponseBody(final byte[] bytes) {
        this(
                // bytes[0..7] => control endpoint
                HPAI.of(Arrays.copyOfRange(bytes, 0, 8)),
                // bytes[8..61] => device information
                DeviceInformationDIB.of(Arrays.copyOfRange(bytes, 8, 62)),
                // bytes[62..]
                SupportedServiceFamiliesDIB.of(Arrays.copyOfRange(bytes, 62, bytes.length))
        );
    }

    private SearchResponseBody(final HPAI controlEndpoint,
                               final DeviceInformationDIB deviceInformation,
                               final SupportedServiceFamiliesDIB supportedServiceFamilies) {
        Preconditions.checkNonNull(controlEndpoint, "Control Endpoint is required.");
        Preconditions.checkNonNull(deviceInformation, "DIB about Device Information is required.");
        Preconditions.checkNonNull(supportedServiceFamilies, "DIB about Supported Service Families is required.");

        this.controlEndpoint = controlEndpoint;
        this.deviceInformation = deviceInformation;
        this.supportedServiceFamilies = supportedServiceFamilies;
    }

    /**
     * Builds a new {@link SearchResponseBody} instance
     *
     * @param bytes complete byte array for {@link SearchResponseBody}
     * @return a new immutable {@link SearchResponseBody}
     */
    public static SearchResponseBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length >= STRUCTURE_MIN_LENGTH && bytes.length <= STRUCTURE_MAX_LENGTH,
                "Incompatible structure length. Expected [{}..{}] but was: {}", STRUCTURE_MIN_LENGTH, STRUCTURE_MAX_LENGTH, bytes.length);
        Preconditions.checkArgument(bytes[HPAI.STRUCTURE_LENGTH + 1] == 0x01,
                "Incompatible structure. No Device Information DIB.");
        Preconditions.checkArgument(bytes[HPAI.STRUCTURE_LENGTH + DeviceInformationDIB.STRUCTURE_LENGTH + 1] == 0x02,
                "Incompatible structure. No Supported Service Families DIB.");
        return new SearchResponseBody(bytes);
    }

    /**
     * Creates a new {@link SearchResponseBody} instance
     *
     * @param controlEndpoint          {@link HPAI} of control endpoint
     * @param deviceInformation        information about device hardware
     * @param supportedServiceFamilies information about supported service families
     * @return a new immutable {@link SearchResponseBody}
     */
    public static SearchResponseBody of(final HPAI controlEndpoint,
                                        final DeviceInformationDIB deviceInformation,
                                        final SupportedServiceFamiliesDIB supportedServiceFamilies) {
        return new SearchResponseBody(controlEndpoint, deviceInformation, supportedServiceFamilies);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.SEARCH_RESPONSE;
    }

    public HPAI getControlEndpoint() {
        return controlEndpoint;
    }

    public DeviceInformationDIB getDeviceInformation() {
        return deviceInformation;
    }

    public SupportedServiceFamiliesDIB getSupportedDeviceFamilies() {
        return supportedServiceFamilies;
    }

    @Override
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        final var controlEndpointAsBytes = controlEndpoint.getRawData();
        final var deviceInformationAsBytes = deviceInformation.getRawData();
        final var deviceFamiliesAsBytes = supportedServiceFamilies.getRawData();

        final var totalLength = controlEndpointAsBytes.length + //
                deviceInformationAsBytes.length + //
                deviceFamiliesAsBytes.length;

        // create bytes
        final var bytes = new byte[totalLength];
        var pos = 0;
        System.arraycopy(controlEndpointAsBytes, 0, bytes, pos, controlEndpointAsBytes.length);
        pos += controlEndpointAsBytes.length;
        System.arraycopy(deviceInformationAsBytes, 0, bytes, pos, deviceInformationAsBytes.length);
        pos += deviceInformationAsBytes.length;
        System.arraycopy(deviceFamiliesAsBytes, 0, bytes, pos, deviceFamiliesAsBytes.length);

        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("controlEndpoint", controlEndpoint)
                .add("deviceInformation", deviceInformation)
                .add("supportedServiceFamilies", supportedServiceFamilies)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof SearchResponseBody) {
            final var other = (SearchResponseBody) obj;
            return Objects.equals(this.controlEndpoint, other.controlEndpoint)
                    && Objects.equals(this.deviceInformation, other.deviceInformation)
                    && Objects.equals(this.supportedServiceFamilies, other.supportedServiceFamilies);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlEndpoint, deviceInformation, supportedServiceFamilies);
    }
}
