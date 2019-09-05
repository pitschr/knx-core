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

package li.pitschmann.knx.link;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.link.header.Header;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;
import li.pitschmann.utils.Bytes.FillDirection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link Header}
 *
 * @author PITSCHR
 */
public final class HeaderTest {
    /**
     * Tests the {@link Header} with several <strong>valid</strong> test data and total length.
     */
    @Test
    public void testHeaders() {
        // @formatter:off
        this.assertHeader(ServiceType.CONNECTION_STATE_REQUEST,
                16,
                new byte[]{0x06, 0x10, 0x02, 0x07, 0x00, 0x10,
                        0x11, 0x00, 0x08, 0x01, (byte) 0xc0, (byte) 0xa8, 0x01, 0x18, (byte) 0xe1, (byte) 0xa9}); // complete original stream

        this.assertHeader(ServiceType.DESCRIPTION_REQUEST,
                255,
                Bytes.fillByteArray(new byte[255], new byte[]{0x06, 0x10, 0x02, 0x03, 0x00, (byte) 0xff, 0x08, 0x01}, FillDirection.LEFT_TO_RIGHT));

        this.assertHeader(ServiceType.CONNECT_RESPONSE,
                4711,
                Bytes.fillByteArray(new byte[4711], new byte[]{0x06, 0x10, 0x02, 0x06, 0x12, 0x67,
                        0x11, 0x00, 0x08, 0x01, (byte) 0xc0, (byte) 0xa8, 0x01, 0x10, 0x0e, 0x57, 0x04, 0x04, 0x10, (byte) 0xff}, FillDirection.LEFT_TO_RIGHT));

        this.assertHeader(ServiceType.DEVICE_CONFIGURATION_ACK,
                0xFFFF,
                Bytes.fillByteArray(new byte[0xFFFF], new byte[]{0x06, 0x10, 0x03, 0x11, (byte) 0xff, (byte) 0xff, 0x04, 0x12}, FillDirection.LEFT_TO_RIGHT));
        // @formatter:on
    }

    /**
     * Tests the {@link Header} with several <strong>valid</strong> test data.
     */
    @Test
    public void testHeadersWithBody() {
        final var body = TunnelingAckBody.of(0x33, 0x66, Status.E_TUNNELING_LAYER);
        final var headerByBody = Header.of(body);
        final var headerByCreate = Header.of(ServiceType.TUNNELING_ACK, Header.KNXNET_HEADER_LENGTH + body.getLength());

        assertThat(headerByBody.getRawData()).containsExactly(headerByCreate.getRawData());
    }

    /**
     * Tests the {@link Header} with several <strong>invalid</strong> test data
     */
    @Test
    public void testHeadersInvalid() {
        // test with no service type
        assertThatThrownBy(() -> Header.of(null, -1)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("serviceType");
        // test with no body
        assertThatThrownBy(() -> Header.of((Body)null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("body");
        // test with illegal total length (underflow)
        assertThatThrownBy(() -> Header.of(ServiceType.CONNECT_REQUEST, -1)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("totalLength");
        // test with illegal total length (overflow)
        assertThatThrownBy(() -> Header.of(ServiceType.CONNECT_REQUEST, 0xFFFF + 1)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("totalLength");

        // test with illegal bytes
        assertThatThrownBy(() -> Header.of((byte[])null)).isInstanceOf(KnxNullPointerException.class);
        // test with empty bytes
        assertThatThrownBy(() -> Header.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class);
        // test with small header size ( < 6 bytes)
        assertThatThrownBy(() -> Header.of(new byte[1])).isInstanceOf(KnxNumberOutOfRangeException.class);
        // test with large header size ( > 6 bytes)
        assertThatThrownBy(() -> Header.of(new byte[10])).isInstanceOf(KnxNumberOutOfRangeException.class);
        // test with exact header size, but invalid header length (1st byte = 0x00)
        assertThatThrownBy(() -> Header.of(new byte[6])).isInstanceOf(KnxNumberOutOfRangeException.class);
        // test with exact header size, but invalid protocol version (2nd byte = 0x00)
        assertThatThrownBy(() -> Header.of(new byte[]{Header.KNXNET_HEADER_LENGTH, 0x00, 0x00, 0x00, 0x00, 0x00}))
                .isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    /**
     * Asserts the header if {@link ServiceType} and {@code totalLength} are correctly parsed for
     * <ul>
     * <li>{@link Header#of(ServiceType, int)}</li>
     * <li>{@link Header#of(byte[])}</li>
     * </ul>
     * <p>
     * The param {@code bytes} is the stream to be compared as well against other parameters. This method will also test
     * the string representation from {@link #toString()}.
     *
     * @param serviceType
     * @param totalLength
     * @param bytes
     */
    private void assertHeader(final ServiceType serviceType, final int totalLength, final byte[] bytes) {
        final var testByCreate = Header.of(serviceType, totalLength);
        final var testByCreateRawData = Header.of(testByCreate.getRawData());
        final var testByValueOfRawData = Header.of(bytes);

        // KNX Header Length + Protocol Version are hard-coded
        assertThat(testByCreate.getLength()).isEqualTo(Header.KNXNET_HEADER_LENGTH);
        assertThat(testByCreateRawData.getLength()).isEqualTo(Header.KNXNET_HEADER_LENGTH);
        assertThat(testByValueOfRawData.getLength()).isEqualTo(Header.KNXNET_HEADER_LENGTH);
        assertThat(testByCreate.getProtocolVersion()).isEqualTo(Header.KNXNET_PROTOCOL_VERSION);
        assertThat(testByCreateRawData.getProtocolVersion()).isEqualTo(Header.KNXNET_PROTOCOL_VERSION);
        assertThat(testByValueOfRawData.getProtocolVersion()).isEqualTo(Header.KNXNET_PROTOCOL_VERSION);

        assertThat(testByCreate.getServiceType()).isEqualTo(serviceType);
        assertThat(testByCreateRawData.getServiceType()).isEqualTo(serviceType);
        assertThat(testByValueOfRawData.getServiceType()).isEqualTo(serviceType);

        assertThat(testByCreate.getTotalLength()).isEqualTo(totalLength);
        assertThat(testByCreateRawData.getTotalLength()).isEqualTo(totalLength);
        assertThat(testByValueOfRawData.getTotalLength()).isEqualTo(totalLength);

        // test toString()
        assertThat(testByCreate).hasToString(
                String.format("Header{length=6 (0x06), protocolVersion=16 (0x10), serviceType=%s, totalLength=%s (%s), rawData=%s}",
                        serviceType, totalLength, ByteFormatter.formatHex(totalLength),
                        ByteFormatter.formatHexAsString(Arrays.copyOf(bytes, Header.KNXNET_HEADER_LENGTH))));
    }
}
