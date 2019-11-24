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
import li.pitschmann.knx.core.body.dib.DeviceHardwareInformationDIB;
import li.pitschmann.knx.core.body.dib.SupportedDeviceFamiliesDIB;
import li.pitschmann.knx.core.body.hpai.HPAI;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Body for Search Response
 * <p/>
 * The {@link ServiceType#SEARCH_RESPONSE} frame shall be sent by the KNXnet/IP
 * Server as an answer to a received SEARCH_REQUEST frame. It shall be addressed
 * to the KNXnet/IP Client’s discovery endpoint using the HPAI included in the
 * received {@link ServiceType#SEARCH_REQUEST} frame.
 * <p/>
 * The HPAI of the KNXnet/IP Server’s own control endpoint shall be carried in the
 * KNXnet/IP body of the SEARCH_RESPONSE frame along with the description of the
 * device hardware and the supported service families. If the KNXnet/IP Server
 * supports more than one KNX connection, the KNXnet/IP Server shall announce each
 * of its own control endpoints in a single SEARCH_RESPONSE frame.
 * <p/>
 * At least two DIB structures shall be returned with information about the device capabilities on:
 * {@link DeviceHardwareInformationDIB} and {@link SupportedDeviceFamiliesDIB}.
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
 *
 * @author PITSCHR
 */
public final class SearchResponseBody extends AbstractMultiRawData implements ResponseBody, MulticastChannelRelated {
    /**
     * Structure Length for {@link SearchResponseBody}
     * <p>
     * 2 bytes for control endpoint<br>
     * 54 bytes for device hardware information<br>
     * min. 2 bytes for supported service families<br>
     */
    private static final int STRUCTURE_MIN_LENGTH = 2 + 54 + 2;
    /**
     * Maximum Structure Length for {@link SearchResponseBody} is 254
     */
    private static final int STRUCTURE_MAX_LENGTH = 0xFE;
    private final HPAI controlEndpoint;
    private final DeviceHardwareInformationDIB deviceHardwareInformation;
    private final SupportedDeviceFamiliesDIB supportedDeviceFamilies;

    private SearchResponseBody(final @Nonnull byte[] bytes) {
        super(bytes);

        int pos = 0;
        // HPAI .. 8 bytes
        this.controlEndpoint = HPAI.of(Arrays.copyOfRange(bytes, pos, pos += HPAI.KNXNET_HPAI_LENGTH));
        // Device Hardware Information .. 54 bytes
        this.deviceHardwareInformation = DeviceHardwareInformationDIB.of(Arrays.copyOfRange(bytes, pos, pos += DeviceHardwareInformationDIB.STRUCTURE_LENGTH));
        // Supported Device Families
        this.supportedDeviceFamilies = SupportedDeviceFamiliesDIB.of(Arrays.copyOfRange(bytes, pos, bytes.length));
    }

    /**
     * Builds a new {@link SearchResponseBody} instance
     *
     * @param bytes complete byte array for {@link SearchResponseBody}
     * @return a new immutable {@link SearchResponseBody}
     */
    @Nonnull
    public static SearchResponseBody of(final @Nonnull byte[] bytes) {
        return new SearchResponseBody(bytes);
    }

    /**
     * Creates a new {@link SearchResponseBody} instance
     *
     * @param controlEndpoint
     * @param deviceHardwareInformation
     * @param supportedDeviceFamilies
     * @return a new immutable {@link SearchResponseBody}
     */
    @Nonnull
    public static SearchResponseBody of(final @Nonnull HPAI controlEndpoint,
                                        final @Nonnull DeviceHardwareInformationDIB deviceHardwareInformation,
                                        final @Nonnull SupportedDeviceFamiliesDIB supportedDeviceFamilies) {
        // validate
        if (controlEndpoint == null) {
            throw new KnxNullPointerException("controlEndpoint");
        } else if (deviceHardwareInformation == null) {
            throw new KnxNullPointerException("deviceHardwareInformation");
        } else if (supportedDeviceFamilies == null) {
            throw new KnxNullPointerException("supportedDeviceFamilies");
        }

        final var controlEndpointAsBytes = controlEndpoint.getRawData();
        final var deviceHardwareInformationAsBytes = deviceHardwareInformation.getRawData();
        final var deviceFamiliesAsBytes = supportedDeviceFamilies.getRawData();

        final var totalLength = controlEndpointAsBytes.length + //
                deviceHardwareInformationAsBytes.length + //
                deviceFamiliesAsBytes.length;

        // create bytes
        final var bytes = new byte[totalLength];
        var pos = 0;
        System.arraycopy(controlEndpointAsBytes, 0, bytes, pos, controlEndpointAsBytes.length);
        pos += controlEndpointAsBytes.length;
        System.arraycopy(deviceHardwareInformationAsBytes, 0, bytes, pos, deviceHardwareInformationAsBytes.length);
        pos += deviceHardwareInformationAsBytes.length;
        System.arraycopy(deviceFamiliesAsBytes, 0, bytes, pos, deviceFamiliesAsBytes.length);

        return of(bytes);
    }

    @Override
    protected void validate(final @Nonnull byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length < STRUCTURE_MIN_LENGTH || rawData.length > STRUCTURE_MAX_LENGTH) {
            throw new KnxNumberOutOfRangeException("rawData", STRUCTURE_MIN_LENGTH, STRUCTURE_MAX_LENGTH, rawData.length, rawData);
        } else if (rawData.length % 2 != 0) {
            throw new KnxIllegalArgumentException(String.format("The size of 'rawData' must be divisible by two. Actual length is: %s. RawData: %s",
                    rawData.length, ByteFormatter.formatHexAsString(rawData)));
        } else {
            // mandatory are device information DIB and supported device families DIB
            if (rawData[HPAI.KNXNET_HPAI_LENGTH + 1] != 0x01) {
                throw new KnxException("Could not find device hardware information DIB array.");
            }
            if (rawData[HPAI.KNXNET_HPAI_LENGTH + DeviceHardwareInformationDIB.STRUCTURE_LENGTH + 1] != 0x02) {
                throw new KnxException("Could not find supported device families DIB array.");
            }
        }
    }

    @Nonnull
    @Override
    public ServiceType getServiceType() {
        return ServiceType.SEARCH_RESPONSE;
    }

    @Nonnull
    public HPAI getControlEndpoint() {
        return this.controlEndpoint;
    }

    @Nonnull
    public DeviceHardwareInformationDIB getDeviceInformation() {
        return this.deviceHardwareInformation;
    }

    @Nonnull
    public SupportedDeviceFamiliesDIB getSupportedDeviceFamilies() {
        return this.supportedDeviceFamilies;
    }

    @Nonnull
    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("controlEndpoint", this.controlEndpoint.toString(false))
                .add("deviceHardwareInformation", this.deviceHardwareInformation.toString(false))
                .add("supportedDeviceFamilies", this.supportedDeviceFamilies.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
