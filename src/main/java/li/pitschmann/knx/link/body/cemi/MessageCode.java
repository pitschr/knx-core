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

package li.pitschmann.knx.link.body.cemi;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.KnxByteEnum;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.utils.ByteFormatter;

import java.util.Arrays;

/**
 * {@link MessageCode} covers the different message type for the communication mode. Each cEMI message starts with the
 * message code octet.
 *
 * @author PITSCHR
 */
public enum MessageCode implements KnxByteEnum {
    /**
     * To be used by software when transit a frame to KNX Net/IP router. Important: The status change is only a request and not
     * an effective change!
     * <p>
     * Example: Software requests to 'light on' to KNX Net/IP router and the KNX Net/IP router acknowledges the requests and forwards
     * to KNX device.
     *
     * <pre>
     * You --> | Router |      KNX device   (TUNNELLING_REQ to router)
     * You <-- | Router |      KNX device   (TUNNELLING_ACK from router)
     * </pre>
     */
    L_DATA_REQ(0x11, "L-Data.req (Request)"),
    /**
     * To be used when confirm a frame from KNX Net/IP router. Usually happens after {@link #L_DATA_REQ}.
     * <p>
     * Example: After change on KNX device the KNX Net/IP router notifies the software about it's status. Finally, the software
     * sends an acknowledge message back to KNX Net/IP router. The change is taken as effective!
     *
     * <pre>
     * You --> | Router |     KNX device     (TUNNELLING_REQ to router)
     * You <-- | Router |     KNX device     (TUNNELLING_ACK from router)
     * You     | Router | --> KNX device     (TUNNELLING_REQ forwarded to device)
     * You     | Router | <-- KNX device     (TUNNELLING_ACK from device to router)
     * You     | Router | <-- KNX device     (TUNNELLING_CON to router)
     * You     | Router | --> KNX device     (TUNNELLING_ACK from router to device)
     * You <-- | Router |     KNX device	 (TUNNELLING_CON forwarded to you)
     * You --> | Router |     KNX device	 (TUNNELLING_ACK to router)
     * </pre>
     */
    L_DATA_CON(0x2E, "L-Data.con (Confirmation)"),
    /**
     * To be used when receive a frame from a remote user
     * <p>
     * Example: A KNX device wants to inform about the light status (e.g. when changed or periodically, if
     * parameterized). KNX device sends the status to KNX Net/IP router and KNX Net/IP router forwards it to the software. Finally,
     * the software sends an acknowledgement message back to KNX Net/IP router.
     * <p>
     * The behavior is very similar to the {@link #L_DATA_CON}. This message code can be used to distinguish if the
     * change was requested by software or if the request happened outside of the software.
     *
     * <pre>
     * You     | Router | <-- KNX device     (TUNNELLING_IND from device to router)
     * You     | Router | --> KNX device     (TUNNELLING_ACK to device)
     * You <-- | Router |     KNX device	 (TUNNELLING_IND forwarded to you)
     * You --> | Router |     KNX device	 (TUNNELLING_ACK to router)
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
     * {@inheritDoc}
     * <p>
     * In case if the message code is unknown or unsupported message, the received message should be simply be ignored
     * and no confirmation message should be sent.
     *
     * @param code
     * @return {@link MessageCode}, or {@link KnxEnumNotFoundException} in case the {@code} is unknown or unsupported
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
        return MoreObjects.toStringHelper(this)
                .add("name", this.name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:off
    }
}
