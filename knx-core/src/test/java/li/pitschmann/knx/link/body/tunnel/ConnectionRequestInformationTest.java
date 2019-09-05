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

import li.pitschmann.knx.link.body.hpai.ConnectionType;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link ConnectionRequestInformation}
 *
 * @author PITSCHR
 */
public final class ConnectionRequestInformationTest {
    /**
     * Valid {@link ConnectionRequestInformation} for tunneling.
     * <p>
     * Tests the {@link ConnectionRequestInformation#useDefault()} and
     * {@link ConnectionRequestInformation#of(byte[])}
     */
    @Test
    public void validConnectionRequestInformationTunnel() {
        // create
        final var criByCreate = ConnectionRequestInformation.useDefault();
        final var criByCreateRawData = ConnectionRequestInformation.of(criByCreate.getRawData());
        assertThat(criByCreateRawData.getLength()).isEqualTo(4);
        assertThat(criByCreateRawData.getConnectionType()).isEqualTo(ConnectionType.TUNNEL_CONNECTION);
        assertThat(criByCreateRawData.getLayerType()).isEqualTo(LayerType.TUNNEL_LINKLAYER);

        // create by bytes
        final var criByValueOf = ConnectionRequestInformation.of(new byte[]{0x04, 0x04, 0x02, 0x00});
        assertThat(criByValueOf.getLength()).isEqualTo(4);
        assertThat(criByValueOf.getConnectionType()).isEqualTo(ConnectionType.TUNNEL_CONNECTION);
        assertThat(criByValueOf.getLayerType()).isEqualTo(LayerType.TUNNEL_LINKLAYER);

        // compare raw data of 'create' and 'create by bytes'
        assertThat(criByCreateRawData.getRawData()).isEqualTo(criByCreateRawData.getRawData());
        assertThat(criByCreateRawData.getRawData()).isEqualTo(criByValueOf.getRawData());
    }

    /**
     * Valid CRI for device management.
     * <p>
     * Tests the {@link ConnectionRequestInformation#useDefault()} and
     * {@link ConnectionRequestInformation#of(byte[])}
     */
    @Test
    public void validConnectionRequestInformationDeviceMgmt() {
        final var criByCreate = ConnectionRequestInformation.useDefault();
        final var criByCreateRawData = ConnectionRequestInformation.of(criByCreate.getRawData());
        assertThat(criByCreateRawData.getLength()).isEqualTo(4);
        assertThat(criByCreateRawData.getConnectionType()).isEqualTo(ConnectionType.TUNNEL_CONNECTION);
        assertThat(criByCreateRawData.getLayerType()).isEqualTo(LayerType.TUNNEL_LINKLAYER);

        // create by bytes
        final var criByValueOf = ConnectionRequestInformation.of(new byte[]{0x04, 0x04, 0x02, 0x00});
        assertThat(criByValueOf.getLength()).isEqualTo(4);
        assertThat(criByValueOf.getConnectionType()).isEqualTo(ConnectionType.TUNNEL_CONNECTION);
        assertThat(criByValueOf.getLayerType()).isEqualTo(LayerType.TUNNEL_LINKLAYER);

        // compare raw data of 'create' and 'create by bytes'
        assertThat(criByCreateRawData.getRawData()).isEqualTo(criByCreateRawData.getRawData());
        assertThat(criByCreateRawData.getRawData()).isEqualTo(criByValueOf.getRawData());
    }

    /**
     * Tests <strong>invalid</strong> connection request information parameters
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> ConnectionRequestInformation.of(null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("criRawData");
        assertThatThrownBy(() -> ConnectionRequestInformation.of(ConnectionType.TUNNEL_CONNECTION, null))
                .isInstanceOf(KnxNullPointerException.class).hasMessageContaining("layerType");
        assertThatThrownBy(() -> ConnectionRequestInformation.of(null, LayerType.TUNNEL_RAW))
                .isInstanceOf(KnxNullPointerException.class).hasMessageContaining("connectionType");

        // out of range
        assertThatThrownBy(() -> ConnectionRequestInformation.of(new byte[3])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("criRawData");
        assertThatThrownBy(() -> ConnectionRequestInformation.of(new byte[4])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("criRawData[0]"); // illegal length (byte 0)
    }

    /**
     * Test {@link ConnectionRequestInformation#toString()} and {@link ConnectionRequestInformation#toString(boolean)}
     */
    @Test
    public void testToString() {
        assertThat(ConnectionRequestInformation.useDefault()).hasToString(
                String.format("ConnectionRequestInformation{length=4 (0x04), connectionType=%s, layerType=%s, rawData=0x04 04 02 00}",
                        ConnectionType.TUNNEL_CONNECTION, LayerType.TUNNEL_LINKLAYER));

        assertThat(ConnectionRequestInformation.of(new byte[]{0x04, 0x03, (byte) 0x80, 0x00}).toString(false))
                .isEqualTo(String.format("ConnectionRequestInformation{length=4 (0x04), connectionType=%s, layerType=%s}",
                        ConnectionType.DEVICE_MANAGEMENT_CONNECTION, LayerType.TUNNEL_BUSMONITOR));
    }
}
