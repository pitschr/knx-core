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

package li.pitschmann.test;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.BodyFactory;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.TunnellingAckBody;
import li.pitschmann.knx.link.body.TunnellingRequestBody;
import li.pitschmann.utils.Bytes;

public final class KnxBody {
    public static final String DESCRIPTION_REQUEST = "06100203000e0801000000000000";
    public static final String DESCRIPTION_RESPONSE = "06100204004636010200100000000083497f01ece000170ccc1be08008da4d4454204b4e5820495020526f75746572000000000000000000000000000a020201030104010501";
    public static final String CONNECT_REQUEST = "06100205001a0801c0a80118e54e0801c0a80118e54f04040200";
    public static final String CONNECT_RESPONSE = "06100206001407000801c0a801100e570404fff2";
    public static final String CONNECTION_STATE_REQUEST = "06100207001007000801c0a80118e54e";
    public static final String CONNECTION_STATE_RESPONSE = "0610020800080700";
    public static final String DISCONNECT_REQUEST = "06100209001007000801c0a80118e54e";
    public static final String DISCONNECT_RESPONSE = "0610020a00080700";
    public static final String TUNNELLING_REQUEST = "06100420001704071b002900bce010c84c0f0300800c23";
    public static final String TUNNELLING_ACK = "06100421000a04071b00";
    public static final String TUNNELLING_REQUEST_2 = "06100420001704070b002900bce010aa4c090300800c82";
    public static final String TUNNELLING_ACK_2 = "06100421000a04070b00";
    public static final DescriptionRequestBody DESCRIPTION_REQUEST_BODY = toBody(DESCRIPTION_REQUEST);
    public static final DescriptionResponseBody DESCRIPTION_RESPONSE_BODY = toBody(DESCRIPTION_RESPONSE);
    public static final ConnectRequestBody CONNECT_REQUEST_BODY = toBody(CONNECT_REQUEST);
    public static final ConnectResponseBody CONNECT_RESPONSE_BODY = toBody(CONNECT_RESPONSE);
    public static final ConnectionStateRequestBody CONNECTION_STATE_REQUEST_BODY = toBody(CONNECTION_STATE_REQUEST);
    public static final ConnectionStateResponseBody CONNECTION_STATE_RESPONSE_BODY = toBody(CONNECTION_STATE_RESPONSE);
    public static final DisconnectRequestBody DISCONNECT_REQUEST_BODY = toBody(DISCONNECT_REQUEST);
    public static final DisconnectResponseBody DISCONNECT_RESPONSE_BODY = toBody(DISCONNECT_RESPONSE);
    public static final TunnellingRequestBody TUNNELLING_REQUEST_BODY = toBody(TUNNELLING_REQUEST);
    public static final TunnellingAckBody TUNNELLING_ACK_BODY = toBody(TUNNELLING_ACK);
    public static final TunnellingRequestBody TUNNELLING_REQUEST_BODY_2 = toBody(TUNNELLING_REQUEST_2);
    public static final TunnellingAckBody TUNNELLING_ACK_BODY_2 = toBody(TUNNELLING_ACK_2);

    private KnxBody() {
        throw new AssertionError("Do not touch me!");
    }

    private static <T extends Body> T toBody(final String hexString) {
        return BodyFactory.valueOf(Bytes.toByteArray(hexString));
    }

    public static final class Sequences {
        public static final String MINIMAL_DISCONNECT_BY_CLIENT =
                // On first request send DescriptionResponseBody
                KnxBody.DESCRIPTION_RESPONSE + "," +
                        // wait for next packet (will be: ConnectRequestBody)
                        "WAIT=NEXT," +
                        // send ConnectResponseBody
                        KnxBody.CONNECT_RESPONSE + "," +
                        // wait for next packet (will be: ConnectionStateRequestBody)
                        "WAIT=NEXT," +
                        // ConnectionStateResponseBody
                        KnxBody.CONNECTION_STATE_RESPONSE + "," +
                        // wait for packet with type 'DisconnectRequestBody'
                        "WAIT=DISCONNECT_REQUEST," +
                        // send DisconnectResponseBody
                        KnxBody.DISCONNECT_RESPONSE;
        public static final String MINIMAL_DISCONNECT_BY_REMOTE =
                // On first request send DescriptionResponseBody
                KnxBody.DESCRIPTION_RESPONSE + "," +
                        // wait for next packet (will be: ConnectRequestBody)
                        "WAIT=NEXT," +
                        // send ConnectResponseBody
                        KnxBody.CONNECT_RESPONSE + "," +
                        // wait for next packet (will be: ConnectionStateRequestBody)
                        "WAIT=NEXT," +
                        // ConnectionStateResponseBody
                        KnxBody.CONNECTION_STATE_RESPONSE + "," +
                        // send DisconnectRequestBody
                        KnxBody.DISCONNECT_REQUEST + "," +
                        // Wait for last response from client and quit mock server gracefully
                        "WAIT=NEXT";
    }

    public static final class Failures {
        public static final String DESCRIPTION_RESPONSE_INVALID_SERVICE_TYPE = "061002FF002036010200100000000083497f01ece000170ccc1be08008da4d44";
        public static final String DESCRIPTION_RESPONSE_NO_SUPPORTED_DEVICE_DIB = "06100204002036010200100000000083497f01ece000170ccc1be08008da4d44";
        public static final String DESCRIPTION_RESPONSE_BAD_DATA = "06100204004634010200100000000083497f01ece000170ccc1be08008da4d4454204b4e5820495020526f7574657200000000000000000000000000";
        public static final String DESCRIPTION_RESPONSE_WITHOUT_TUNNELLING = "06100204004436010200100000000083497f01ece000170ccc1be08008da4d4454204b4e5820495020526f75746572000000000000000000000000000802020103010501";

        public static final String CONNECT_RESPONSE_NO_MORE_CONNECTIONS = "06100206001407240801c0a801100e570404fff2";
        public static final String CONNECT_RESPONSE_BAD_DATA = "0610020600140000000100000000000004000000";

        public static final String TUNNELLING_REQUEST_WRONG_CHANNEL_ID = "061004200015041100001100bce000000a96010081";
    }
}
