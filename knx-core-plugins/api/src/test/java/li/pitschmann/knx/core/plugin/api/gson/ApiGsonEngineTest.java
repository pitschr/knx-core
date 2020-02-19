package li.pitschmann.knx.core.plugin.api.gson;

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
        final var instant = LocalDateTime.of(2020, 1,2, 3, 4, 5, 6)
                .toInstant(ZoneOffset.UTC);

        final var instantJson = ApiGsonEngine.INSTANCE.toString(instant);
        assertThat(instantJson).isEqualTo("{\"seconds\":1577934245,\"nanos\":6}");
    }

    @Test
    @DisplayName("Test conversion of JSON representation to Instant object")
    public void testFromJson() {
        final var instantJson = "{\"seconds\":1580702706,\"nanos\":7}";

        final var instant = ApiGsonEngine.INSTANCE.fromString(instantJson, Instant.class);
        assertThat(instant).isEqualTo(
                LocalDateTime.of(2020, 2, 3, 4, 5, 6, 7)
                        .toInstant(ZoneOffset.UTC)
        );
    }
}
