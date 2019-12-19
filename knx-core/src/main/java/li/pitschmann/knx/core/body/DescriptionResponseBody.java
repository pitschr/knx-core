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
import li.pitschmann.knx.core.dib.DescriptionType;
import li.pitschmann.knx.core.dib.DeviceHardwareInformationDIB;
import li.pitschmann.knx.core.dib.IPConfigDIB;
import li.pitschmann.knx.core.dib.IPCurrentConfigDIB;
import li.pitschmann.knx.core.dib.KnxAddressesDIB;
import li.pitschmann.knx.core.dib.ManufacturerDataDIB;
import li.pitschmann.knx.core.dib.SupportedDeviceFamiliesDIB;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * Body for Description Response
 * <p>
 * The {@link ServiceType#DESCRIPTION_RESPONSE} frame shall be sent by the KNX Net/IP device as an answer to a received
 * {@link ServiceType#DESCRIPTION_REQUEST} frame. It shall be addressed to the KNX client’s control endpoint using
 * the HPAI included in the received {@link ServiceType#DESCRIPTION_REQUEST} frame.
 * <p>
 * At least two DIB structures shall be returned with information about the device capabilities on:
 * {@link DeviceHardwareInformationDIB} and {@link SupportedDeviceFamiliesDIB}.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | DIB                                                           |
 * | device hardware                                               |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | DIB                                                           |
 * | supported service families                                    |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * | DIB                                                           |
 * | other device information (optional)                           |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 *
 * @author PITSCHR
 */
public final class DescriptionResponseBody extends AbstractMultiRawData implements ResponseBody, DescriptionChannelRelated {
    private static final Logger log = LoggerFactory.getLogger(DescriptionResponseBody.class);
    private final DeviceHardwareInformationDIB deviceHardwareInformation;
    private final SupportedDeviceFamiliesDIB supportedDeviceFamilies;
    private final IPConfigDIB ipConfig;
    private final IPCurrentConfigDIB ipCurrentConfig;
    private final KnxAddressesDIB knxAddresses;
    private final ManufacturerDataDIB manufacturerData;

    private DescriptionResponseBody(final byte[] bytes) {
        super(bytes);

        // mandatory
        this.deviceHardwareInformation = DeviceHardwareInformationDIB.of(this.getArrayPartByDIB(DescriptionType.DEVICE_INFO, bytes));
        this.supportedDeviceFamilies = SupportedDeviceFamiliesDIB.of(this.getArrayPartByDIB(DescriptionType.SUPPORTED_SERVICE_FAMILIES, bytes));

        // optional
        byte[] ipConfigArray = this.getArrayPartByDIB(DescriptionType.IP_CONFIG, bytes);
        this.ipConfig = ipConfigArray == null ? null : IPConfigDIB.of(ipConfigArray);

        byte[] ipCurrentConfigArray = this.getArrayPartByDIB(DescriptionType.IP_CURRENT_CONFIG, bytes);
        this.ipCurrentConfig = ipCurrentConfigArray == null ? null : IPCurrentConfigDIB.of(ipCurrentConfigArray);

        byte[] knxAddressesArray = this.getArrayPartByDIB(DescriptionType.KNX_ADDRESSES, bytes);
        this.knxAddresses = knxAddressesArray == null ? null : KnxAddressesDIB.of(knxAddressesArray);

        byte[] manufacturerDataArray = this.getArrayPartByDIB(DescriptionType.MANUFACTURER_DATA, bytes);
        this.manufacturerData = manufacturerDataArray == null ? null : ManufacturerDataDIB.of(manufacturerDataArray);
    }

    /**
     * Builds a new {@link DescriptionResponseBody} instance
     *
     * @param bytes complete byte array for {@link DescriptionResponseBody}
     * @return a new immutable {@link DescriptionResponseBody}
     */
    public static DescriptionResponseBody of(final byte[] bytes) {
        return new DescriptionResponseBody(bytes);
    }

    /**
     * Creates a new {@link DescriptionResponseBody} instance
     *
     * @param deviceHardwareInformation information about device hardware
     * @param supportedDeviceFamilies supported device families
     * @return a new immutable {@link DescriptionResponseBody}
     */
    public static DescriptionResponseBody of(final DeviceHardwareInformationDIB deviceHardwareInformation,
                                             final SupportedDeviceFamiliesDIB supportedDeviceFamilies) {
        // validate
        if (deviceHardwareInformation == null) {
            throw new KnxNullPointerException("deviceHardwareInformation");
        } else if (supportedDeviceFamilies == null) {
            throw new KnxNullPointerException("supportedDeviceFamilies");
        }

        final var deviceHardwareInformationAsBytes = deviceHardwareInformation.getRawData();
        final var deviceFamiliesAsBytes = supportedDeviceFamilies.getRawData();

        final var totalLength = deviceHardwareInformationAsBytes.length + deviceFamiliesAsBytes.length;

        // create bytes
        final var bytes = new byte[totalLength];
        var pos = 0;
        System.arraycopy(deviceHardwareInformationAsBytes, 0, bytes, pos, deviceHardwareInformationAsBytes.length);
        pos += deviceHardwareInformationAsBytes.length;
        System.arraycopy(deviceFamiliesAsBytes, 0, bytes, pos, deviceFamiliesAsBytes.length);

        return of(bytes);
    }

    @Override
    protected void validate(final byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else {
            // mandatory are device information DIB and supported device families DIB
            final var deviceInformationFound = this.indexOfDIB(DescriptionType.DEVICE_INFO, rawData) >= 0;
            if (!deviceInformationFound) {
                throw new KnxException("Could not find device hardware information DIB array.");
            }
            final var supportedDeviceFamiliesFound = this.indexOfDIB(DescriptionType.SUPPORTED_SERVICE_FAMILIES, rawData) >= 0;
            if (!supportedDeviceFamiliesFound) {
                throw new KnxException("Could not find supported device families DIB array.");
            }
            this.getArrayPartByDIB(DescriptionType.MANUFACTURER_DATA, rawData);
        }
    }

    /**
     * Returns the index of {@link DescriptionType} DIB we are looking in {@code rawData} array.
     *
     * @param descriptionType type of description we are looking for
     * @param rawData byte array to be scanned
     * @return positive number if found, otherwise {@code -1}.
     */
    private int indexOfDIB(final DescriptionType descriptionType, final byte[] rawData) {
        var index = -1;
        for (var i = 0; i < rawData.length; ) {
            final var dibLength = Bytes.toUnsignedInt(rawData[i]);
            final var dibCode = Bytes.toUnsignedInt(rawData[i + 1]);
            // we are interested in 2nd byte of DIB only
            if (descriptionType.getCode() == dibCode) {
                // found it!
                index = i;
                break;
            } else {
                // not found - try with next one
                if (dibLength == 0) {
                    throw new KnxException(String.format("Bad rawData provided. This would result into an endless loop: %s", ByteFormatter.formatHexAsString(rawData)));
                }
                i += dibLength;
            }
        }

        log.debug("Position of DIB '{}': {}", descriptionType.getFriendlyName(), index);
        return index;
    }

    /**
     * Returns the array part for the given {@link DescriptionType} DIB.
     *
     * @param descriptionType type of description we are looking for
     * @param rawData byte array to be scanned
     * @return byte array if found, otherwise {@code null}
     */
    private byte[] getArrayPartByDIB(final DescriptionType descriptionType, final byte[] rawData) {
        Preconditions.checkNonNull(descriptionType);
        final var index = this.indexOfDIB(descriptionType, rawData);

        if (index < 0) {
            // not found
            if (log.isDebugEnabled()) {
                log.debug("DIB '{}' not found in: {}", descriptionType.getFriendlyName(), ByteFormatter.formatHexAsString(rawData));
            }
            return null;
        } else {
            // found
            final var dibLength = Bytes.toUnsignedInt(rawData[index]);
            final var dibArray = new byte[dibLength];
            System.arraycopy(rawData, index, dibArray, 0, dibLength);
            if (log.isDebugEnabled()) {
                log.debug("DIB '{}' found: {}", descriptionType.getFriendlyName(), ByteFormatter.formatHexAsString(dibArray));
            }
            return dibArray;
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DESCRIPTION_RESPONSE;
    }

    public DeviceHardwareInformationDIB getDeviceInformation() {
        return this.deviceHardwareInformation;
    }

    public SupportedDeviceFamiliesDIB getSupportedDeviceFamilies() {
        return this.supportedDeviceFamilies;
    }

    @Nullable
    public IPConfigDIB getIPConfig() {
        return this.ipConfig;
    }

    @Nullable
    public IPCurrentConfigDIB getIPCurrentConfig() {
        return this.ipCurrentConfig;
    }

    @Nullable
    public KnxAddressesDIB getKnxAddresses() {
        return this.knxAddresses;
    }

    @Nullable
    public ManufacturerDataDIB getManufacturerData() {
        return this.manufacturerData;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                // mandatory
                .add("deviceHardwareInformation", this.deviceHardwareInformation.toString(false))
                .add("supportedDeviceFamilies", this.supportedDeviceFamilies.toString(false))
                // optional
                .add("ipConfig", this.ipConfig == null ? null : this.ipConfig.toString(false))
                .add("ipCurrentConfig", this.ipCurrentConfig == null ? null : this.ipCurrentConfig.toString(false))
                .add("knxAddresses", this.knxAddresses == null ? null : this.knxAddresses.toString(false))
                .add("manufacturerData", this.manufacturerData == null ? null : this.manufacturerData.toString(false));
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
