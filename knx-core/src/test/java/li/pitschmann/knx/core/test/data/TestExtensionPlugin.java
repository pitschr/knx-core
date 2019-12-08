package li.pitschmann.knx.core.test.data;

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.ExtensionPlugin;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test {@link ExtensionPlugin} implementation
 */
public final class TestExtensionPlugin implements ExtensionPlugin {
    private final AtomicInteger initInvocations = new AtomicInteger();
    private final AtomicInteger startInvocations = new AtomicInteger();
    private final AtomicInteger shutdownInvocations = new AtomicInteger();
    private KnxClient knxClient;

    @Override
    public void onInitialization(final KnxClient client) {
        initInvocations.incrementAndGet();
        knxClient = client;
    }

    @Override
    public void onStart() {
        startInvocations.incrementAndGet();
    }

    @Override
    public void onShutdown() {
        shutdownInvocations.incrementAndGet();
    }

    public int getInitInvocations() {
        return initInvocations.get();
    }

    public int getStartInvocations() {
        return startInvocations.get();
    }

    public int getShutdownInvocations() {
        return shutdownInvocations.get();
    }

    public KnxClient getKnxClient() {
        return knxClient;
    }
}
