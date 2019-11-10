package li.pitschmann.knx.test.data;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.config.PluginConfigValue;
import li.pitschmann.knx.link.plugin.ObserverPlugin;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test {@link ObserverPlugin} implementation
 */
public final class TestObserverPlugin implements ObserverPlugin {
    private final AtomicInteger initInvocations = new AtomicInteger();
    private final AtomicInteger ingoingInvocations = new AtomicInteger();
    private final AtomicInteger outgoingInvocations = new AtomicInteger();
    private final AtomicInteger errorInvocations = new AtomicInteger();

    @Override
    public List<PluginConfigValue<?>> getConfigValues() {
        return List.of();
    }

    @Override
    public void onInitialization(KnxClient client) {
        initInvocations.incrementAndGet();
    }

    @Override
    public void onIncomingBody(@Nonnull Body item) {
        ingoingInvocations.incrementAndGet();
    }

    @Override
    public void onOutgoingBody(@Nonnull Body item) {
        outgoingInvocations.incrementAndGet();
    }

    @Override
    public void onError(@Nonnull Throwable throwable) {
        errorInvocations.incrementAndGet();
    }

    public int getInitInvocations() {
        return initInvocations.get();
    }

    public int getIngoingInvocations() {
        return ingoingInvocations.get();
    }

    public int getOutgoingInvocations() {
        return outgoingInvocations.get();
    }

    public int getErrorInvocations() {
        return errorInvocations.get();
    }
}
