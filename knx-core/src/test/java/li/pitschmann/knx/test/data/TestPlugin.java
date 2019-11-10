package li.pitschmann.knx.test.data;

import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.config.PluginConfigValue;
import li.pitschmann.knx.link.plugin.Plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test {@link Plugin} implementation
 */
public final class TestPlugin implements Plugin {
    private final AtomicInteger initInvocations = new AtomicInteger();

    @Override
    public List<PluginConfigValue<?>> getConfigValues() {
        return List.of();
    }

    @Override
    public void onInitialization(KnxClient client) {
        initInvocations.incrementAndGet();
    }

    public int getInitInvocations() {
        return initInvocations.get();
    }
}
