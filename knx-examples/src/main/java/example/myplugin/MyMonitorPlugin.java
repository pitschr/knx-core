package example.myplugin;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;

public class MyMonitorPlugin implements ObserverPlugin, ExtensionPlugin {

    @Override
    public void onInitialization(final KnxClient client) {
        System.out.println("Initialized by client: " + client);
    }

    @Override
    public void onIncomingBody(final Body item) {
        System.out.println("Incoming: " + item.getServiceType().getFriendlyName() + " (" + item.getRawDataAsHexString() + ")");
    }

    @Override
    public void onOutgoingBody(final Body item) {
        System.out.println("Outgoing: " + item.getServiceType().getFriendlyName() + " (" + item.getRawDataAsHexString() + ")");
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