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
 * Test case for {@link IPConfigDIB}
 *
 * @author PITSCHR
 */
public final class IPConfigDIBTest {
    private static final byte[] BYTES = new byte[]{ //
            0x10, // Structure Length
            0x03, // Description Type Code
            0x11, 0x22, 0x33, 0x44, // IP Address
            0x55, 0x66, 0x77, (byte) 0x88, // Subnet Mask
            (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // Default Gateway
            (byte) 0xDD, // IP Capabilities
            (byte) 0xEE // IP assignment method
    };

    /**
     * Tests {@link IPConfigDIB#valueOf(byte[])}
     */
    @Test
    public void valueOf() {
        // valueOf
        final var dib = IPConfigDIB.valueOf(BYTES);

        // compare
        assertThat(dib.getLength()).isEqualTo(16);
        assertThat(dib.getDescriptionType()).isEqualTo(DescriptionType.IP_CONFIG);
    }

    /**
     * Tests {@link IPConfigDIB} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // incorrect size of bytes
        assertThatThrownBy(() -> IPConfigDIB.valueOf(new byte[]{0x03, 0x02, 0x01})).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");

    }

    /**
     * Test {@link IPConfigDIB#toString()}
     */
    @Test
    public void testToString() {
        assertThat(IPConfigDIB.valueOf(BYTES)).hasToString(String.format("IPConfigDIB{length=16 (0x10), descriptionType=%s, rawData=%s}",
                DescriptionType.IP_CONFIG, ByteFormatter.formatHexAsString(BYTES)));
    }
}
