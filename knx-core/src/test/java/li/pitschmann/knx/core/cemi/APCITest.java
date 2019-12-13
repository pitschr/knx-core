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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.AbstractKnxEnumTest;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.utils.Bytes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link APCI} enum class
 *
 * @author PITSCHR
 */
public final class APCITest extends AbstractKnxEnumTest<APCI> {
    @Override
    protected int numberOfElements() {
        return 6;
    }

    @Override
    @Test
    public void invalidValueOf() {
        // bytes
        final var testInvalidBytes = new byte[][]{new byte[]{0x02}, new byte[]{0x02, 0x01, 0x00}};
        for (final var testInvalidByte : testInvalidBytes) {
            assertThatThrownBy(() -> APCI.valueOf(Bytes.toUnsignedInt(testInvalidByte))).isInstanceOf(KnxEnumNotFoundException.class);
        }

        // integers
        final var testInvalidInts = new int[]{-1, 0x200};
        for (final var testInvalidInt : testInvalidInts) {
            assertThatThrownBy(() -> APCI.valueOf(testInvalidInt)).isInstanceOf(KnxEnumNotFoundException.class);
        }
    }

    @Test
    @Override
    public void validValueOf() {
        assertThat(APCI.valueOf(0x00)).isEqualTo(APCI.GROUP_VALUE_READ);
        assertThat(APCI.valueOf(0x40)).isEqualTo(APCI.GROUP_VALUE_RESPONSE);
        assertThat(APCI.valueOf(0x80)).isEqualTo(APCI.GROUP_VALUE_WRITE);
        assertThat(APCI.valueOf(0xC0)).isEqualTo(APCI.INDIVIDUAL_ADDRESS_WRITE);
        assertThat(APCI.valueOf(0x100)).isEqualTo(APCI.INDIVIDUAL_ADDRESS_READ);
        assertThat(APCI.valueOf(0x140)).isEqualTo(APCI.INDIVIDUAL_ADDRESS_RESPONSE);
    }

    @Test
    @Override
    public void friendlyName() {
        assertThat(APCI.GROUP_VALUE_READ.getFriendlyName()).isEqualTo("Group Value Read");
        assertThat(APCI.GROUP_VALUE_RESPONSE.getFriendlyName()).isEqualTo("Group Value Response");
        assertThat(APCI.GROUP_VALUE_WRITE.getFriendlyName()).isEqualTo("Group Value Write");
        assertThat(APCI.INDIVIDUAL_ADDRESS_WRITE.getFriendlyName()).isEqualTo("Individual Address Write");
        assertThat(APCI.INDIVIDUAL_ADDRESS_READ.getFriendlyName()).isEqualTo("Individual Address Read");
        assertThat(APCI.INDIVIDUAL_ADDRESS_RESPONSE.getFriendlyName()).isEqualTo("Individual Address Response");
    }

    @Test
    @Override
    public void testToString() {
        // without code range
        assertThat(APCI.GROUP_VALUE_READ).hasToString("APCI{name=GROUP_VALUE_READ, friendlyName=Group Value Read, code=0 (0x00)}");

        // with code range
        assertThat(APCI.GROUP_VALUE_RESPONSE)
                .hasToString("APCI{name=GROUP_VALUE_RESPONSE, friendlyName=Group Value Response, code=64..127 (0x40..0x7F)}");
    }

    /**
     * Tests the ranges
     *
     * <pre>
     * GROUP_VALUE_RESPONSE        Range: 0x40 .. 0x7F
     * GROUP_VALUE_WRITE 0x80      Range: 0x80 .. 0xBF
     * INDIVIDUAL_ADDRESS_WRITE    Range: 0xC0
     * </pre>
     */
    @Test
    public void validRangeTest() {
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
