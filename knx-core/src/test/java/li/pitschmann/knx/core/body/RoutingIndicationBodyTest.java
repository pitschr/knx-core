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
import li.pitschmann.knx.core.datapoint.DPT8;
import li.pitschmann.knx.core.header.ServiceType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link RoutingIndicationBody}
 *
 * @author PITSCHR
 */
class RoutingIndicationBodyTest {
    /**
     * <pre>
     *   KNX/IP Routing Indication
     *     KNX/IP Header: Routing Indication
     *         Header Length: 6 bytes
     *         Protocol Version: 1.0
     *         Service Identifier: Routing Indication (0x0530)
     *             Service Family: Routing (0x05)
     *             Service Type: Routing Indication (0x0530)
     *         Total Length: 19 bytes
     *   cEMI L_Data.ind, P=Low, H=5, Src=1.0.110, Dst=9/4/1, GroupValueWrite, $0C35
     *     Message Code: L_Data.ind (0x29)
     *     Additional Info (0 bytes)
     *         Additional Information Length: 0
     *     Ctrl1: Prio = Low
     *         1... .... = Frame Type: Standard (1)
     *         ..1. .... = Repeat On Error: No (1)
     *         ...1 .... = Broadcast Type: Domain (1)
     *         .... 11.. = Priority: Low (3)
     *         .... ..0. = Ack Wanted: No (0)
     *         .... ...0 = Confirmation Error: No (0)
     *     Ctrl2: Hops = 5
     *         1... .... = Address Type: Group (1)
     *         .101 .... = Hop Count: 5
     *         .... 0000 = Extended Frame Format: 0x0
     *     Source: 0x106e = 1.0.110
     *     Destination: 0x4c01 = 9/4/1
     *     Length: 3
     *     TPCI
     *         0... .... = Packet Type: Data (0)
     *         .0.. .... = Sequence Type: Unnumbered (0)
     *     APCI: GroupValueWrite
     *         .... ..00 10.. .... = Service: GroupValueWrite (0x2)
     *     Data: $ 0C 35
     * </pre>
     */
    @Test
    @DisplayName("Test valid cases using #of(byte[]) and #of(CEMI)")
    void validCases() {
        // create by bytes
        final var bodyByBytes = RoutingIndicationBody.of(new byte[]{
                0x29,          // Message Code
                0x00,          // Additional Info
                (byte) 0xBC,   // Control Byte 1
                (byte) 0xD0,   // Control Byte 2
                0x10, 0x6E,    // Source Address
                0x4C, 0x01,    // Destination Address
                0x03,          // NPDU Length
                0x00,          // TPCI + Packet Number
                (byte) 0x80,   // APCI
                0x0C, 0x35     // Data
        });

        // create
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.GROUP, 5, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 110);
        final var destinationAddress = GroupAddress.of(9, 4, 1);
        final var cemi = CEMI.of(
                MessageCode.L_DATA_IND,
                AdditionalInfo.empty(),
                controlByte1,
                controlByte2,
                sourceAddress,
                destinationAddress,
                TPCI.UNNUMBERED_PACKAGE,
                0,
                APCI.GROUP_VALUE_WRITE,
                DPT8.VALUE_2_OCTET_COUNT.of(3125));

        final var body = RoutingIndicationBody.of(cemi);
        assertThat(body.getServiceType()).isSameAs(ServiceType.ROUTING_INDICATION);
        assertThat(body.getCEMI()).isSameAs(cemi);

        // compare byte array of 'create' and 'create by bytes'
        assertThat(body.toByteArray()).containsExactly(bodyByBytes.toByteArray());

        // toString
        assertThat(body).hasToString(
                String.format("RoutingIndicationBody{cemi=%s}", cemi)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_ofBytes() {
        assertThatThrownBy(() -> RoutingIndicationBody.of((byte[])null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> RoutingIndicationBody.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [11..255] but was: 0");
    }

    @Test
    @DisplayName("Invalid cases for #of(CEMI)")
    void invalidCases_ofObjects() {
        assertThatThrownBy(() -> RoutingIndicationBody.of((CEMI) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CEMI is required.");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(RoutingIndicationBody.class).verify();
    }

}
