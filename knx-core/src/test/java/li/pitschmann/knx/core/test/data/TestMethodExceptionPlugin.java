package li.pitschmann.knx.core.test.data;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.ExtensionPlugin;
import li.pitschmann.knx.core.plugin.ObserverPlugin;
import li.pitschmann.knx.core.plugin.Plugin;


/**
 * Test {@link Plugin} implementation that is throwing
 * an {@link RuntimeException} in all methods.
 */
public final class TestMethodExceptionPlugin implements ObserverPlugin, ExtensionPlugin {
    @Override
    public void onInitialization(final KnxClient client) {
        throw new RuntimeException("Exception in onInitialization(..) method");
    }

    @Override
    public void onStart() {
        throw new RuntimeException("Exception in onStart() method");
    }

    @Override
    public void onIncomingBody(final Body item) {
        throw new RuntimeException("Exception in onIncomingBody(..) method");
    }

    @Override
    public void onOutgoingBody(final Body item) {
        throw new RuntimeException("Exception in onOutgoingBody(..) method");
    }

    @Override
    public void onError(final Throwable throwable) {
        throw new RuntimeException("Exception in onError(..) method");
    }

    @Override
    public void onShutdown() {
        throw new RuntimeException("Exception in onShutdown() method");
    }
}
