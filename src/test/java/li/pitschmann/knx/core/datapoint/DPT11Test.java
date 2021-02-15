/*
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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT11Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT11}
 *
 * @author PITSCHR
 */
class DPT11Test {
    @Test
    @DisplayName("Test #getId() and #getDescription()")
    void testIdAndDescription() {
        final var dpt = DPT11.DATE;
        assertThat(dpt.getId()).isEqualTo("11.001");
        assertThat(dpt.getDescription()).isEqualTo("Date");
    }

    @Test
    @DisplayName("Test #of(byte[])")
    void testByteCompatibility() {
        final var dpt = DPT11.DATE;
        // byte is supported for length == 3 only
        assertThat(dpt.isCompatible(new byte[0])).isFalse();
        assertThat(dpt.isCompatible(new byte[1])).isFalse();
        assertThat(dpt.isCompatible(new byte[2])).isFalse();
        assertThat(dpt.isCompatible(new byte[3])).isTrue();
        assertThat(dpt.isCompatible(new byte[4])).isFalse();
    }

    @Test
    @DisplayName("Test #of(String[])")
    void testStringCompatibility() {
        final var dpt = DPT11.DATE;
        // String is supported for length == 1 only
        // YYYY-MM-DD
        assertThat(dpt.isCompatible(new String[0])).isFalse();
        assertThat(dpt.isCompatible(new String[1])).isTrue(); // HH:MM:SS
        assertThat(dpt.isCompatible(new String[2])).isFalse();
    }

    @Test
    @DisplayName("Test #parse(byte[])")
    void testByteParse() {
        final var dpt = DPT11.DATE;
        assertThat(dpt.parse(new byte[]{0x01, 0x01, 0x00})).isInstanceOf(DPT11Value.class);
        assertThat(dpt.parse(new byte[]{(byte) 0x1F, 0x0C, 0x63})).isInstanceOf(DPT11Value.class);
    }

    @Test
    @DisplayName("Test #parse(String[])")
    void testStringParse() {
        final var dpt = DPT11.DATE;

        // date: 1990-01-01
        final var dateMin = dpt.parse(new String[]{"1990-01-01"});
        assertThat(dateMin.getDate()).isEqualTo(LocalDate.of(1990, 1, 1));

        // date: 2089-12-31
        final var dateMax = dpt.parse(new String[]{"2089-12-31"});
        assertThat(dateMax.getDate()).isEqualTo(LocalDate.of(2089, 12, 31));
    }

    @Test
    @DisplayName("Test #parse(String[]) with invalid cases")
    void testStringParseInvalidCases() {
        final var dpt = DPT11.DATE;

        // no date provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"foobar"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date missing (supported format: 'yyyy-mm-dd'). Provided: [foobar]");
        // wrong date format provided
        assertThatThrownBy(() -> dpt.parse(new String[]{"01.02.2020"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date missing (supported format: 'yyyy-mm-dd'). Provided: [01.02.2020]");
    }

    @Test
    @DisplayName("Test #of(LocalDate)")
    void testOf() {
        final var dpt = DPT11.DATE;
        assertThat(dpt.of(LocalDate.now())).isInstanceOf(DPT11Value.class);
    }
}
