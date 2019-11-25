package li.pitschmann.knx.core.test.data;

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.Plugin;

import javax.annotation.Nonnull;

/**
 * Test {@link Plugin} implementation
 */
public final class TestPlugin implements Plugin {
    @Override
    public void onInitialization(final @Nonnull KnxClient client) {
        // NO-OP
    }
}
