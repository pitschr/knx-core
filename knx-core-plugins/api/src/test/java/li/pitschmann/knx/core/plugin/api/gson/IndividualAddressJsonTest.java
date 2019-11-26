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

package li.pitschmann.knx.core.plugin.api.gson;

import com.google.gson.JsonParser;
import li.pitschmann.knx.core.body.address.IndividualAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link IndividualAddressJsonSerializer} and {@link IndividualAddressJsonDeserializer}
 */
public class IndividualAddressJsonTest {

    /**
     * Test serialization of {@link IndividualAddress} to a string representation
     */
    @Test
    @DisplayName("Serialize IndividualAddress")
    public void testSerialize() {
        final var address = IndividualAddress.of(13, 4, 56);

        // serialize to JSON String
        final var json = IndividualAddressJsonSerializer.INSTANCE.serialize(address, null, null).toString();

        // @formatter:off
        assertThat(json).isEqualTo(
                "{" +
                    "\"type\":0," +
                    "\"format\":\"13.4.56\"," +
                    "\"raw\":[-44,56]" +
                "}");
        // @formatter:on
    }

    /**
     * Test deserialization of couple of accepted JSON strings to {@link IndividualAddress}
     */
    @Test
    @DisplayName("De-Serialize IndividualAddress")
    public void testDeserialize() {
        final var expectedAddress = IndividualAddress.of(12, 3, 45);

        // given JSON strings
        final var jsons = new String[]{
                // @formatter:off
                // #1 - complete format
                "{" +
                    "\"type\":0," +
                    "\"format\":\"12.3.45\"," +
                    "\"raw\":[-61,45]" +
                "}",
                // #2 - only type and raw
                "{" +
                    "\"type\":0," +
                    "\"raw\":[-61,45]" +
                "}",
                // #3 - only byte array
                "[-61,45]",
                // #4 - only address (as string)
                "\"12.3.45\""
                // @formatter:off
        };

        // verify
        final var jsonParser = new JsonParser();
        for (final var json : jsons) {
            final var actualAddress = IndividualAddressJsonDeserializer.INSTANCE.deserialize(jsonParser.parse(json), null, null);
            assertThat(actualAddress).isEqualTo(expectedAddress);
        }
    }
}
