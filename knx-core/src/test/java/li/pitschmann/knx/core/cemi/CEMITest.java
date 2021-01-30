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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.address.AddressType;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.address.KnxAddress;
import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.datapoint.DPT5;
import li.pitschmann.knx.core.datapoint.DPT7;
import li.pitschmann.knx.core.datapoint.DPT8;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Test case for {@link CEMI}
 *
 * @author PITSCHR
 */
final class CEMITest {

    @Test
    @DisplayName("Test #useDefault(..)")
    void testUseDefaultMethods() {
        final var knxAddress = IndividualAddress.of(5, 9, 15);
        final var bytes = new byte[]{(byte) 0xCC, (byte) 0xEE};
        final var dptValue = DPT7.LENGTH.of(bytes);

        final var cemiDefault = CEMI.useDefault(MessageCode.L_DATA_IND, knxAddress, APCI.GROUP_VALUE_WRITE, dptValue);

        // assert
        assertThat(cemiDefault.getRawData()).containsExactly(
                0x29, // Message Code
                0x00, // Additional Info Length
                0xBC, // ControlByte1
                0x60, // ControlByte2
                0x00, 0x00, // source address
                0x59, 0x0F, // destination address
                0x03, // NDPU length
                0x00, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                0x80, // APCI (8bits)
                0xCC, 0xEE // APCI data
        );
    }

    @Test
    @DisplayName("Test #of(..)")
    void testOfMethods() {
        final var knxAddress = GroupAddress.of(2, 4, 230);
        final var bytes = new byte[]{(byte) 0xEE, (byte) 0xFF};
        final var dptValue = DPT7.LENGTH.of(bytes);

        final var cemi = CEMI.of(
                MessageCode.L_DATA_CON,
                AdditionalInfo.empty(),
                ControlByte1.useDefault(),
                ControlByte2.of(knxAddress),
                IndividualAddress.useDefault(),
                knxAddress,
                TPCI.UNNUMBERED_PACKAGE,
                0,
                APCI.GROUP_VALUE_WRITE,
                dptValue);

        // assert
        assertThat(cemi.getRawData()).containsExactly(
                0x2E, // Message Code
                0x00, // Additional Info Length
                0xBC, // ControlByte1
                0xE0, // ControlByte2
                0x00, 0x00, // source address
                0x14, (byte) 0xE6, // destination address
                0x03, // NDPU length
                0x00, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                0x80, // APCI (8bits)
                0xEE, 0xFF // APCI data
        );
    }

    /**
     * Test for L_Data Confirmation, A_GroupValue_Read
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.con (0x2e)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0xe0
     * 1... .... = Destination address type: 1
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.255
     * Destination Address 1/2/150 or 1/662
     * NPDU length: 1 octet
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..00 0000 0000 = APCI: A_GroupValue_Read (0x0000)
     * </pre>
     */
    @Test
    @DisplayName("Test CEMI with confirmation (apci: GroupValue_Read)")
    void validGroupValueRead() {
        final var additionalInfo = AdditionalInfo.empty();
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.GROUP, 6, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 255);
        final var destinationAddress = GroupAddress.of(1, 2, 150);

        // create
        final var cemi = CEMI.of(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, null);

        assertThat(cemi.getRawData()).containsExactly(
                0x2E, // Message Code
                0x00, // Additional Info Length
                0xBC, // ControlByte1
                0xE0, // ControlByte2
                0x10, 0xFF, // source address
                0x0A, 0x96, // destination address
                0x01, // NDPU length
                0x00, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                0x00 // APCI (8bits), no APCI data
        );

        assertCEMI(cemi, MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0]);
    }

    /**
     * Test for L_Data Indication, A_GroupValue_Write (value inside APCI byte)
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xa8
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...0 .... = System-Broadcast: 0
     * .... 10.. = Priority: 0x2
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0xbc
     * 1... .... = Destination address type: 1
     * .011 .... = Hop count: 3
     * .... 1100 = Extended Frame Format: 0x0c
     * Source Address 1.0.21
     * Destination Address 1/3/203 or 1/971
     * NPDU length: 1 octet
     * 01.. .... = TPCI: NDT (Numbered Data Packet) (0x40)
     * ..00 01.. = Package Number
     * .... ..00 10.. .... = APCI: A_GroupValue_Write (0x0002)
     * ..00 0001 = Data: 0x01
     * </pre>
     */
    @Test
    @DisplayName("Test CEMI with indication (apci: GroupValue_Write) - one-byte value inside APCI bytes")
    void validGroupValueWriteApciDataInside() {
        final var additionalInfo = AdditionalInfo.empty();
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.SYSTEM, Priority.URGENT, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.GROUP, 3, 0x0C);
        final var sourceAddress = IndividualAddress.of(1, 0, 21);
        final var destinationAddress = GroupAddress.of(1, 3, 203);

        // create
        final var cemi = CEMI.of(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.NUMBERED_PACKAGE, 1, APCI.GROUP_VALUE_WRITE, DPT1.SWITCH.of(true));

        assertThat(cemi.getRawData()).containsExactly(
                0x29, // Message Code
                0x00, // Additional Info Length
                0xA8, // ControlByte1
                0xBC, // ControlByte2
                0x10, 0x15, // source address
                0x0B, 0xCB, // destination address
                0x01, // NDPU length
                0x44, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                0x81 // APCI (8bits), APCI data (optimized)
        );

        assertCEMI(cemi, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
                TPCI.NUMBERED_PACKAGE, 1, APCI.GROUP_VALUE_WRITE, new byte[]{0x01});
    }

    /**
     * Test for L_Data Indication, A_GroupValue_Write (one-byte value outside of APCI byte)
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0xe0
     * 1... .... = Destination address type: 1
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.21
     * Destination Address 1/3/204 or 1/972
     * NPDU length: 2 octets
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..00  10.. .... = APCI: A_GroupValue_Write (0x0002)
     * data: 5d
     * </pre>
     */
    @Test
    @DisplayName("Test CEMI with indication (apci: GroupValue_Write) - one-byte value outside APCI bytes")
    void validGroupValueWriteApciDataOneByteAppended() {
        final var additionalInfo = AdditionalInfo.empty();
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.GROUP, 6, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 21);
        final var destinationAddress = GroupAddress.of(1, 3, 204);

        // create
        final var cemi = CEMI.of(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, DPT5.VALUE_1_OCTET_UNSIGNED_COUNT.of(93));

        assertThat(cemi.getRawData()).containsExactly(
                0x29, // Message Code
                0x00, // Additional Info Length
                0xBC, // ControlByte1
                0xE0, // ControlByte2
                0x10, 0x15, // source address
                0x0B, 0xCC, // destination address
                0x02, // NDPU length
                0x00, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                0x80, // APCI (8bits)
                0x5d  // APCI data
        );

        assertCEMI(cemi, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 2,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x5d});
    }

    /**
     * Test for L_Data Indication, A_GroupValue_Write (two-bytes value outside of APCI byte) *
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0x60
     * 0... .... = Destination address type: 0
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.130
     * Destination Address 6.3.67
     * NPDU length: 3 octets
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..00  10.. .... = APCI: A_GroupValue_Write (0x0002)
     * data: 0c09
     * </pre>
     */
    @Test
    @DisplayName("Test CEMI with indication (apci: GroupValue_Write) - two-byte value outside APCI bytes")
    void validGroupValueWriteApciDataTwoBytesAppended() {
        final var additionalInfo = AdditionalInfo.empty();
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.INDIVIDUAL, 6, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 130);
        final var destinationAddress = IndividualAddress.of(6, 3, 67);

        // create
        final var cemi = CEMI.of(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, DPT8.VALUE_2_OCTET_COUNT.of(3081));

        assertThat(cemi.getRawData()).containsExactly(
                0x29, // Message Code
                0x00, // Additional Info Length
                0xBC, // ControlByte1
                0x60, // ControlByte2
                0x10, 0x82, // source address
                0x63, 0x43, // destination address
                0x03, // NDPU length
                0x00, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                0x80, // APCI (8bits)
                0x0C, 0x09 // APCI data
        );

        assertCEMI(cemi, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 3,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x0c, 0x09});
    }

    /**
     * Test for L_Data Indication, A_GroupValue_Response
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0x60
     * 0... .... = Destination address type: 0
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.130
     * Destination Address 6.3.67
     * NPDU length: 3 octets
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..00  10.. .... = APCI: A_GroupValue_Response (0x0040)
     * data: fc46
     * </pre>
     */
    @Test
    @DisplayName("Test CEMI with indication (apci: GroupValue_Response)")
    void testGroupValueResponse() {
        final var additionalInfo = AdditionalInfo.empty();
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.INDIVIDUAL, 6, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 130);
        final var destinationAddress = IndividualAddress.of(6, 3, 67);

        // create
        final var cemi = CEMI.of(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_RESPONSE, DPT7.VALUE_2_OCTET_UNSIGNED_COUNT.of(64582));

        assertThat(cemi.getRawData()).containsExactly(
                0x29, // Message Code
                0x00, // Additional Info Length
                0xBC, // ControlByte1
                0x60, // ControlByte2
                0x10, 0x82, // source address
                0x63, 0x43, // destination address
                0x03, // NDPU length
                0x00, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                0x40, // APCI (8bits)
                0xFC, 0x46 // APCI data
        );

        // assert
        assertCEMI(cemi, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 3,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_RESPONSE, new byte[]{(byte) 0xFC, 0x46});
    }

    @Test
    @DisplayName("Test #of(byte[]) with additional info")
    void testWithAdditionalInfoLength() {
        // create by bytes
        final var cemiAsBytes = CEMI.of(new byte[]{
                0x29, // Message Code
                0x04, // Additional Info Length
                0x01, 0x02, 0x03, 0x04, // Additional Info Data
                (byte) 0xBC, // ControlByte1
                0x60, // ControlByte2
                0x10, (byte) 0x82, // source address
                0x63, 0x43, // destination address
                0x03, // NDPU length
                0x00, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                0x40, // APCI (8bits)
                (byte) 0xFC, 0x46 // APCI data
        });

        // create
        final var additionalInfo = AdditionalInfo.of(new byte[]{0x01, 0x02, 0x03, 0x04});
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.INDIVIDUAL, 6, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 130);
        final var destinationAddress = IndividualAddress.of(6, 3, 67);

        // create
        final var cemi = CEMI.of(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_RESPONSE, DPT7.VALUE_2_OCTET_UNSIGNED_COUNT.of(64582));
        assertThat(cemi.getAdditionalInfo().toByteArray()).containsExactly(0x01, 0x02, 0x03, 0x04);

        // compare the byte array of 'create' and 'create by bytes'
        assertThat(cemi.toByteArray()).containsExactly(cemiAsBytes.toByteArray());
    }

    @Test
    @DisplayName("Test #of(byte[]) with null or empty byte array")
    void testWithNullAndEmptyByteArray() {
        assertThatThrownBy(() -> CEMI.of(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Bytes is required.");

        assertThatThrownBy(() -> CEMI.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [11..250] but was: 0");
    }

    @Test
    @DisplayName("Test #of(..) with missing mandatory arguments")
    void testWithoutMandatoryArguments() {
        assertThatThrownBy(() -> CEMI.of(
                null, // issue!
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                mock(TPCI.class),
                0,
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("Message Code is required.");

        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                null, // issue!
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                mock(TPCI.class),
                0,
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("Additional Info is required.");

        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                null, // issue!
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                mock(TPCI.class),
                0,
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("Control Byte 1 is required.");

        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                null, // issue!
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                mock(TPCI.class),
                0,
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("Control Byte 2 is required.");

        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                null, // issue!
                mock(GroupAddress.class),
                mock(TPCI.class),
                0,
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("Source Address is required.");

        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                null, // issue!
                mock(TPCI.class),
                0,
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("Destination Address is required.");

        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                null, // issue!
                0,
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("TPCI is required.");

        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                mock(TPCI.class),
                0,
                null, // issue!
                mock(DataPointValue.class))
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("APCI is required.");
    }

    @Test
    @DisplayName("Test #of(..) with packet number out of range")
    void testOutOfRangeArguments() {
        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                mock(TPCI.class),
                -1, // issue!
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible packet number. Expected [0..255] but was: -1");

        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                mock(TPCI.class),
                256, // issue!
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible packet number. Expected [0..255] but was: 256");
    }

    @Test
    @DisplayName("Test #of(..) with illegal, unsupported or incompatible arguments")
    void testIllegalArguments() {
        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                TPCI.UNNUMBERED_PACKAGE, // issue!
                1, // issue!
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TPCI packet number should not be set when TCPI is unnumbered: tpci=UNNUMBERED_PACKAGE, packetNumber=1");

        assertThatThrownBy(() -> CEMI.of(
                mock(MessageCode.class),
                mock(AdditionalInfo.class),
                mock(ControlByte1.class),
                mock(ControlByte2.class),
                mock(IndividualAddress.class),
                mock(GroupAddress.class),
                TPCI.UNNUMBERED_CONTROL_DATA, // issue!
                1, // issue!
                mock(APCI.class),
                mock(DataPointValue.class))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TPCI packet number should not be set when TCPI is unnumbered: tpci=UNNUMBERED_CONTROL_DATA, packetNumber=1");

        assertThatThrownBy(() -> CEMI.useDefault(
                MessageCode.L_DATA_CON,
                GroupAddress.of(1, 2, 3),
                APCI.INDIVIDUAL_ADDRESS_READ, // issue!
                null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Current APCI is not supported: INDIVIDUAL_ADDRESS_READ");
    }

    @Test
    @DisplayName("Test CEMI with WRITE/RESPONSE APCI and without mandatory DataPointType")
    void testMissingArgumentsForWriteResponseAPCI() {
        final var destinationAddress = GroupAddress.of(1, 2, 150);

        assertThatThrownBy(() -> CEMI.useDefault(
                MessageCode.L_DATA_CON,
                GroupAddress.of(1, 2, 3),
                APCI.GROUP_VALUE_WRITE,
                null) // issue!
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("Data Point Value is required for APCI: GROUP_VALUE_WRITE");

        assertThatThrownBy(() -> CEMI.useDefault(
                MessageCode.L_DATA_CON,
                GroupAddress.of(1, 2, 3),
                APCI.GROUP_VALUE_RESPONSE, null) // issue!
        ).isInstanceOf(NullPointerException.class)
                .hasMessage("Data Point Value is required for APCI: GROUP_VALUE_RESPONSE");
    }

    @Test
    @DisplayName("Test conflict between ControlByte2 and Address Type of destination")
    void testAddressTypeIncompatbility() {
        // ControlByte2#addressType = GROUP, destination = INDIVIDUAL
        assertThatThrownBy(() -> CEMI.of(
                MessageCode.L_DATA_CON,
                AdditionalInfo.empty(),
                ControlByte1.useDefault(),
                ControlByte2.of(AddressType.GROUP, 6, 0), // conflict with destination
                IndividualAddress.useDefault(),
                IndividualAddress.of(1, 2, 3), // conflict with ControlByte2
                TPCI.UNNUMBERED_PACKAGE,
                0,
                APCI.GROUP_VALUE_READ,
                null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible address type between destinationAddress (INDIVIDUAL) and ControlByte2#addressType (GROUP) detected.");

        // ControlByte2#addressType = INDIVIDUAL, destination = GROUP
        assertThatThrownBy(() -> CEMI.of(
                MessageCode.L_DATA_CON,
                AdditionalInfo.empty(),
                ControlByte1.useDefault(),
                ControlByte2.of(AddressType.INDIVIDUAL, 6, 0), // conflict with destination
                IndividualAddress.useDefault(),
                GroupAddress.of(1, 2, 3), // conflict with ControlByte2
                TPCI.UNNUMBERED_PACKAGE,
                0,
                APCI.GROUP_VALUE_READ,
                null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible address type between destinationAddress (GROUP) and ControlByte2#addressType (INDIVIDUAL) detected.");
    }

    @Test
    @DisplayName("Test #of(byte[]) with wrong NDPU length")
    void testWithWrongNDPULength() {
        // the APCI data is 2-byte array, therefore the NDPU length should
        // be 3-bytes (=1 byte for APCI Type, 2 bytes for APCI data)

        // here we just tell that NDPU length is 4 bytes which is incorrect!
        assertThatThrownBy(() -> CEMI.of(new byte[]{
                        0x2E, // Message Code
                        0x00, // Additional Info Length
                        (byte) 0xBC, // ControlByte1
                        (byte) 0xE0, // ControlByte2
                        0x10, (byte) 0xFF, // source address
                        0x0A, (byte) 0x96, // destination address
                        0x04, // NDPU length <-- ISSUE
                        0x00, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                        (byte) 0x80, // APCI (8bits)
                        0x00, 0x00 // APCI data
                })
        ).isInstanceOf(KnxIllegalArgumentException.class)
                .hasMessageStartingWith("There seems be a conflict with NDPU length (4), NPDU Start Index (11), NPDU End Index (14) and CEMI raw (length=13):");

        // here we just tell that NDPU length is 2 bytes which is incorrect!
        assertThatThrownBy(() -> CEMI.of(new byte[]{
                        0x2E, // Message Code
                        0x00, // Additional Info Length
                        (byte) 0xBC, // ControlByte1
                        (byte) 0xE0, // ControlByte2
                        0x10, (byte) 0xFF, // source address
                        0x0A, (byte) 0x96, // destination address
                        0x02, // NDPU length <-- ISSUE
                        0x00, // TPCI (first 2 bits) + TPCI packet number (4 bits) + APCI (2 bits)
                        (byte) 0x80, // APCI (8bits)
                        0x00, 0x00 // APCI data
                })
        ).isInstanceOf(KnxIllegalArgumentException.class)
                .hasMessageStartingWith("There seems be a conflict with NDPU length (2), NPDU Start Index (11), NPDU End Index (12) and CEMI raw (length=13):");
    }

    @Test
    @DisplayName("Test #toString() with Address Type: Group")
    void testToStringWithRawData() {
        // with raw data
        final var additionalInfo = AdditionalInfo.empty();
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.GROUP, 6, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 255);
        final var destinationAddress = GroupAddress.of(1, 2, 150);

        final var cemi = CEMI.of(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, null);

        // with raw data
        assertThat(cemi).hasToString(String.format(
                "CEMI{" +
                        "messageCode=L_DATA_CON, " +
                        "additionalInfo=%s, " +
                        "controlByte1=%s, " +
                        "controlByte2=%s, " +
                        "sourceAddress=%s, " +
                        "destinationAddress=%s, " +
                        "npduLength=1, " +
                        "tpci=UNNUMBERED_PACKAGE, " +
                        "packetNumber=0, " +
                        "apci=GROUP_VALUE_READ, " +
                        "data=" +
                        "}",
                additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress
        ));
    }

    @Test
    @DisplayName("Test #toString() with Address Type: Individual")
    void testToStringWithRawData2() {
        final var additionalInfo = AdditionalInfo.empty();
        final var controlByte1 = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final var controlByte2 = ControlByte2.of(AddressType.INDIVIDUAL, 6, 0);
        final var sourceAddress = IndividualAddress.of(1, 0, 130);
        final var destinationAddress = IndividualAddress.of(6, 3, 67);
        final var dataPointValue = DPT7.VALUE_2_OCTET_UNSIGNED_COUNT.of(3081);

        // create
        final var cemi = CEMI.of(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, dataPointValue);

        assertThat(cemi).hasToString(String.format(
                "CEMI{" +
                        "messageCode=L_DATA_IND, " +
                        "additionalInfo=%s, " +
                        "controlByte1=%s, " +
                        "controlByte2=%s, " +
                        "sourceAddress=%s, " +
                        "destinationAddress=%s, " +
                        "npduLength=3, " +
                        "tpci=UNNUMBERED_PACKAGE, " +
                        "packetNumber=0, " +
                        "apci=GROUP_VALUE_WRITE, " +
                        "data=0x0C 09" +
                        "}",
                additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress
        ));
    }

    /**
     * Asserts the given {@link CEMI} it has been read and processed correctly.
     *
     * @param cemi               CEMI to be asserted
     * @param messageCode        expected message code
     * @param additionalInfo     expected additional information
     * @param controlByte1       expected first control byte
     * @param controlByte2       expected second control byte
     * @param sourceAddress      expected source individual address
     * @param destinationAddress expected destination KNX address
     * @param ndpuLength         expected NDPU length
     * @param tpci               expected TPCI
     * @param packetNumber       expected TPCI packet number
     * @param apci               expected APCI
     * @param data               expected APCI data
     */
    private void assertCEMI(final CEMI cemi,
                            final MessageCode messageCode,
                            final AdditionalInfo additionalInfo,
                            final ControlByte1 controlByte1,
                            final ControlByte2 controlByte2,
                            final IndividualAddress sourceAddress,
                            final KnxAddress destinationAddress,
                            final int ndpuLength,
                            final TPCI tpci,
                            final int packetNumber,
                            final APCI apci,
                            final byte[] data) {
        assertThat(cemi.getMessageCode()).isEqualTo(messageCode);
        assertThat(cemi.getAdditionalInfo()).isEqualTo(additionalInfo);
        assertThat(cemi.getControlByte1()).isEqualTo(controlByte1);
        assertThat(cemi.getControlByte2()).isEqualTo(controlByte2);
        assertThat(cemi.getSourceAddress()).isEqualTo(sourceAddress);
        assertThat(cemi.getDestinationAddress()).isEqualTo(destinationAddress);
        assertThat(cemi.getLength()).isEqualTo(ndpuLength);
        assertThat(cemi.getTPCI()).isSameAs(tpci);
        assertThat(cemi.getPacketNumber()).isEqualTo(packetNumber);
        assertThat(cemi.getAPCI()).isSameAs(apci);
        assertThat(cemi.getData()).containsExactly(data);

        // test byte array
        final var cemiByRawData = CEMI.of(cemi.toByteArray());
        assertThat(cemi.toByteArray()).containsExactly(cemiByRawData.toByteArray());
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(CEMI.class).verify();
    }

}
