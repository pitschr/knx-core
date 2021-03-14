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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.exceptions.KnxUnsupportedAPCICodeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link APCI} enum class
 *
 * @author PITSCHR
 */
final class APCITest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(APCI.values()).hasSize(6);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(APCI.valueOf(0x00)).isEqualTo(APCI.GROUP_VALUE_READ);
        assertThat(APCI.valueOf(0x40)).isEqualTo(APCI.GROUP_VALUE_RESPONSE);
        assertThat(APCI.valueOf(0x80)).isEqualTo(APCI.GROUP_VALUE_WRITE);
        assertThat(APCI.valueOf(0xC0)).isEqualTo(APCI.INDIVIDUAL_ADDRESS_WRITE);
        assertThat(APCI.valueOf(0x100)).isEqualTo(APCI.INDIVIDUAL_ADDRESS_READ);
        assertThat(APCI.valueOf(0x140)).isEqualTo(APCI.INDIVIDUAL_ADDRESS_RESPONSE);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> APCI.valueOf(1))
                .isInstanceOf(KnxUnsupportedAPCICodeException.class)
                .hasMessage("Unsupported APCI code and is not suitable for KNX Net/IP communication: 1 (0000 | 000001)");
        assertThatThrownBy(() -> APCI.valueOf(960))
                .isInstanceOf(KnxUnsupportedAPCICodeException.class)
                .hasMessage("Unsupported APCI code and is not suitable for KNX Net/IP communication: 960 (1111 | 000000)");
        assertThatThrownBy(() -> APCI.valueOf(0xFFFF))
                .isInstanceOf(KnxUnsupportedAPCICodeException.class)
                .hasMessage("Unsupported APCI code and is not suitable for KNX Net/IP communication: 65535 (1111 | 111111)");
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(APCI.GROUP_VALUE_READ.getFriendlyName()).isEqualTo("Group Value Read");
        assertThat(APCI.GROUP_VALUE_RESPONSE.getFriendlyName()).isEqualTo("Group Value Response");
        assertThat(APCI.GROUP_VALUE_WRITE.getFriendlyName()).isEqualTo("Group Value Write");
        assertThat(APCI.INDIVIDUAL_ADDRESS_WRITE.getFriendlyName()).isEqualTo("Individual Address Write");
        assertThat(APCI.INDIVIDUAL_ADDRESS_READ.getFriendlyName()).isEqualTo("Individual Address Read");
        assertThat(APCI.INDIVIDUAL_ADDRESS_RESPONSE.getFriendlyName()).isEqualTo("Individual Address Response");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        // without code range
        assertThat(APCI.GROUP_VALUE_READ).hasToString(
                "APCI{name=GROUP_VALUE_READ, friendlyName=Group Value Read, code=0}"
        );

        // with code range
        assertThat(APCI.GROUP_VALUE_RESPONSE).hasToString(
                "APCI{name=GROUP_VALUE_RESPONSE, friendlyName=Group Value Response, code=64..127}"
        );
    }

    @Test
    @DisplayName("Test #getCodeAsBytes()")
    void testGetCodeAsByte() {
        assertThat(APCI.GROUP_VALUE_READ.getCodeAsBytes()).containsExactly(0x00, 0x00);
        assertThat(APCI.GROUP_VALUE_RESPONSE.getCodeAsBytes()).containsExactly(0x00, 0x40);
        assertThat(APCI.GROUP_VALUE_WRITE.getCodeAsBytes()).containsExactly(0x00, (byte) 0x80);
        assertThat(APCI.INDIVIDUAL_ADDRESS_WRITE.getCodeAsBytes()).containsExactly(0x00, (byte) 0xC0);
        assertThat(APCI.INDIVIDUAL_ADDRESS_READ.getCodeAsBytes()).containsExactly(0x01, 0x00);
        assertThat(APCI.INDIVIDUAL_ADDRESS_RESPONSE.getCodeAsBytes()).containsExactly(0x01, 0x40);
    }

    /*
     * <pre>
     * GROUP_VALUE_RESPONSE        Range: 0x40 .. 0x7F
     * GROUP_VALUE_WRITE 0x80      Range: 0x80 .. 0xBF
     * INDIVIDUAL_ADDRESS_WRITE    Range: 0xC0
     * </pre>
     */
    @Test
    @DisplayName("Test the range of #valueOf() and #isCodeRange()")
    void validRangeTest() {
        // check if correct APCI is returned
        assertThat(APCI.valueOf(0x7F)).isEqualTo(APCI.GROUP_VALUE_RESPONSE);
        assertThat(APCI.valueOf(0x80)).isEqualTo(APCI.GROUP_VALUE_WRITE);
        assertThat(APCI.valueOf(0x81)).isEqualTo(APCI.GROUP_VALUE_WRITE);
        assertThat(APCI.valueOf(0xBE)).isEqualTo(APCI.GROUP_VALUE_WRITE);
        assertThat(APCI.valueOf(0xBF)).isEqualTo(APCI.GROUP_VALUE_WRITE);
        assertThat(APCI.valueOf(0xC0)).isEqualTo(APCI.INDIVIDUAL_ADDRESS_WRITE);

        // check if
        assertThat(APCI.GROUP_VALUE_READ.isCodeRange()).isFalse();
        assertThat(APCI.GROUP_VALUE_WRITE.isCodeRange()).isTrue();
        assertThat(APCI.GROUP_VALUE_RESPONSE.isCodeRange()).isTrue();
    }
}
