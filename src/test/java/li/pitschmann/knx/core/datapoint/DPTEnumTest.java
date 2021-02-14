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
class DPTEnumTest {
    private static final DPTEnum<DPT20.CommunicationMode> DPT_ENUM = new DPTEnum<>("123.456", "foobar");
    private static final DPTEnumValue<DPT20.CommunicationMode> VALUE_LINK_LAYER = new DPTEnumValue<>(DPT_ENUM, DPT20.CommunicationMode.DATA_LINK_LAYER, 0, "DATA_LINK_LAYER");
    private static final DPTEnumValue<DPT20.CommunicationMode> VALUE_NO_LAYER = new DPTEnumValue<>(DPT_ENUM, DPT20.CommunicationMode.NO_LAYER, 255, "NO_LAYER");

    static {
        // register enumeration values
        DPT_ENUM.addValue(VALUE_LINK_LAYER);
        DPT_ENUM.addValue(VALUE_NO_LAYER);
    }

    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        assertThat(DPT_ENUM.getId()).isEqualTo("123.456");
        assertThat(DPT_ENUM.getDescription()).isEqualTo("foobar");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT_ENUM;
        // byte is supported for length == 1 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isTrue();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT_ENUM;
        // String is supported for length == 1 only
        // First String may not be null nor empty
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isFalse();
        assertThat(dpt.isCompatible(new String[2])).isFalse();

        assertThat(dpt.isCompatible(new String[]{null})).isFalse();
        assertThat(dpt.isCompatible(new String[]{""})).isFalse();
        assertThat(dpt.isCompatible(new String[]{"something"})).isTrue();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    public void testByteParse() {
        final var dpt = DPT_ENUM;
        assertThat(dpt.parse(new byte[]{0x00})).isSameAs(VALUE_LINK_LAYER);
        assertThat(dpt.parse(new byte[]{(byte) 0xFF})).isSameAs(VALUE_NO_LAYER);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    public void testStringParse() {
        final var dpt = DPT_ENUM;

        // try parse by ordinal value
        assertThat(dpt.parse(new String[]{"0"})).isSameAs(VALUE_LINK_LAYER);
        assertThat(dpt.parse(new String[]{"255"})).isSameAs(VALUE_NO_LAYER);

        // try parse by non-existing ordinal value
        assertThatThrownBy(() -> dpt.parse(new String[]{"999"}))
                .isInstanceOf(KnxEnumNotFoundException.class)
                .hasMessage("Could not find data point enum value for dpt '123.456' and value '999'.");

        // try parse by description
        assertThat(dpt.parse(new String[]{"DATA_LINK_LAYER"})).isSameAs(VALUE_LINK_LAYER);
        assertThat(dpt.parse(new String[]{"NO_LAYER"})).isSameAs(VALUE_NO_LAYER);

        // try parse by non-existing description
        assertThatThrownBy(() -> dpt.parse(new String[]{"LOREM_IPSUM"}))
                .isInstanceOf(KnxEnumNotFoundException.class)
                .hasMessage("Could not find data point enum value for dpt '123.456' and value 'LOREM_IPSUM'.");
    }

    @Test
    @DisplayName("Test #of(int)")
    void testOf() {
        final var dpt = DPT_ENUM;
        // existing enumeration value
        assertThat(dpt.of(0)).isSameAs(VALUE_LINK_LAYER);
        assertThat(dpt.of(255)).isSameAs(VALUE_NO_LAYER);

        // non-existing enumeration value
        assertThatThrownBy(() -> dpt.of(999)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #addValue(DPTEnumValue)")
    void testAddValue() {
        final var enumValue = new DPTEnumValue<>(DPT_ENUM, DPT20.CommunicationMode.DATA_LINK_LAYER, 100, "SOMETHING");

        // adding first time should be fine
        DPT_ENUM.addValue(enumValue);

        // adding second time should fail, as the ordinal = 100 is added already
        assertThatThrownBy(() -> DPT_ENUM.addValue(enumValue)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Test #equals(Object)")
    void testEquals() {
        // should be equal - same reference
        assertThat(DPT_ENUM.equals(DPT_ENUM)).isTrue();

        // should be equal - same id
        final var enumWithSameId = new DPTEnum<>("123.456", "");
        assertThat(DPT_ENUM.equals(enumWithSameId)).isTrue();

        // should not be equal
        assertThat(DPT_ENUM.equals(null)).isFalse();
        assertThat(DPT_ENUM.equals(new Object())).isFalse();

        // should not be equal - different id
        final var enumWithDifferentId = new DPTEnum<>("123.999", "");
        assertThat(DPT_ENUM.equals(enumWithDifferentId)).isFalse();
    }

    @Test
    @DisplayName("Test #hashCode()")
    void testHashCode() {
        // hash code should be same
        final var enumWithSameId = new DPTEnum<>("123.456", "");
        assertThat(DPT_ENUM.hashCode()).isEqualTo(enumWithSameId.hashCode());

        // hash code should not be same
        final var enumWithDifferentId = new DPTEnum<>("123.999", "");
        assertThat(DPT_ENUM.hashCode()).isNotEqualTo(enumWithDifferentId.hashCode());
    }
}