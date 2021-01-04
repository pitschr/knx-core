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
import li.pitschmann.knx.core.datapoint.DPT12;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.header.ServiceType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the {@link RoutingIndicationBody}
 *
 * @author PITSCHR
 */
public class RoutingIndicationBodyTest {
    /**
     * Tests the {@link RoutingIndicationBody#of(CEMI)} and
     * {@link RoutingIndicationBody#of(byte[])} methods.
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
    public void validCases() {
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.GROUP, 5, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 110);
        final var destinationAddress = GroupAddress.of(9, 4, 1);

        CEMI cemi = CEMI.of(
                MessageCode.L_DATA_IND,
                AdditionalInfo.empty(),
                controlByte1,
                controlByte2,
                sourceAddress,
                destinationAddress,
                TPCI.UNNUMBERED_PACKAGE,
                0,
                APCI.GROUP_VALUE_WRITE,
                DPT12.VALUE_4_OCTET_UNSIGNED_COUNT.of(722821273));

        // create
        final var body = RoutingIndicationBody.of(cemi);
        assertThat(body.getServiceType()).isEqualTo(ServiceType.ROUTING_INDICATION);
        assertThat(body.getCEMI().getRawData()).containsExactly(cemi.getRawData());

        // create by bytes
        final var bodyByBytes = RoutingIndicationBody.of(new byte[]{
                0x29, // Message Code
                0x00, // Additional Info
                (byte) 0xbc, // Control Byte 1
                (byte) 0xd0, // Control Byte 2
                0x10, 0x6e, // Source Address
                0x4c, 0x01, // Destination Address
                0x05, // NPDU Length
                0x00, // TPCI + Packet Number
                (byte) 0x80, // APCI
                0x2B, 0x15, 0x60, (byte)0x99 // Data
        });

        // compare raw data of 'create' and 'create by bytes'
        assertThat(body.getRawData()).containsExactly(bodyByBytes.getRawData());

        // toString
        assertThat(body).hasToString(String.format(
                "RoutingIndicationBody{cemi=%s, rawData=0x29 00 BC D0 10 6E 4C 01 05 00 80 2B 15 60 99}",
                cemi.toString(false)));
    }

    /**
     * Tests {@link RoutingIndicationBody} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> RoutingIndicationBody.of((CEMI) null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("cemi");

        // invalid raw data length
        assertThatThrownBy(() -> RoutingIndicationBody.of((byte[]) null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> RoutingIndicationBody.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> RoutingIndicationBody.of(new byte[0xFF + 1])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }
}
