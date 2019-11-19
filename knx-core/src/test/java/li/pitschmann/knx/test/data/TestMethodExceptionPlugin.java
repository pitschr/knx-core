package li.pitschmann.knx.test.data;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.Plugin;

import javax.annotation.Nonnull;

/**
 * Test {@link Plugin} implementation that is throwing
 * an {@link RuntimeException} in all methods.
 */
public final class TestMethodExceptionPlugin implements ObserverPlugin, ExtensionPlugin {
    @Override
    public void onInitialization(final @Nonnull KnxClient client) {
        throw new RuntimeException("Exception in onInitialization(..) method");
    }

    @Override
    public void onStart() {
        throw new RuntimeException("Exception in onStart() method");
    }

    @Override
    public void onIncomingBody(@Nonnull Body item) {
        throw new RuntimeException("Exception in onIncomingBody(..) method");
    }

    @Override
    public void onOutgoingBody(@Nonnull Body item) {
        throw new RuntimeException("Exception in onOutgoingBody(..) method");
    }

    @Override
    public void onError(@Nonnull Throwable throwable) {
        throw new RuntimeException("Exception in onError(..) method");
    }

    @Override
    public void onShutdown() {
        throw new RuntimeException("Exception in onShutdown() method");
    }
}
