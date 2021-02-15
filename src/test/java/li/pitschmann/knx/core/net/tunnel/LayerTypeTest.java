/*
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

import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link LayerType} enum class
 *
 * @author PITSCHR
 */
final class LayerTypeTest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(LayerType.values()).hasSize(3);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(LayerType.valueOf(0x02)).isEqualTo(LayerType.TUNNEL_LINKLAYER);
        assertThat(LayerType.valueOf(0x04)).isEqualTo(LayerType.TUNNEL_RAW);
        assertThat(LayerType.valueOf(0x80)).isEqualTo(LayerType.TUNNEL_BUSMONITOR);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> LayerType.valueOf(0x00)).isInstanceOf(KnxEnumNotFoundException.class);
        assertThatThrownBy(() -> LayerType.valueOf(0xFF)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(LayerType.TUNNEL_LINKLAYER.getFriendlyName()).isEqualTo("Tunneling Link Layer");
        assertThat(LayerType.TUNNEL_RAW.getFriendlyName()).isEqualTo("Tunneling Raw Layer");
        assertThat(LayerType.TUNNEL_BUSMONITOR.getFriendlyName()).isEqualTo("Tunneling Busmonitor Layer");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        assertThat(LayerType.TUNNEL_LINKLAYER).hasToString(
                "LayerType{name=TUNNEL_LINKLAYER, friendlyName=Tunneling Link Layer, code=2}"
        );
    }
}
