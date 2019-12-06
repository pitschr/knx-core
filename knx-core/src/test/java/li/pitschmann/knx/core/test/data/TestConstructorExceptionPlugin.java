package li.pitschmann.knx.core.test.data;

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.Plugin;


/**
 * Test {@link Plugin} implementation that is throwing
 * an {@link RuntimeException} in constructor.
 */
public final class TestConstructorExceptionPlugin implements Plugin {
    public TestConstructorExceptionPlugin() {
        throw new RuntimeException("Exception in constructor");
    }

    @Override
    public void onInitialization(final KnxClient client) {
        // NO-OP
    }
}
