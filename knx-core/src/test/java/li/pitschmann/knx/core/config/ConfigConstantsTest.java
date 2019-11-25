package li.pitschmann.knx.core.config;

import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testing for {@link ConfigConstantsTest} instance
 */
public class ConfigConstantsTest {
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(CoreConfigs.class);
    }
}
