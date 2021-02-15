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

package li.pitschmann.knx.core.dib;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link ManufacturerDataDIB}
 *
 * @author PITSCHR
 */
final class ManufacturerDataDIBTest {

    @Test
    @DisplayName("Test #of(byte[]) with data")
    void testOf_Bytes_With_Data() {
        final var bytes = new byte[]{ //
                0x13,                                           // Structure Length
                (byte) 0xFE,                                    // Description Type Code
                0x21, 0x22,                                     // KNX Manufacturer ID
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, // Data
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47        // Data (continued)
        };
        final var dib = ManufacturerDataDIB.of(bytes);

        // compare
        assertThat(dib.getId()).isEqualTo(8482);
        assertThat(dib.getData()).containsExactly( //
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, //
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47);
        assertThat(dib.toByteArray()).containsExactly(bytes);
        assertThat(dib).hasToString(
                "ManufacturerDataDIB{id=8482, data=0x31 32 33 34 35 36 37 38 41 42 43 44 45 46 47}"
        );
    }

    @Test
    @DisplayName("Test #of(byte[]) without data")
    void testOf_Bytes_Without_Data() {
        final var bytes = new byte[]{ //
                0x04,        // Structure Length
                (byte) 0xFE, // Description Type Code
                0x51, 0x52   // KNX Manufacturer ID
        };

        final var dib = ManufacturerDataDIB.of(bytes);

        // compare
        assertThat(dib.getId()).isEqualTo(20818);
        assertThat(dib.getData()).isEmpty();
        assertThat(dib.toByteArray()).containsExactly(bytes);
        assertThat(dib).hasToString(
                "ManufacturerDataDIB{id=20818, data=}"
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidCases_of_Bytes() {
        // null
        assertThatThrownBy(() -> ManufacturerDataDIB.of(null))
                .isInstanceOf(NullPointerException.class);

        // incorrect size of bytes
        assertThatThrownBy(() -> ManufacturerDataDIB.of(new byte[]{0x03, 0x02, 0x01}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [4..255] but was: 3");
        assertThatThrownBy(() -> ManufacturerDataDIB.of(new byte[256]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible structure length. Expected [4..255] but was: 256");

        // incorrect description type on index 1
        final var bytesInvalidDescriptionType = new byte[4];
        bytesInvalidDescriptionType[0] = 0x04; // correct
        bytesInvalidDescriptionType[1] = DescriptionType.UNKNOWN.getCodeAsByte(); // not correct
        assertThatThrownBy(() -> ManufacturerDataDIB.of(bytesInvalidDescriptionType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible value for bytes[1]. Expected '-2' but was: -1");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ManufacturerDataDIB.class).verify();
    }

}
