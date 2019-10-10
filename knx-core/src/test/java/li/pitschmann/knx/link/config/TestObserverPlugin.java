package li.pitschmann.knx.link.config;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.ObserverPlugin;

import javax.annotation.Nonnull;

public class TestObserverPlugin implements ObserverPlugin {
    @Override
    public void onInitialization(KnxClient client) {

    }

    @Override
    public void onIncomingBody(@Nonnull Body item) {

    }

    @Override
    public void onOutgoingBody(@Nonnull Body item) {

    }

    @Override
    public void onError(@Nonnull Throwable throwable) {

    }
}
