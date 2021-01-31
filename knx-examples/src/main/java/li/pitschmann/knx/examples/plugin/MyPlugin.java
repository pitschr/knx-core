package li.pitschmann.knx.examples.plugin;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.ExtensionPlugin;
import li.pitschmann.knx.core.plugin.ObserverPlugin;

/**
 * Demo implementation for monitor plugin
 */
public class MyPlugin implements ObserverPlugin, ExtensionPlugin {

    @Override
    public void onInitialization(final KnxClient client) {
        System.out.println("Initialized by client: " + client);
    }

    @Override
    public void onIncomingBody(final Body item) {
        System.out.println("Incoming: " + item);
    }

    @Override
    public void onOutgoingBody(final Body item) {
        System.out.println("Outgoing: " + item);
    }

    @Override
    public void onError(final Throwable throwable) {
        System.out.println("On Error: " + throwable.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Start signal received");
    }

    @Override
    public void onShutdown() {
        System.out.println("Stop signal received");
    }

}