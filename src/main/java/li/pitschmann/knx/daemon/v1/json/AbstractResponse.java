package li.pitschmann.knx.daemon.v1.json;

/**
 * Abstract Response with status for all JSON responses
 */
public abstract class AbstractResponse {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
