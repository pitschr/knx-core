package li.pitschmann.knx.test.data;

import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.config.PluginConfigValue;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test {@link ExtensionPlugin} implementation
 */
public final class TestExtensionPlugin implements ExtensionPlugin {
    private final AtomicInteger initInvocations = new AtomicInteger();
    private final AtomicInteger startInvocations = new AtomicInteger();
    private final AtomicInteger shutdownInvocations = new AtomicInteger();

    @Override
    public List<PluginConfigValue<?>> getConfigValues() {
        return List.of();
    }

    @Override
    public void onInitialization(KnxClient client) {
        initInvocations.incrementAndGet();
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
}
