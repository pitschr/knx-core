package li.pitschmann.knx.daemon.v1.controllers;

import li.pitschmann.knx.daemon.v1.json.ProjectStructureRequest;
import li.pitschmann.knx.parser.XmlGroupAddress;
import li.pitschmann.knx.parser.XmlGroupAddressStyle;
import li.pitschmann.knx.parser.XmlGroupRange;
import li.pitschmann.utils.Preconditions;
import ro.pippo.controller.GET;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Param;

import java.util.List;

/**
 * Controller for project-specific endpoints to return some data
 * from KNX Project file
 */
public final class ProjectController extends AbstractController {

    /**
     * Returns the project structure containing metadata from *.knxproj file
     *
     * @return response of project structure
     */
    @GET("/project")
    @Produces(Produces.JSON)
    public ProjectStructureRequest projectStructure() {
        log.trace("Request for project overview");

        final var xmlProject = getXmlProject();

        // get project data
        final var response = new ProjectStructureRequest();
        if (containsExpand("id")) {
            response.setId(xmlProject.getId());
        }
        if (containsExpand("name")) {
            response.setName(xmlProject.getName());
        }
        if (containsExpand("groupAddressStyle")) {
            response.setGroupAddressStyle(xmlProject.getGroupAddressStyle().getCode());
        }
        if (containsExpand("numberOfGroupRanges")) {
            response.setNumberOfGroupRanges(xmlProject.getGroupRanges().size());
        }
        if (containsExpand("numberOfGroupAddresses")) {
            response.setNumberOfGroupAddresses(xmlProject.getGroupAddresses().size());
        }

        getResponse().ok();
        return response;
    }

    /**
     * Returns the main  group for a two-level project ({@code main}/{@code sub})
     * or a three-level project ({@code main}/{@code middle}/{@code sub})
     * <p/>
     * Not supported for: free-level projects
     *
     * @return list of {@link XmlGroupRange} on root level
     */
    @GET("/project/groups")
    @Produces(Produces.JSON)
    public List<XmlGroupRange> mainGroups() {
        log.trace("Request for all main group in project");

        final var xmlProject = getXmlProject();
        final var groupAddressStyle = xmlProject.getGroupAddressStyle();
        Preconditions.checkArgument(
                groupAddressStyle == XmlGroupAddressStyle.THREE_LEVEL ||
                        groupAddressStyle == XmlGroupAddressStyle.TWO_LEVEL);

        final var mainGroups = xmlProject.getMainGroups();
        log.debug("Request for get '{}' found: {}", getRequest().getPath(), mainGroups);

        getResponse().ok();
        return limitAndGetAsList(mainGroups);
    }

    /**
     * Returns the middle groups for a three-level project ({@code main}/{@code middle}/{@code sub})
     * <ul>
     * <li>Main: 0 .. 31</li>
     * </ul>
     * Not supported for: two-level and free-level projects
     *
     * @return list of {@link XmlGroupRange} for given {@code main} range
     */
    @GET("/project/groups/{main: \\d+}")
    @Produces(Produces.JSON)
    public List<XmlGroupRange> getGroups(@Param int main) {
        log.trace("Request for main group in project: {}", main);
        checkArgumentMainGroup(main);

        final var xmlProject = getXmlProject();
        Preconditions.checkArgument(xmlProject.getGroupAddressStyle() == XmlGroupAddressStyle.THREE_LEVEL);

        final var mainGroup = xmlProject.getMainGroup(main);
        log.debug("Request for get '{}' found: {}", getRequest().getPath(), mainGroup);

        getResponse().ok();
        return limitAndGetAsList(mainGroup.getChildGroupRanges());
    }

    /**
     * Returns the group addresses for a two-level project ({@code main}/{@code sub})
     * <ul>
     * <li>Main: 0 .. 31</li>
     * </ul>
     * Not supported for: three-level and free-level projects
     *
     * @return list of {@link XmlGroupAddress} for given {@code main} range
     */
    @GET("/project/addresses/{main: \\d+}")
    @Produces(Produces.JSON)
    public List<XmlGroupAddress> getAddresses(@Param int main) {
        log.trace("Request for middle group of main group '{}' in project: {}", main);
        checkArgumentMainGroup(main);

        final var xmlProject = getXmlProject();
        Preconditions.checkArgument(xmlProject.getGroupAddressStyle() == XmlGroupAddressStyle.TWO_LEVEL);

        final var middleGroup = xmlProject.getMainGroup(main);
        log.debug("Request for get '{}' found: {}", getRequest().getPath(), middleGroup);

        getResponse().ok();
        return limitAndGetAsList(middleGroup.getGroupAddresses());
    }

    /**
     * Returns the group addresses for a three-level project ({@code main}/{@code middle}/{@code sub})
     * <ul>
     * <li>Main: 0 .. 31</li>
     * <li>Middle: 0 .. 7</li>
     * </ul>
     * Not supported for: two-level and free-level projects
     *
     * @return list of {@link XmlGroupAddress} for given {@code main} and {@code middle} ranges
     */
    @GET("/project/addresses/{main: \\d+}/{middle: \\d+}")
    @Produces(Produces.JSON)
    public List<XmlGroupAddress> getAddresses(@Param int main, @Param int middle) {
        log.trace("Request for middle group of main group '{}' in project: {}", main);
        checkArgumentMainGroup(main);
        checkArgumentMiddleGroup(middle);

        final var xmlProject = getXmlProject();
        Preconditions.checkArgument(xmlProject.getGroupAddressStyle() == XmlGroupAddressStyle.THREE_LEVEL);

        final var middleGroup = xmlProject.getMiddleGroup(main, middle);
        log.debug("Request for get '{}' found: {}", getRequest().getPath(), middleGroup);

        getResponse().ok();
        return limitAndGetAsList(middleGroup.getGroupAddresses());
    }

    private void checkArgumentMainGroup(final int main) {
        Preconditions.checkArgument(main >= 0 && main <= 31,
                "Invalid number of main group provided, should be within range [0-31]: {}", main);
    }

    private void checkArgumentMiddleGroup(final int middle) {
        Preconditions.checkArgument(middle >= 0 && middle <= 7,
                "Invalid number of middle group provided, should be within range [0-7]: {}", middle);
    }
}
