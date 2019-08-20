package li.pitschmann.knx.daemon.v1.json;

/**
 * JSON project overview response
 */
public final class ProjectOverviewResponse {
    private String id;
    private String name;
    private String groupAddressStyle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupAddressStyle() {
        return groupAddressStyle;
    }

    public void setGroupAddressStyle(String groupAddressStyle) {
        this.groupAddressStyle = groupAddressStyle;
    }
}
