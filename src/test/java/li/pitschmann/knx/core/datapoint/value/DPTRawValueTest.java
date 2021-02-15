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

package li.pitschmann.knx.core.datapoint.value;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DPTRawValue}
 *
 * @author PITSCHR
 */
class DPTRawValueTest {

    @Test
    @DisplayName("#(byte[]) with: 0x11 22 33 44 55")
    void testByte_11_22_33_44_55() {
        final var value = new DPTRawValue(new byte[]{0x11, 0x22, 0x33, 0x44, 0x55});
        assertThat(value.toByteArray()).containsExactly(0x11, 0x22, 0x33, 0x44, 0x55);

        assertThat(value.toText()).isEqualTo("0x11 22 33 44 55");
    }

    @Test
    @DisplayName("#(byte[]) with: 0xAA BB CC DD EE FF")
    void testByte_AA_BB_CC_DD_EE_FF() {
        final var value = new DPTRawValue(new byte[]{(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF});
        assertThat(value.toByteArray()).containsExactly(0xAA, 0xBB, 0xCC, 0xDD, 0xEE, 0xFF);

        assertThat(value.toText()).isEqualTo("0xAA BB CC DD EE FF");
    }

    @Test
    @DisplayName("#toString()")
    void testToString() {
        final var value = new DPTRawValue(new byte[]{0x43, (byte) 0x83, (byte) 0xAD, 0x21});
        assertThat(value).hasToString(
                "DPTRawValue{dpt=raw, byteArray=0x43 83 AD 21}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(DPTRawValue.class).withIgnoredFields("dpt").verify();
    }
}
