package li.pitschmann.knx.examples.load_from_configfile;

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.Plugin;


/**
 * Test {@link Plugin} implementation
 */
public final class TestPlugin implements Plugin {
    @Override
    public void onInitialization(final KnxClient client) {
        // NO-OP
    }
}
