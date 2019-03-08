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

import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Bytes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link KnxAddressesDIB}
 *
 * @author PITSCHR
 */
public final class KnxAddressesDIBTest {
    private static final byte[] BYTES = new byte[]{ //
            0x0A, // Structure Length
            0x05, // Description Type Code
            0x00, 0x00, // KNX Individual Address (0.0.0)
            0x12, 0x03, // Additional Individual Address 1 (1.2.3)
            0x78, 0x7F, // Additional Individual Address 2 (7.8.127)
            (byte) 0xFF, (byte) 0xFF, // Additional Individual Address 3 (15.15.255)
    };

    /**
     * Tests {@link KnxAddressesDIB#valueOf(byte[])}
     */
    @Test
    public void valueOf() {
        // valueOf
        final var dib = KnxAddressesDIB.valueOf(BYTES);

        // compare
        assertThat(dib.getLength()).isEqualTo(10);
        assertThat(dib.getDescriptionType()).isEqualTo(DescriptionType.KNX_ADDRESSES);
        assertThat(dib.getKnxAddress()).isNotNull();
        assertThat(dib.getKnxAddress().getAddress()).isEqualTo("0.0.0");

        final var additionalAddresses = dib.getAdditionalAddresses();
        assertThat(additionalAddresses).hasSize(3);
        assertThat(additionalAddresses.get(0).getAddress()).isEqualTo("1.2.3");
        assertThat(additionalAddresses.get(1).getAddress()).isEqualTo("7.8.127");
        assertThat(additionalAddresses.get(2).getAddress()).isEqualTo("15.15.255");
    }

    /**
     * Tests {@link KnxAddressesDIB} with invalid arguments
     */
    @Test
    public void invalidCases() {
        // specific for KNX addresses DIB
        assertThatThrownBy(() -> KnxAddressesDIB.valueOf(new byte[]{0x05, 0x00, 0x00, 0x00, 0x00})).isInstanceOf(KnxIllegalArgumentException.class)
                .hasMessageContaining("divisible by two");

        // incorrect size of bytes
        assertThatThrownBy(() -> KnxAddressesDIB.valueOf(new byte[]{0x03, 0x02, 0x01})).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> KnxAddressesDIB.valueOf(Bytes.padRight(new byte[]{(byte) 0xFF}, (byte) 0x00, 255)))
                .isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("rawData");
    }

    /**
     * Test {@link KnxAddressesDIB#toString()}
     */
    @Test
    public void testToString() {
        final var knxAddress = IndividualAddress.of(new byte[]{0x00, 0x00});
        final var additionalAddresses = Arrays.asList(//
                IndividualAddress.of(1, 2, 3), //
                IndividualAddress.of(7, 8, 127), //
                IndividualAddress.of(15, 15, 255));

        assertThat(KnxAddressesDIB.valueOf(BYTES)).hasToString(
                String.format("KnxAddressesDIB{length=10 (0x0A), descriptionType=%s, knxAddress=%s, additionalAddresses=%s, rawData=%s}",
                        DescriptionType.KNX_ADDRESSES, knxAddress, additionalAddresses, ByteFormatter.formatHexAsString(BYTES)));
    }
}
