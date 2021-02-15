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

import li.pitschmann.knx.core.address.AddressType;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.cemi.APCI;
import li.pitschmann.knx.core.cemi.AdditionalInfo;
import li.pitschmann.knx.core.cemi.BroadcastType;
import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.cemi.ControlByte1;
import li.pitschmann.knx.core.cemi.ControlByte2;
import li.pitschmann.knx.core.cemi.MessageCode;
import li.pitschmann.knx.core.cemi.Priority;
import li.pitschmann.knx.core.cemi.TPCI;
import li.pitschmann.knx.core.datapoint.DPT7;
import li.pitschmann.knx.core.header.ServiceType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link TunnelingRequestBody}
 *
 * @author PITSCHR
 */
public class TunnelingRequestBodyTest {
    /**
     * <pre>
     * 	KNX/IP
     * 	    Header
     * 	        Header Length: 0x06
     * 	        Protocol Version: 0x10
     * 	        Service Type Identifier: TUNNELING_REQUEST (0x0420)
     * 	        Total Length: 23 octets
     * 	    Body
     * 	        Structure Length: 4 octets (0x04)
     * 	        Communication Channel ID: 17 (0x11)
     * 	        Sequence Counter: 92 (0x5C)
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
     * 	            Source Address 1.0.160 (0x10 A0)
     * 	            Destination Address 9/4/7 or 9/1031 (0x4C 07)
     * 		        NPDU length: 3 octets (0x03)
     * 		        00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * 		        .... ..00  10.. .... = APCI: A_GroupValue_Write (0x0002)
     * 		        data: 0c3f
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(int, int, CEMI)")
    public void validCases() {
        // create by bytes
        final var bodyByBytes = TunnelingRequestBody.of(new byte[]{
                0x04,               // Structure Length
                0x11,               // Communication Channel ID
                0x5C,               // Sequence Counter
                0x00,               // (reserved)
                // CEMI -- Start --
                0x29,               // Message Code
                0x00,               // Additional Information Length
                (byte) 0xBC,        // Control Field 1
                (byte) 0xE0,        // Control Field 2
                0x10, (byte) 0xA0,  // Source Address
                0x4C, 0x07,         // Destination Address
                0x03,               // NPDU length
                0x00, (byte) 0x80,  // TPCI + APCI type
                0x0C, 0x3F          // APCI data
                // CEMI --  End  --
        });

        // create
        final var channelId = 17;
        final var sequence = 92;

        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.GROUP, 6, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 160);
        final var destinationAddress = GroupAddress.of(9, 4, 7);
        final var dptValue = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT.of(new byte[]{0x0c, 0x3f});
        final var cemi = CEMI.of(MessageCode.L_DATA_IND, AdditionalInfo.empty(), controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, dptValue);

        // create
        final var body = TunnelingRequestBody.of(channelId, sequence, cemi);
        assertThat(body.getServiceType()).isSameAs(ServiceType.TUNNELING_REQUEST);
        assertThat(body.getLength()).isEqualTo(4);
        assertThat(body.getChannelId()).isEqualTo(channelId);
        assertThat(body.getSequence()).isEqualTo(sequence);
        assertThat(body.getCEMI()).isSameAs(cemi);

        // compare byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("TunnelingRequestBody{length=4, channelId=17, sequence=92, cemi=%s}", cemi)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        // invalid cases
        assertThatThrownBy(() -> TunnelingRequestBody.of(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> TunnelingRequestBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [15..255] but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(int, int, CEMI)")
    void invalidCases_ofObjects() {
        // null
        assertThatThrownBy(() -> TunnelingRequestBody.of(0, 0, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CEMI is required.");

        // invalid range
        assertThatThrownBy(() -> TunnelingRequestBody.of(-1, 0, mock(CEMI.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: -1");
        assertThatThrownBy(() -> TunnelingRequestBody.of(0xFF + 1, 0, mock(CEMI.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible channel id. Expected [0..255] but was: 256");

        assertThatThrownBy(() -> TunnelingRequestBody.of(0, -1, mock(CEMI.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible sequence. Expected [0..255] but was: -1");
        assertThatThrownBy(() -> TunnelingRequestBody.of(0, 0xFF + 1, mock(CEMI.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible sequence. Expected [0..255] but was: 256");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(TunnelingRequestBody.class).verify();
    }

}
