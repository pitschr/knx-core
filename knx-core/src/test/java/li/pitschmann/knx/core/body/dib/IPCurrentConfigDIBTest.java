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

package li.pitschmann.knx.core.body.dib;

import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link IPCurrentConfigDIB}
 *
 * @author PITSCHR
 */
public final class IPCurrentConfigDIBTest {
    private static final byte[] BYTES = new byte[]{ //
            0x14, // Structure Length
            0x04, // Description Type Code
            0x11, 0x22, 0x33, 0x44, // Current IP Address
            0x55, 0x66, 0x77, (byte) 0x88, // Current Subnet Mask
            (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, // Current Default Gateway
            (byte) 0xD1, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, // DHCP Server
            (byte) 0xDD, // IP Capabilities
            (byte) 0x00 // (reserved)
    };

    /**
     * Tests {@link IPCurrentConfigDIB#of(byte[])}
     */
    @Test
    public void validCases() {
        // create by bytes
        final var dib = IPCurrentConfigDIB.of(BYTES);

        // compare
        assertThat(dib.getLength()).isEqualTo(20);
        assertThat(dib.getDescriptionType()).isEqualTo(DescriptionType.IP_CURRENT_CONFIG);
    }

    /**
     * Tests {@link IPCurrentConfigDIB} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // incorrect size of bytes
        assertThatThrownBy(() -> IPCurrentConfigDIB.of(new byte[]{0x03, 0x02, 0x01})).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");

    }

    /**
     * Test {@link IPCurrentConfigDIB#toString()}
     */
    @Test
    public void testToString() {
        assertThat(IPCurrentConfigDIB.of(BYTES))
                .hasToString(String.format("IPCurrentConfigDIB{length=20 (0x14), descriptionType=%s, rawData=%s}",
                        DescriptionType.IP_CURRENT_CONFIG, ByteFormatter.formatHexAsString(BYTES)));
    }
}
