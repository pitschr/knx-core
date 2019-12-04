package li.pitschmann.knx.core.test.data;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.ObserverPlugin;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test {@link ObserverPlugin} implementation
 */
public final class TestObserverPlugin implements ObserverPlugin {
    private final AtomicInteger initInvocations = new AtomicInteger();
    private final List<Body> incomingBodies = new LinkedList<>();
    private final List<Body> outgoingBodies = new LinkedList<>();
    private final List<Throwable> errors = new LinkedList<>();

    @Override
    public void onInitialization(final KnxClient client) {
        initInvocations.incrementAndGet();
    }

    @Override
    public void onIncomingBody(final Body item) {
        incomingBodies.add(item);
    }

    @Override
    public void onOutgoingBody(final Body item) {
        outgoingBodies.add(item);
    }

    @Override
    public void onError(final Throwable throwable) {
        errors.add(throwable);
    }

    public int getInitInvocations() {
        return initInvocations.get();
    }

    public List<Body> getIncomingBodies() {
        return Collections.unmodifiableList(incomingBodies);
    }

    public List<Body> getOutgoingBodies() {
        return Collections.unmodifiableList(outgoingBodies);
    }

    public List<Throwable> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
