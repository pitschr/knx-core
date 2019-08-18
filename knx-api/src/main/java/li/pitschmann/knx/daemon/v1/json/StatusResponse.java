package li.pitschmann.knx.daemon.v1.json;

import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.body.cemi.APCI;

import java.time.Instant;

/**
 * JSON status response
 */
public final class StatusResponse extends ReadResponse {
    private Status status;
    private GroupAddress groupAddress;
    private Instant timestamp;
    private KnxAddress sourceAddress;
    private APCI apci;
    private Boolean dirty;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public GroupAddress getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(final GroupAddress groupAddress) {
        this.groupAddress = groupAddress;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Instant timestamp) {
        this.timestamp = timestamp;
    }

    public KnxAddress getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(final KnxAddress sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public APCI getApci() {
        return apci;
    }

    public void setApci(final APCI apci) {
        this.apci = apci;
    }

    public Boolean isDirty() {
        return dirty;
    }

    public void setDirty(final Boolean dirty) {
        this.dirty = dirty;
    }
}
