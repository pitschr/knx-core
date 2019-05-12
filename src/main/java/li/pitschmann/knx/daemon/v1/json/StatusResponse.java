package li.pitschmann.knx.daemon.v1.json;

import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.body.cemi.APCI;

import java.time.Instant;

/**
 * JSON status response
 */
public class StatusResponse extends ReadResponse {
    private Instant timestamp;
    private KnxAddress sourceAddress;
    private APCI apci;
    private Boolean dirty;

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public KnxAddress getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(KnxAddress sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public APCI getApci() {
        return apci;
    }

    public void setApci(APCI apci) {
        this.apci = apci;
    }

    public Boolean isDirty() {
        return dirty;
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }
}
