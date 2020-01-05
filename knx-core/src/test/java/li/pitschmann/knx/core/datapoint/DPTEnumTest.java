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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPTEnumValue;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPTEnum}
 *
 * @author PITSCHR
 */
public class DPTEnumTest extends AbstractDataPointTypeTest<DPTEnum<DPT20.CommunicationMode>, DPTEnumValue<DPT20.CommunicationMode>> {
    private static final DPTEnum<DPT20.CommunicationMode> DPT_ENUM = new DPTEnum<>("123.456", "foobar");

    static {
        DPT_ENUM.addValue(new DPTEnumValue<>(DPT_ENUM, DPT20.CommunicationMode.DATA_LINK_LAYER, 0, "DATA_LINK_LAYER"));
        DPT_ENUM.addValue(new DPTEnumValue<>(DPT_ENUM, DPT20.CommunicationMode.NO_LAYER, 255, "NO_LAYER"));
    }

    @Override
    @Test
    public void testIdAndDescription() {
        // instance methods
        assertThat(DPT_ENUM.getId()).isEqualTo("123.456");
        assertThat(DPT_ENUM.getDescription()).isEqualTo("foobar");
    }

    @Override
    @Test
    public void testCompatibility() {
        // failures
        assertThatThrownBy(() -> DPT_ENUM.of(new byte[0])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_ENUM.of(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_ENUM.of("0x00", "0x00")).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_ENUM.of("")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_ENUM.of("foo", "bar")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_ENUM.of("-1")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_ENUM.of("256")).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);
        assertThatThrownBy(() -> DPT_ENUM.of(-1)).isInstanceOf(KnxEnumNotFoundException.class);
        assertThatThrownBy(() -> DPT_ENUM.of(256)).isInstanceOf(KnxEnumNotFoundException.class);

        // OK
        assertThat(DPT_ENUM.of("0")).isInstanceOf(DPTEnumValue.class);
        assertThat(DPT_ENUM.of("255")).isInstanceOf(DPTEnumValue.class);
        assertThat(DPT_ENUM.of("DATA_LINK_LAYER")).isInstanceOf(DPTEnumValue.class);
        assertThat(DPT_ENUM.of("NO_LAYER")).isInstanceOf(DPTEnumValue.class);
        assertThat(DPT_ENUM.of(0)).isInstanceOf(DPTEnumValue.class);
        assertThat(DPT_ENUM.of(255)).isInstanceOf(DPTEnumValue.class);
        assertThat(DPT_ENUM.of("0x00")).isInstanceOf(DPTEnumValue.class);
        assertThat(DPT_ENUM.of("0xFF")).isInstanceOf(DPTEnumValue.class);
    }

    @Test
    @DisplayName("Try to parse and test methods of DPTEnumValue")
    public void testParse() {
        final var enumValueDataLinkLayer = DPT_ENUM.parse(new byte[]{0x00});
        assertThat(DPT_ENUM.parse(new String[]{"DATA_LINK_LAYER"})).isSameAs(enumValueDataLinkLayer);
        assertThat(DPT_ENUM.of(0x00)).isSameAs(enumValueDataLinkLayer);
        assertThat(DPT_ENUM.of("DATA_LINK_LAYER")).isSameAs(enumValueDataLinkLayer);
        assertThat(DPT_ENUM.of(new byte[]{0x00})).isSameAs(enumValueDataLinkLayer);
        assertThat(DPT_ENUM.of(new String[]{"DATA_LINK_LAYER"})).isSameAs(enumValueDataLinkLayer);
        assertThat(enumValueDataLinkLayer.getDPT()).isSameAs(DPT_ENUM);
        assertThat(enumValueDataLinkLayer.getOrdinal()).isEqualTo(0);
        assertThat(enumValueDataLinkLayer.getDescription()).isEqualTo("DATA_LINK_LAYER");
        assertThat(enumValueDataLinkLayer.toText()).isEqualTo("DATA_LINK_LAYER");
        assertThat(enumValueDataLinkLayer.getEnum()).isSameAs(DPT20.CommunicationMode.DATA_LINK_LAYER);

        final var enumValueNoLayer = DPT_ENUM.parse(new byte[]{(byte) 0xFF});
        assertThat(DPT_ENUM.parse(new String[]{"NO_LAYER"})).isSameAs(enumValueNoLayer);
        assertThat(DPT_ENUM.of(0xFF)).isSameAs(enumValueNoLayer);
        assertThat(DPT_ENUM.of("NO_LAYER")).isSameAs(enumValueNoLayer);
        assertThat(DPT_ENUM.of(new byte[]{(byte) 0xFF})).isSameAs(enumValueNoLayer);
        assertThat(DPT_ENUM.of(new String[]{"NO_LAYER"})).isSameAs(enumValueNoLayer);
        assertThat(enumValueNoLayer.getDPT()).isSameAs(DPT_ENUM);
        assertThat(enumValueNoLayer.getOrdinal()).isEqualTo(0xFF);
        assertThat(enumValueNoLayer.getDescription()).isEqualTo("NO_LAYER");
        assertThat(enumValueNoLayer.toText()).isEqualTo("NO_LAYER");
        assertThat(enumValueNoLayer.getEnum()).isSameAs(DPT20.CommunicationMode.NO_LAYER);
    }

    @Override
    @Test
    public void testOf() {
        // re-adding with same value = 0 should cause an exception
        assertThatThrownBy(() -> DPT_ENUM.addValue(new DPTEnumValue<>(DPT_ENUM, DPT20.CommunicationMode.DATA_LINK_LAYER, 0, "DATA_LINK_LAYER")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

