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

import com.google.gson.JsonPrimitive;
import li.pitschmann.knx.core.datapoint.DPT16;
import li.pitschmann.knx.core.datapoint.DPT18;
import li.pitschmann.knx.core.datapoint.DPT3;
import li.pitschmann.knx.core.datapoint.DPT5;
import li.pitschmann.knx.core.datapoint.DataPointType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link DataPointTypeJsonSerializer} and {@link DataPointTypeJsonDeserializer}
 */
public class DataPointTypeJsonTest {

    /**
     * Test serialization of {@link DataPointType} to a string representation
     */
    @Test
    @DisplayName("Serialize DataPointType")
    public void testSerialize() {
        // serialize to JSON String
        final var dpt5Json = DataPointTypeJsonSerializer.INSTANCE.serialize(DPT5.VALUE_1_OCTET_UNSIGNED_COUNT, null, null).toString();
        final var dpt16Json = DataPointTypeJsonSerializer.INSTANCE.serialize(DPT16.ISO_8859_1, null, null).toString();

        // verify
        assertThat(dpt5Json).isEqualTo("\"5.010\"");
        assertThat(dpt16Json).isEqualTo("\"16.001\"");
    }

    /**
     * Test deserialization of couple of accepted JSON strings to {@link DataPointType}
     */
    @Test
    @DisplayName("De-Serialize DataPointType")
    public void testDeserialize() {
        // DPT3 Test
        final var dpt3JsonObject = new JsonPrimitive("3.008");
        final DataPointType dpt3 = DataPointTypeJsonDeserializer.INSTANCE.deserialize(dpt3JsonObject, null, null);
        assertThat(dpt3).isEqualTo(DPT3.CONTROL_BLINDS);

        // DPT18 Test
        final var dpt18JsonObject = new JsonPrimitive("18.001");
        final DataPointType dpt18 = DataPointTypeJsonDeserializer.INSTANCE.deserialize(dpt18JsonObject, null, null);
        assertThat(dpt18).isEqualTo(DPT18.SCENE_CONTROL);
    }
}
