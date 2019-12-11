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

package li.pitschmann.knx.core.net.tunnel;

import li.pitschmann.knx.core.test.AbstractKnxByteEnumTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link LayerType} enum class
 *
 * @author PITSCHR
 */
public final class LayerTypeTest extends AbstractKnxByteEnumTest<LayerType> {
    @Override
    protected int numberOfElements() {
        return 3;
    }

    @Test
    @Override
    public void validValueOf() {
        assertThat(LayerType.valueOf(0x02)).isEqualTo(LayerType.TUNNEL_LINKLAYER);
        assertThat(LayerType.valueOf(0x04)).isEqualTo(LayerType.TUNNEL_RAW);
        assertThat(LayerType.valueOf(0x80)).isEqualTo(LayerType.TUNNEL_BUSMONITOR);
    }

    @Test
    @Override
    public void friendlyName() {
        assertThat(LayerType.TUNNEL_LINKLAYER.getFriendlyName()).isEqualTo("Tunneling Link Layer");
        assertThat(LayerType.TUNNEL_RAW.getFriendlyName()).isEqualTo("Tunneling Raw Layer");
        assertThat(LayerType.TUNNEL_BUSMONITOR.getFriendlyName()).isEqualTo("Tunneling Busmonitor Layer");
    }

    @Test
    @Override
    public void testToString() {
        assertThat(LayerType.TUNNEL_LINKLAYER).hasToString("LayerType{name=TUNNEL_LINKLAYER, friendlyName=Tunneling Link Layer, code=2 (0x02)}");
    }
}
