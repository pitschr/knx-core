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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.dib.DescriptionType;
import li.pitschmann.knx.core.dib.DeviceInformationDIB;
import li.pitschmann.knx.core.dib.IPConfigDIB;
import li.pitschmann.knx.core.dib.IPCurrentConfigDIB;
import li.pitschmann.knx.core.dib.KnxAddressesDIB;
import li.pitschmann.knx.core.dib.ManufacturerDataDIB;
import li.pitschmann.knx.core.dib.SupportedServiceFamiliesDIB;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;

/**
 * Body for Description Response
 * <p>
 * The {@link ServiceType#DESCRIPTION_RESPONSE} frame shall be sent by the KNXnet/IP
 * Server as an answer to a received {@link ServiceType#DESCRIPTION_REQUEST} frame.
 * It shall be addressed to the KNXnet/IP Client’s control endpoint using the HPAI
 * included in the received {@link ServiceType#DESCRIPTION_REQUEST} frame.
 * <p>
 * The size of the KNXnet/IP body varies depending on the number of DIB structures
 * sent by the KNXnet/IP Server in response to the KNXnet/IP Client’s {@link ServiceType#DESCRIPTION_REQUEST}.
 * <p>
 * At least two DIB structures shall be returned with information about the device capabilities on:
 * {@link DeviceInformationDIB} and {@link SupportedServiceFamiliesDIB}.
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
 * Source: KNX Specification, Core
 *
 * @author PITSCHR
 */
public final class DescriptionResponseBody implements ResponseBody, DescriptionChannelRelated {
    private static final Logger log = LoggerFactory.getLogger(DescriptionResponseBody.class);

    /**
     * Minimum Structure Length for {@link DescriptionResponseBody}
     * <p>
     * 54 bytes for {@link DeviceInformationDIB}<br>
     * 2 bytes for {@link SupportedServiceFamiliesDIB} at minimum<br>
     * (others are optional)
     */
    private static final int STRUCTURE_MIN_LENGTH = DeviceInformationDIB.STRUCTURE_LENGTH + 2;

    /**
     * Maximum Structure Length for {@link DescriptionResponseBody}
     */
    private static final int STRUCTURE_MAX_LENGTH = 255;

    private final DeviceInformationDIB deviceInformation;
    private final SupportedServiceFamiliesDIB supportedServiceFamilies;
    private final IPConfigDIB ipConfig;
    private final IPCurrentConfigDIB ipCurrentConfig;
    private final KnxAddressesDIB knxAddresses;
    private final ManufacturerDataDIB manufacturerData;

    private DescriptionResponseBody(final byte[] bytes) {
        this(
                // device information
                getDIB(bytes, DescriptionType.DEVICE_INFO, DeviceInformationDIB::of),
                // supported service families
                getDIB(bytes, DescriptionType.SUPPORTED_SERVICE_FAMILIES, SupportedServiceFamiliesDIB::of),
                // (optional) - IP Config
                getDIB(bytes, DescriptionType.IP_CONFIG, IPConfigDIB::of),
                // (optional) - IP Current Config
                getDIB(bytes, DescriptionType.IP_CURRENT_CONFIG, IPCurrentConfigDIB::of),
                // (optional) - KNX Address
                getDIB(bytes, DescriptionType.KNX_ADDRESSES, KnxAddressesDIB::of),
                // (optional) - Manufacturer Data
                getDIB(bytes, DescriptionType.MANUFACTURER_DATA, ManufacturerDataDIB::of)
        );
    }

    private DescriptionResponseBody(final DeviceInformationDIB deviceInformation,
                                    final SupportedServiceFamiliesDIB supportedServiceFamilies,
                                    final @Nullable IPConfigDIB ipConfig,
                                    final @Nullable IPCurrentConfigDIB ipCurrentConfig,
                                    final @Nullable KnxAddressesDIB knxAddresses,
                                    final @Nullable ManufacturerDataDIB manufacturerData) {
        Preconditions.checkNonNull(deviceInformation, "DIB about Device Information is required.");
        Preconditions.checkNonNull(supportedServiceFamilies, "DIB about Supported Service Families is required.");

        // mandatory
        this.deviceInformation = deviceInformation;
        this.supportedServiceFamilies = supportedServiceFamilies;

        // optional
        this.ipConfig = ipConfig;
        this.ipCurrentConfig = ipCurrentConfig;
        this.knxAddresses = knxAddresses;
        this.manufacturerData = manufacturerData;
    }

    /**
     * Builds a new {@link DescriptionResponseBody} instance
     *
     * @param bytes complete byte array for {@link DescriptionResponseBody}
     * @return a new immutable {@link DescriptionResponseBody}
     */
    public static DescriptionResponseBody of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length >= STRUCTURE_MIN_LENGTH && bytes.length <= STRUCTURE_MAX_LENGTH,
                "Incompatible structure length. Expected [{}..{}] but was: {}", STRUCTURE_MIN_LENGTH, STRUCTURE_MAX_LENGTH, bytes.length);
        Preconditions.checkArgument(bytes[1] == 0x01,
                "Incompatible structure. No Device Information DIB.");
        Preconditions.checkArgument(bytes[DeviceInformationDIB.STRUCTURE_LENGTH + 1] == 0x02,
                "Incompatible structure. No Supported Service Families DIB.");
        return new DescriptionResponseBody(bytes);
    }

    /**
     * Creates a new {@link DescriptionResponseBody} instance
     *
     * @param deviceInformation        description information about device hardware; may not be null
     * @param supportedServiceFamilies description information about supported service families; may not be null
     * @return a new immutable {@link DescriptionResponseBody}
     */
    public static DescriptionResponseBody of(final DeviceInformationDIB deviceInformation,
                                             final SupportedServiceFamiliesDIB supportedServiceFamilies) {
        return of(deviceInformation, supportedServiceFamilies, null, null, null, null);
    }

    /**
     * Creates a new {@link DescriptionResponseBody} instance
     *
     * @param deviceInformation        description information about device hardware; may not be null
     * @param supportedServiceFamilies description information about supported service families; may not be null
     * @param ipConfig                 description information about IP config
     * @param ipCurrentConfig          description about current IP config
     * @param knxAddresses             description information about KNX addresses
     * @param manufacturerData         description information about manufacturer
     * @return a new immutable {@link DescriptionResponseBody}
     */
    public static DescriptionResponseBody of(final DeviceInformationDIB deviceInformation,
                                             final SupportedServiceFamiliesDIB supportedServiceFamilies,
                                             final @Nullable IPConfigDIB ipConfig,
                                             final @Nullable IPCurrentConfigDIB ipCurrentConfig,
                                             final @Nullable KnxAddressesDIB knxAddresses,
                                             final @Nullable ManufacturerDataDIB manufacturerData) {
        return new DescriptionResponseBody(deviceInformation, supportedServiceFamilies, ipConfig, ipCurrentConfig, knxAddresses, manufacturerData);
    }

    /**
     * Returns the index of {@link DescriptionType} DIB we are looking in {@code rawData} array.
     *
     * @param descriptionType type of description we are looking for
     * @param bytes           byte array to be scanned
     * @return positive number if found, otherwise {@code -1}.
     */
    private static int indexOfDIB(final DescriptionType descriptionType, final byte[] bytes) {
        var index = -1;
        for (var i = 0; i < bytes.length; ) {
            final var dibLength = Byte.toUnsignedInt(bytes[i]);
            final var dibCode = Byte.toUnsignedInt(bytes[i + 1]);
            // we are interested in 2nd byte of DIB only
            if (descriptionType.getCode() == dibCode) {
                // found it!
                index = i;
                break;
            } else {
                // not found - try with next one
                if (dibLength == 0) {
                    throw new KnxException(String.format("Bad bytes provided. This would result into an endless loop: %s", ByteFormatter.formatHexAsString(bytes)));
                }
                i += dibLength;
            }
        }

        log.debug("Position of DIB '{}': {}", descriptionType.getFriendlyName(), index);
        return index;
    }

    /**
     * Returns the given {@link DescriptionType} DIB if found
     *
     * @param bytes           byte array to be parsed
     * @param descriptionType type of description we are looking for
     * @return an instance of DIB if found, otherwise {@code null}
     */
    @Nullable
    private static <T> T getDIB(final byte[] bytes, final DescriptionType descriptionType, Function<byte[], T> caller) {
        final var index = indexOfDIB(descriptionType, bytes);

        if (index < 0) {
            // not found
            if (log.isDebugEnabled()) {
                log.debug("DIB '{}' not found in: {}", descriptionType.name(), ByteFormatter.formatHexAsString(bytes));
            }
            return null;
        } else {
            // found
            final var dibLength = Byte.toUnsignedInt(bytes[index]);
            final var dibArray = new byte[dibLength];
            System.arraycopy(bytes, index, dibArray, 0, dibLength);

            final var dibObject = caller.apply(dibArray);
            if (log.isDebugEnabled()) {
                log.debug("DIB '{}' found: {}", descriptionType.name(), dibObject);
            }
            return dibObject;
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DESCRIPTION_RESPONSE;
    }

    public DeviceInformationDIB getDeviceInformation() {
        return deviceInformation;
    }

    public SupportedServiceFamiliesDIB getSupportedDeviceFamilies() {
        return supportedServiceFamilies;
    }

    @Nullable
    public IPConfigDIB getIPConfig() {
        return ipConfig;
    }

    @Nullable
    public IPCurrentConfigDIB getIPCurrentConfig() {
        return ipCurrentConfig;
    }

    @Nullable
    public KnxAddressesDIB getKnxAddresses() {
        return knxAddresses;
    }

    @Nullable
    public ManufacturerDataDIB getManufacturerData() {
        return manufacturerData;
    }

    @Override
    public byte[] toByteArray() {
        final var deviceInformationAsBytes = deviceInformation.toByteArray();
        final var deviceFamiliesAsBytes = supportedServiceFamilies.toByteArray();

        final var totalLength = deviceInformationAsBytes.length + deviceFamiliesAsBytes.length;

        // create bytes
        final var bytes = new byte[totalLength];
        var pos = 0;
        System.arraycopy(deviceInformationAsBytes, 0, bytes, pos, deviceInformationAsBytes.length);
        pos += deviceInformationAsBytes.length;
        System.arraycopy(deviceFamiliesAsBytes, 0, bytes, pos, deviceFamiliesAsBytes.length);

        return bytes;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("deviceInformation", deviceInformation)
                .add("supportedServiceFamilies", supportedServiceFamilies)
                .add("ipConfig", ipConfig)
                .add("ipCurrentConfig", ipCurrentConfig)
                .add("knxAddresses", knxAddresses)
                .add("manufacturerData", manufacturerData)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof DescriptionResponseBody) {
            final var other = (DescriptionResponseBody) obj;
            return Objects.equals(this.deviceInformation, other.deviceInformation)
                    && Objects.equals(this.supportedServiceFamilies, other.supportedServiceFamilies)
                    && Objects.equals(this.ipConfig, other.ipConfig)
                    && Objects.equals(this.ipCurrentConfig, other.ipCurrentConfig)
                    && Objects.equals(this.knxAddresses, other.knxAddresses)
                    && Objects.equals(this.manufacturerData, other.manufacturerData);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                deviceInformation,
                supportedServiceFamilies,
                ipConfig,
                ipCurrentConfig,
                knxAddresses,
                manufacturerData
        );
    }
}
