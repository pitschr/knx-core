package li.pitschmann.knx.daemon.v1.json;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.body.address.GroupAddress;

/**
 * JSON for HTTP Read Request to KNX Daemon
 */
public final class ReadRequest {
    private GroupAddress groupAddress;

    public GroupAddress getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(GroupAddress groupAddress) {
        this.groupAddress = groupAddress;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("groupAddress", this.groupAddress) //
                .toString();
    }
}
