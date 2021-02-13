/*
 * KNX Link - A library for KNX Net/IP communication
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

package li.pitschmann.knx.core.plugin.api.v1.gson;

import com.google.gson.JsonParser;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.address.IndividualAddress;
import li.pitschmann.knx.core.address.KnxAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link KnxAddressJsonDeserializer}
 */
class KnxAddressJsonTest {
    /**
     * Test deserialization of an instance of {@link KnxAddress}
     */
    @Test
    @DisplayName("De-Serialize KnxAddress")
    void testDeserialize() {
        // given JSON strings
        final var individualAddressJson = "{\"type\":0,\"raw\":[-58,89]}"; // 12.6.89
        final var groupAddressJson = "{\"type\":1,\"raw\":[117,67]}"; // 14/5/67

        // verify
        final var individualAddress = KnxAddressJsonDeserializer.INSTANCE.deserialize(JsonParser.parseString(individualAddressJson), null, null);
        final var expectedIndividualAddress = IndividualAddress.of(12, 6, 89);
        assertThat(individualAddress).isEqualTo(expectedIndividualAddress);

        final var groupAddress = KnxAddressJsonDeserializer.INSTANCE.deserialize(JsonParser.parseString(groupAddressJson), null, null);
        final var expectedGroupAddress = GroupAddress.of(14, 5, 67);
        assertThat(groupAddress).isEqualTo(expectedGroupAddress);
    }

    /**
     * Test deserialization of an wrong JSON format
     */
    @Test
    @DisplayName("De-Serialize KnxAddress of wrong int type")
    void testInvalidDeserialize() {
        // given JSON strings
        final var wrongJson = "{\"type\":2,\"raw\":[0,0]}"; // 12.6.89

        // verify
        assertThatThrownBy(() -> KnxAddressJsonDeserializer.INSTANCE.deserialize(JsonParser.parseString(wrongJson), null, null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Given JSON type is not supported: " + wrongJson);
    }
}
