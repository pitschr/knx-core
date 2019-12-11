package li.pitschmann.knx.core.plugin.api.v1.json;

import li.pitschmann.knx.core.address.GroupAddress;

/**
 * JSON for HTTP Status Request
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
