package li.pitschmann.knx.test.data;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.ObserverPlugin;

import javax.annotation.Nonnull;

/**
 * Test {@link ObserverPlugin} implementation
 */
public final class TestObserverPlugin implements ObserverPlugin {
    @Override
    public void onInitialization(final @Nonnull KnxClient client) {
        // NO-OP
    }

    @Override
    public void onIncomingBody(@Nonnull Body item) {
        // NO-OP
    }

    @Override
    public void onOutgoingBody(@Nonnull Body item) {
        // NO-OP
    }

    @Override
    public void onError(@Nonnull Throwable throwable) {
        // NO-OP
    }
}
