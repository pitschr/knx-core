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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Bytes;
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
     * Tests {@link KnxAddressesDIB#of(byte[])}
     */
    @Test
    public void validCases() {
        // create by bytes
        final var dib = KnxAddressesDIB.of(BYTES);

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
        assertThatThrownBy(() -> KnxAddressesDIB.of(new byte[]{0x05, 0x00, 0x00, 0x00, 0x00})).isInstanceOf(KnxIllegalArgumentException.class)
                .hasMessageContaining("divisible by two");

        // incorrect size of bytes
        assertThatThrownBy(() -> KnxAddressesDIB.of(new byte[]{0x03, 0x02, 0x01})).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("rawData");
        assertThatThrownBy(() -> KnxAddressesDIB.of(Bytes.padRight(new byte[]{(byte) 0xFF}, (byte) 0x00, 255)))
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

        assertThat(KnxAddressesDIB.of(BYTES)).hasToString(
                String.format("KnxAddressesDIB{length=10, descriptionType=KNX_ADDRESSES, knxAddress=%s, additionalAddresses=%s}",
                        knxAddress, additionalAddresses)
        );
    }
}
