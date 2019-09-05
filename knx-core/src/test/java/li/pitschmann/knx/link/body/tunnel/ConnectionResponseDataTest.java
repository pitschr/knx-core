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

package li.pitschmann.knx.link.body.tunnel;

import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.hpai.ConnectionType;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link ConnectionResponseData}
 *
 * @author PITSCHR
 */
public final class ConnectionResponseDataTest {
    private static final IndividualAddress TEST_ADDRESS = IndividualAddress.of(15, 15, 255);

    /**
     * Valid {@link ConnectionResponseData} for tunneling.
     * <p>
     * Tests the {@link ConnectionResponseData#of(IndividualAddress)} and
     * {@link ConnectionResponseData#of(byte[])}
     */
    @Test
    public void validCase() {
        final var criByCreate = ConnectionResponseData.of(TEST_ADDRESS);
        final var criByCreateRawData = ConnectionResponseData.of(criByCreate.getRawData());
        assertThat(criByCreateRawData.getLength()).isEqualTo(4);
        assertThat(criByCreateRawData.getConnectionType()).isEqualTo(ConnectionType.TUNNEL_CONNECTION);
        assertThat(criByCreateRawData.getAddress()).isEqualTo(TEST_ADDRESS);

        // create by bytes
        final var criByValueOf = ConnectionResponseData.of(new byte[]{0x04, 0x04, (byte) 0xFF, (byte) 0xFF});
        assertThat(criByValueOf.getLength()).isEqualTo(4);
        assertThat(criByValueOf.getConnectionType()).isEqualTo(ConnectionType.TUNNEL_CONNECTION);
        assertThat(criByValueOf.getAddress()).isEqualTo(TEST_ADDRESS);

        // compare raw data of 'create' and 'create by bytes'
        assertThat(criByCreateRawData.getRawData()).isEqualTo(criByCreateRawData.getRawData());
        assertThat(criByCreateRawData.getRawData()).isEqualTo(criByValueOf.getRawData());
    }

    /**
     * Tests <strong>invalid</strong> connection response data parameters
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> ConnectionResponseData.of((byte[]) null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("crdRawData");
        assertThatThrownBy(() -> ConnectionResponseData.of((IndividualAddress) null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("address");

        // out of range
        assertThatThrownBy(() -> ConnectionResponseData.of(new byte[3])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("crdRawData");
        assertThatThrownBy(() -> ConnectionResponseData.of(new byte[4])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("crdRawData[0]"); // illegal length (byte 0)
    }

    /**
     * Test {@link ConnectionResponseData#toString()} and {@link ConnectionResponseData#toString(boolean)}
     */
    @Test
    public void testToString() {
        assertThat(ConnectionResponseData.of(TEST_ADDRESS))
                .hasToString(String.format("ConnectionResponseData{length=4 (0x04), connectionType=%s, address=%s, rawData=0x04 04 FF FF}",
                        ConnectionType.TUNNEL_CONNECTION, TEST_ADDRESS.toString(false)));

        assertThat(ConnectionResponseData.of(new byte[]{0x04, 0x06, 0x16, 0x63}).toString(false)).hasToString(String.format(
                "ConnectionResponseData{length=4 (0x04), connectionType=%s, address=%s}", ConnectionType.REMOTE_LOGGING_CONNECTION,
                IndividualAddress.of(new byte[]{0x16, 0x63}).toString(false)));

    }
}
