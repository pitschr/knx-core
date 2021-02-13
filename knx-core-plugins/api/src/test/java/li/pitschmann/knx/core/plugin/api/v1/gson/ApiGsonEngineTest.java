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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ApiGsonEngine}
 */
public class ApiGsonEngineTest {

    @Test
    @DisplayName("Test conversion of Instant object to a JSON representation")
    public void testToJson() {
        final var instant = LocalDateTime.of(2020, 1, 2, 3, 4, 5, 6)
                .toInstant(ZoneOffset.UTC);

        final var instantJson = ApiGsonEngine.INSTANCE.getGson().toJson(instant);
        assertThat(instantJson).isEqualTo("{\"seconds\":1577934245,\"nanos\":6}");
    }

    @Test
    @DisplayName("Test conversion of JSON representation to Instant object")
    public void testFromJson() {
        final var instantJson = "{\"seconds\":1580702706,\"nanos\":7}";

        final var instant = ApiGsonEngine.INSTANCE.getGson().fromJson(instantJson, Instant.class);
        assertThat(instant).isEqualTo(
                LocalDateTime.of(2020, 2, 3, 4, 5, 6, 7)
                        .toInstant(ZoneOffset.UTC)
        );
    }
}
