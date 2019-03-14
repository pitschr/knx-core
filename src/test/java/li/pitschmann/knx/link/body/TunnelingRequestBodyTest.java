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

import li.pitschmann.knx.link.body.address.AddressType;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.body.cemi.AdditionalInfo;
import li.pitschmann.knx.link.body.cemi.BroadcastType;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.body.cemi.ControlByte1;
import li.pitschmann.knx.link.body.cemi.ControlByte2;
import li.pitschmann.knx.link.body.cemi.MessageCode;
import li.pitschmann.knx.link.body.cemi.Priority;
import li.pitschmann.knx.link.body.cemi.TPCI;
import li.pitschmann.knx.link.datapoint.DPT7;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.utils.Bytes;
import li.pitschmann.utils.Bytes.FillDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link TunnelingRequestBody}
 *
 * @author PITSCHR
 */
public class TunnelingRequestBodyTest {
    // prepare
    private int channelId;
    private int sequence;
    private CEMI cemi;

    @BeforeEach
    public void before() {
        final var controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.create(AddressType.GROUP, 6, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 160);
        final var destinationAddress = GroupAddress.of(9, 4, 7);

        // ACPI data: new byte[]{0x0c, 0x3f}
        final var dptValue = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT.toValue(3135);

        this.channelId = 17;
        this.sequence = 92;
        this.cemi = CEMI.create(MessageCode.L_DATA_IND, AdditionalInfo.empty(), controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, dptValue);
    }

    /**
     * Tests the {@link TunnelingRequestBody#create(int, int, CEMI)} and
     * {@link TunnelingRequestBody#valueOf(byte[])} methods.
     *
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: TUNNELING_REQUEST (0x0420)
     * 	        Total Length: 23 octets
     * 	    Body
     * 	        Structure Length: 4 octets
     * 	        Communication Channel ID: 17
     * 	        Sequence Counter: 92
     * 	        reserved: 00
     * 	        cEMI
     * 	            messagecode: L_Data.ind (0x29)
     * 	            add information length: 0 octets
     * 	            Controlfield 1: 0xbc
     * 	                1... .... = Frametype: 1
     * 	                ..1. .... = Repeat: 1
     * 	                ...1 .... = System-Broadcast: 1
     * 	                .... 11.. = Priority: 0x3
     * 	                .... ..0. = Acknowledge-Request: 0
     * 	                .... ...0 = Confirm-Flag: 0
     * 	            Controlfield 2: 0xe0
     * 	                1... .... = Destination address type: 1
     * 	                .110 .... = Hop count: 6
     * 	                .... 0000 = Extended Frame Format: 0x0
     * 	            Source Address 1.0.160
     * 	            Destination Address 9/4/7 or 9/1031
     * 		        NPDU length: 3 octets
     * 		        00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * 		        .... ..00  10.. .... = APCI: A_GroupValue_Write (0x0002)
     * 		        data: 0c3f
     * </pre>
     */
    @Test
    public void validCases() {
        // create
        final var body = TunnelingRequestBody.create(this.channelId, this.sequence, this.cemi);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.TUNNELING_REQUEST);
        assertThat(body.getLength()).isEqualTo(4);
        assertThat(body.getChannelId()).isEqualTo(this.channelId);
        assertThat(body.getSequence()).isEqualTo(this.sequence);
        assertThat(body.getCEMI().getRawData()).containsExactly(this.cemi.getRawData());

        // compare raw data of with valueOf(byte[])
        final var bodyByBytes = TunnelingRequestBody.valueOf(new byte[]{0x04, 0x11, 0x5c, 0x00, 0x29, 0x00, (byte) 0xbc,
                (byte) 0xe0, 0x10, (byte) 0xa0, 0x4c, 0x07, 0x03, 0x00, (byte) 0x80, 0x0c, 0x3f});
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format(
                "TunnelingRequestBody{length=4 (0x04), channelId=17 (0x11), sequence=92 (0x5C), cemi=%s, rawData=0x04 11 5C 00 29 00 BC E0 10 A0 4C 07 03 00 80 0C 3F}",
                this.cemi.toString(false)));
    }

    /**
     * Tests {@link TunnelingRequestBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> TunnelingRequestBody.create(this.channelId, this.sequence, null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("cemi");

        // invalid size
        assertThatThrownBy(() -> TunnelingRequestBody.create(this.channelId, -1, this.cemi)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("sequence");
        assertThatThrownBy(() -> TunnelingRequestBody.create(this.channelId, 0xFF + 1, this.cemi)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("sequence");
        assertThatThrownBy(() -> TunnelingRequestBody.create(-1, this.sequence, this.cemi)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");
        assertThatThrownBy(() -> TunnelingRequestBody.create(0xFF + 1, this.sequence, this.cemi)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("channelId");

        // invalid raw data length
        assertThatThrownBy(() -> TunnelingRequestBody.valueOf(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("rawData");
        assertThatThrownBy(() -> TunnelingRequestBody.valueOf(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> TunnelingRequestBody.valueOf(new byte[0xFF + 1])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> TunnelingRequestBody.valueOf(Bytes.fillByteArray(new byte[20], new byte[]{0x05}, FillDirection.LEFT_TO_RIGHT)))
                .isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("rawData[0]");
    }
}
