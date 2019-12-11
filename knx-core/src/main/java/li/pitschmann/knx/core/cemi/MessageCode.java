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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.KnxByteEnum;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * {@link MessageCode} covers the different message type for the communication mode. Each cEMI message starts with the
 * message code octet.
 *
 * @author PITSCHR
 */
public enum MessageCode implements KnxByteEnum {
    /**
     * To be used by software when transit a frame to KNX Net/IP device.
     * Important: The status change is only a request and not an effective change!
     * <p>
     * Example: Software requests to 'light on' to KNX Net/IP device and the KNX Net/IP device
     * acknowledges the requests and forwards to a KNX device (e.g. a KNX push-button).
     *
     * <pre>
     * Client --> | KNX Net/IP |      KNX device   (TUNNELING_REQ to KNX Net/IP)
     * Client <-- | KNX Net/IP |      KNX device   (TUNNELING_ACK from KNX Net/IP)
     * </pre>
     */
    L_DATA_REQ(0x11, "L-Data.req (Request)"),
    /**
     * To be used when confirm a frame from KNX Net/IP device. Usually happens after {@link #L_DATA_REQ}.
     * <p>
     * Example:<br>
     * 1+2) Client wants to request (write or read) on a Group Address. The frame will be accepted by the
     * KNX Net/IP device and sends an acknowledge frame to client. This doesn't mean that the change is effective!<br>
     * 3+4) The KNX Net/IP device will forward clients request to the KNX bus which is (probably) accepted by the
     * KNX device. The KNX device sends an acknowledge frame to the KNX Net/IP device.<br>
     * 5+6) After apply the KNX device may (depending on flag setting) send a frame about the new status to the
     * KNX Net/IP device. This will be acknowledged by KNX Net/IP device.<br>
     * 7+8) The KNX Net/IP device will forward the frame to client. And client will send acknowledge frame and
     * the status pool of client knows that the change has been applied!<br>
     *
     * <pre>
     * Client --> | KNX Net/IP |     KNX device  (TUNNELING_REQ to KNX Net/IP)
     * Client <-- | KNX Net/IP |     KNX device  (TUNNELING_ACK from KNX Net/IP)
     * Client     | KNX Net/IP | --> KNX device  (TUNNELING_REQ forwarded to KNX device)
     * Client     | KNX Net/IP | <-- KNX device  (TUNNELING_ACK from device to KNX Net/IP)
     * Client     | KNX Net/IP | <-- KNX device  (TUNNELING_CON to KNX Net/IP)
     * Client     | KNX Net/IP | --> KNX device  (TUNNELING_ACK from KNX Net/IP to KNX device)
     * Client <-- | KNX Net/IP |     KNX device	 (TUNNELING_CON forwarded to client)
     * Client --> | KNX Net/IP |     KNX device	 (TUNNELING_ACK to KNX Net/IP)
     * </pre>
     */
    L_DATA_CON(0x2E, "L-Data.con (Confirmation)"),
    /**
     * To be used when receive a frame from a remote user.
     * <p>
     * Example:<br>
     * 1+2) A status has been changed KNX device (the initiator may be another device inside the KNX bus).
     * The frame will be sent to KNX Net/IP device and will be acknowledged.<br>
     * 3+4) The frame will be forwarded by KNX Net/IP device to client about the new status. Finally, the client
     * acknowledges the received frame.<br>
     * <p>
     * The behavior is very similar to the {@link #L_DATA_CON}. This message code can be used to distinguish if the
     * change was requested by client or if the request happened outside of the application.
     *
     * <pre>
     * Client     | KNX Net/IP | <-- KNX device  (TUNNELING_IND from device to KNX Net/IP)
     * Client     | KNX Net/IP | --> KNX device  (TUNNELING_ACK to KNX device)
     * Client <-- | KNX Net/IP |     KNX device	 (TUNNELING_IND forwarded to client)
     * Client --> | KNX Net/IP |     KNX device	 (TUNNELING_ACK to KNX Net/IP)
     * </pre>
     */
    L_DATA_IND(0x29, "L-Data.ind (Indication)");

    private final int code;
    private final String friendlyName;

    MessageCode(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link MessageCode} for the given {@code code}
     * <p/>
     * In case if the message code is unknown or unsupported message, the received message should be simply be ignored
     * and no confirmation message should be sent.
     *
     * @param code
     * @return existing {@link MessageCode}, or {@link KnxEnumNotFoundException} if no {@link MessageCode}
     * for given {@code code} exists
     */
    public static MessageCode valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(MessageCode.class, code));
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("name", this.name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:off
    }
}
