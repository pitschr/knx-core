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
import com.google.gson.JsonPrimitive;
import li.pitschmann.knx.core.address.GroupAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link GroupAddressJsonSerializer} and {@link GroupAddressJsonDeserializer}
 */
public class GroupAddressJsonTest {

    /**
     * Test serialization of {@link GroupAddress} to a string representation
     */
    @Test
    @DisplayName("Serialize GroupAddress")
    public void testSerialize() {
        final var address = GroupAddress.of(1, 7, 59);

        // serialize to JSON String
        final var json = GroupAddressJsonSerializer.INSTANCE.serialize(address, null, null).toString();

        // @formatter:off
        assertThat(json).isEqualTo(
                "{" +
                    "\"type\":1," +
                    "\"format\":{" +
                        "\"free_level\":\"3899\"," +
                        "\"two_level\":\"1/1851\"," +
                        "\"three_level\":\"1/7/59\"" +
                    "}," +
                    "\"raw\":[15,59]" +
                "}");
        // @formatter:on
    }

    /**
     * Test deserialization of couple of accepted JSON strings to {@link GroupAddress}
     */
    @Test
    @DisplayName("De-Serialize GroupAddress")
    public void testDeserialize() {
        final var expectedAddress = GroupAddress.of(1, 7, 59);

        // given JSON strings
        final var jsons = new String[]{
                // @formatter:off
                // #1 - complete format
                "{" +
                    "\"type\":1," +
                    "\"format\":{" +
                        "\"free_level\":\"3899\"," +
                        "\"two_level\":\"1/1851\"," +
                        "\"three_level\":\"1/7/59\"" +
                    "}," +
                    "\"raw\":[15,59]" +
                "}",
                // #2 - only type and raw
                "{" +
                    "\"type\":1," +
                    "\"raw\":[15,59]" +
                "}",
                // #3 - only byte array
                "[15,59]",
                // #4 - only address (as int)
                "3899",
                // #5 - only address (as string)
                "\"3899\"",
                // #6 - two-level address
                "\"1/1851\"",
                // #7 - three-level address
                "\"1/7/59\""
                // @formatter:off
        };

        // verify
        final var jsonParser = new JsonParser();
        for (final var json : jsons) {
            final var actualAddress = GroupAddressJsonDeserializer.INSTANCE.deserialize(jsonParser.parse(json), null, null);
            assertThat(actualAddress).isEqualTo(expectedAddress);
        }
    }

    /**
     * Test deserialization of an unsupported JSON format
     */
    @Test
    @DisplayName("De-Serialize GroupAddress with unsupported format")
    public void testInvalidDeserialize() {
        // given JSON boolean primitive
        final var booleanJson = new JsonPrimitive(true);

        // verify
        final var jsonParser = new JsonParser();
        assertThatThrownBy(() -> GroupAddressJsonDeserializer.INSTANCE.deserialize(booleanJson, null, null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Given JSON format is not supported: true");
    }
}
