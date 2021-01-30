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

package li.pitschmann.knx.core.header;

import li.pitschmann.knx.core.KnxBytesEnum;
import li.pitschmann.knx.core.dib.ServiceTypeFamily;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxServiceTypeHasNoResponseIdentifier;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * Service Type Identifier used in KNX/IP headers to identify
 * the message frames.
 * <p>
 * The KNXnet/IP service type identifier defines the kind of
 * action to be performed and the type of the data payload
 * contained in the KNXnet/IP body if applicable. The high octet
 * of the KNXnet/IP service type identifier denotes the
 * service type family and the low octet the actual service type
 * in that family. For a detailed description of the services,
 * see below.
 *
 * <table>
 * <tr>
 * <td><strong>KNX/IP Core:</strong></td>
 * <td>0x0200 ... 0x020F</td>
 * </tr>
 * <tr>
 * <td><strong>KNX device Management:</strong></td>
 * <td>0x0310 ... 0x031F</td>
 * </tr>
 * <tr>
 * <td><strong>KNX/IP Tunneling:</strong></td>
 * <td>0x0420 ... 0x042F</td>
 * </tr>
 * <tr>
 * <td><strong>KNX/IP Routing:</strong></td>
 * <td>0x0530 ... 0x053F</td>
 * </tr>
 * <tr>
 * <td><strong>KNX/IP Remote Logging:</strong></td>
 * <td>0x0600 ... 0x06FF</td>
 * </tr>
 * <tr>
 * <td><strong>KNX/IP Remote Configuration and Diagnosis:</strong></td>
 * <td>0x0740 ... 0x07FF</td>
 * </tr>
 * <tr>
 * <td><strong>KNX/IP Object Server:</strong></td>
 * <td>0x0800 ... 0x08FF</td>
 * </tr>
 * </table>
 * See: KNX Specification, Core, 2.3.4 + 7.4
 *
 * @author PITSCHR
 */
public enum ServiceType implements KnxBytesEnum {
    // @formatter:off
    /**
     * Service type sent by KNX Net/IP device when responding to a {@link #SEARCH_REQUEST}.
     * <p>
     * Communication way: Server -> Client
     */
    SEARCH_RESPONSE(0x0202, "Search Response"),
    /**
     * Service type sent by KNX client to search available KNX Net/IP devices.
     * <p>
     * Communication way: Client -> Server
     */
    SEARCH_REQUEST(0x0201, "Search Request", SEARCH_RESPONSE),
    /**
     * Service type sent by KNX Net/IP device in response to a {@link #DESCRIPTION_REQUEST} to
     * provide information about the KNX Net/IP device implementation.
     * <p>
     * Communication way: Server -> Client
     */
    DESCRIPTION_RESPONSE(0x0204, "Description Response"),
    /**
     * Service type sent by KNX client to a KNX Net/IP device to retrieve information
     * about capabilities and supported services.
     * <p>
     * Communication way: Client -> Server
     */
    DESCRIPTION_REQUEST(0x0203, "Description Request", DESCRIPTION_RESPONSE),
    /**
     * Service type sent by KNX Net/IP device in response to a {@link #CONNECT_REQUEST} frame.
     * <p>
     * Communication way: Server -> Client
     */
    CONNECT_RESPONSE(0x0206, "Connect Response"),
    /**
     * Service type sent by KNX client to establish a communication channel with a
     * KNX Net/IP device.
     * <p>
     * Communication way: Client -> Server
     */
    CONNECT_REQUEST(0x0205, "Connect Request", CONNECT_RESPONSE),
    /**
     * Service type sent by KNX Net/IP device when receiving a {@link #CONNECTION_STATE_REQUEST}
     * for an established connection.
     * <p>
     * Communication way: Server -> Client
     */
    CONNECTION_STATE_RESPONSE(0x0208, "Connection State Response"),
    /**
     * Service type sent by KNX client requesting the connection state of an established
     * connection with KNX Net/IP device.
     * <p>
     * Communication way: Client -> Server
     */
    CONNECTION_STATE_REQUEST(0x0207, "Connection State Request", CONNECTION_STATE_RESPONSE),
    /**
     * Service type sent by KNX device, typically the KNX Net/IP device, in response to a
     * {@link #DISCONNECT_REQUEST}.
     * <p>
     * Communication way: Client -> Server
     */
    DISCONNECT_RESPONSE(0x020A, "Disconnect Response"),
    /**
     * Service type sent by KNX device, typically the KNX client, to terminate an
     * established connection.
     * <p>
     * Communication ways: Client -> Server ; Server -> Client
     */
    DISCONNECT_REQUEST(0x0209, "Disconnect Request", DISCONNECT_RESPONSE),
    /**
     * Service type sent by KNX device to confirm the reception of the {@link #DEVICE_CONFIGURATION_REQUEST}.
     * <p>
     * Communication ways: Client -> Server ; Server -> Client
     */
    DEVICE_CONFIGURATION_ACK(0x0311, "Device Configuration Acknowledge"),
    /**
     * Reads / Writes KNX device configuration data (Interface Object Properties)
     */
    DEVICE_CONFIGURATION_REQUEST(0x0310, "Device Configuration Request", DEVICE_CONFIGURATION_ACK),
    /**
     * Service type sent by KNX device to cofirm the reception of the  {@link #TUNNELING_REQUEST}.
     */
    TUNNELING_ACK(0x0421, "Tunneling Acknowledgement"),
    /**
     * Used for sending and receiving single KNX telegrams between KNX client and server.
     */
    TUNNELING_REQUEST(0x0420, "Tunneling Request", TUNNELING_ACK),
    /**
     * Used for sending KNX telegrams over IP networks. This service is unconfirmed.
     */
    ROUTING_INDICATION(0x0530, "Routing indication"),
    /**
     * Used for indication of lost KNX/IP routing messages. This service is unconfirmed.
     */
    ROUTING_LOST_MESSAGE(0x0531, "Routing lost message"),
    /**
     * Used for busy signal if the KNX device exceeds the number of datagram that can
     * be processed. This service is unconfirmed
     */
    ROUTING_BUSY(0x0532, "Routing busy message")
    // @formatter:on
    ;

    private final int code;
    private final String friendlyName;
    private final ServiceTypeFamily family;
    private final ServiceType responseIdentifier;

    /**
     * Constructor for Response/Acknowledge Service Type identifiers
     */
    ServiceType(final int code, final String friendlyName) {
        this(code, friendlyName, null);
    }

    /**
     * Constructor for Request Service Type Identifiers
     */
    ServiceType(final int code, final String friendlyName, final ServiceType responseIdentifier) {
        this.code = code;
        this.friendlyName = friendlyName;
        // the service type family is the high octet of the service type ID
        this.family = ServiceTypeFamily.valueOf(Byte.toUnsignedInt((byte) (code >>> 8)));
        this.responseIdentifier = responseIdentifier;
    }

    /**
     * Returns the {@link ServiceType} for given {@code code}
     *
     * @param code value to find the associated {@link ServiceType}
     * @return instance of {@link ServiceType} otherwise {@link KnxEnumNotFoundException}
     * if given {@code code} is not known
     */
    public static ServiceType valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(ServiceType.class, code));
    }

    @Override
    public int getCode() {
        return code;
    }

    /**
     * Returns the code of {@link ServiceType} in two bytes.
     *
     * @return two bytes
     */
    @Override
    public byte[] getCodeAsBytes() {
        return new byte[]{(byte) (code >>> 8), (byte) code};
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    public ServiceTypeFamily getFamily() {
        return family;
    }

    /**
     * Returns if the current service type is classified as response {@link ServiceType}.
     *
     * @return {@code true} if current service type is response/acknowledge, otherwise {@code false}
     */
    public boolean hasResponseIdentifier() {
        return responseIdentifier != null;
    }

    /**
     * Returns the response {@link ServiceType} for current request {@link ServiceType}.
     *
     * @return the response {@link ServiceType}, else {@link KnxServiceTypeHasNoResponseIdentifier}
     * @throws KnxServiceTypeHasNoResponseIdentifier when no response identifier is defined.
     */
    public ServiceType getResponseIdentifier() {
        if (!hasResponseIdentifier()) {
            throw new KnxServiceTypeHasNoResponseIdentifier(this);
        }
        return responseIdentifier;
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("name", name())
                .add("friendlyName", friendlyName)
                .add("code", code)
                .add("family", family.name())
                .add("responseIdentifier", responseIdentifier == null ? "" : responseIdentifier.name())
                .toString();
        // @formatter:on
    }
}
