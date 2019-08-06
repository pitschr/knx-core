package li.pitschmann.knx.daemon.v1.json;

import li.pitschmann.knx.link.body.address.GroupAddress;

/**
 * JSON for HTTP Status Request to KNX Daemon
 */
public final class StatusRequest {
    private GroupAddress groupAddress;

    public GroupAddress getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(final GroupAddress groupAddress) {
        this.groupAddress = groupAddress;
    }
}
