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

package li.pitschmann.knx.link.body.dib;

import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link ManufacturerDataDIB}
 *
 * @author PITSCHR
 */
public final class ManufacturerDataDIBTest {
    private static final byte[] BYTES_WITH_DATA = new byte[]{ //
            0x13, // Structure Length
            (byte) 0xFE, // Description Type Code
            0x21, 0x22, // KNX Manufacturer ID
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, // Data
            0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47 // Data (continued)
    };
    private static final byte[] BYTES_WITHOUT_DATA = new byte[]{ //
            0x04, // Structure Length
            (byte) 0xFE, // Description Type Code
            0x51, 0x52 // KNX Manufacturer ID
    };

    /**
     * Tests {@link ManufacturerDataDIB#valueOf(byte[])} with data
     */
    @Test
    public void valueOfWithData() {
        // valueOf
        final var dib = ManufacturerDataDIB.valueOf(BYTES_WITH_DATA);

        // compare
        assertThat(dib.getLength()).isEqualTo(19);
        assertThat(dib.getDescriptionType()).isEqualTo(DescriptionType.MANUFACTURER_DATA);
        assertThat(dib.getManufacturerId()).isEqualTo(8482);
        assertThat(dib.getManufacturerSpecificData()).hasSize(15);
        assertThat(dib.getManufacturerSpecificData()).containsExactly( //
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, //
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47);

    }

    /**
     * Tests {@link ManufacturerDataDIB#valueOf(byte[])} without data
     */
    @Test
    public void valueOfWithoutData() {
        // valueOf
        final var dib = ManufacturerDataDIB.valueOf(BYTES_WITHOUT_DATA);

        // compare
        assertThat(dib.getLength()).isEqualTo(4);
        assertThat(dib.getDescriptionType()).isEqualTo(DescriptionType.MANUFACTURER_DATA);
        assertThat(dib.getManufacturerId()).isEqualTo(20818);
        assertThat(dib.getManufacturerSpecificData()).isEmpty();
    }

    /**
     * Tests {@link ManufacturerDataDIB} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // incorrect size of bytes
        assertThatThrownBy(() -> ManufacturerDataDIB.valueOf(new byte[]{0x03, 0x02, 0x01})).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> ManufacturerDataDIB.valueOf(new byte[256])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
    }

    /**
     * Test {@link ManufacturerDataDIB#toString()}
     */
    @Test
    public void testToString() {
        assertThat(ManufacturerDataDIB.valueOf(BYTES_WITH_DATA))
                .hasToString(String.format("ManufacturerDataDIB{length=19 (0x13), descriptionType=%s, rawData=%s}",
                        DescriptionType.MANUFACTURER_DATA, ByteFormatter.formatHexAsString(BYTES_WITH_DATA)));
    }
}
