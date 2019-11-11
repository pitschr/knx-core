package li.pitschmann.knx.test.data;

import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;

import javax.annotation.Nonnull;

/**
 * Test {@link ExtensionPlugin} implementation
 */
public final class TestExtensionPlugin implements ExtensionPlugin {
    @Override
    public void onInitialization(final @Nonnull KnxClient client) {
        // NO-OP
    }

    @Override
    public void onStart() {
        // NO-OP
    }

    @Override
    public void onShutdown() {
        // NO-OP
    }
}
