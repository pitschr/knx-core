package li.pitschmann.knx.test.data;

import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.Plugin;

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
