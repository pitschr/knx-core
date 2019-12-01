package li.pitschmann.knx.core.plugin.api.v1.controllers;

import li.pitschmann.knx.core.knxproj.XmlGroupAddress;
import li.pitschmann.knx.core.knxproj.XmlGroupAddressStyle;
import li.pitschmann.knx.core.knxproj.XmlGroupRange;
import li.pitschmann.knx.core.plugin.api.v1.json.ProjectStructureRequest;
import li.pitschmann.knx.core.utils.Preconditions;
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
        response.setId(xmlProject.getId());
        response.setName(xmlProject.getName());
        response.setGroupAddressStyle(xmlProject.getGroupAddressStyle().getCode());
        response.setVersion(xmlProject.getVersion());
        response.setNumberOfGroupRanges(xmlProject.getGroupRanges().size());
        response.setNumberOfGroupAddresses(xmlProject.getGroupAddresses().size());

        getResponse().ok();
        return response;
    }

    /**
     * Returns the all group main ranges for a two-level project ({@code main}/{@code sub})
     * or a three-level project ({@code main}/{@code middle}/{@code sub})
     * <p/>
     * Not supported for: free-level projects
     *
     * @return list of {@link XmlGroupRange} on root level
     */
    @GET("/project/ranges")
    @Produces(Produces.JSON)
    public List<XmlGroupRange> getGroupRanges() {
        log.trace("Request for all main group ranges in project");

        final var xmlProject = getXmlProject();
        final var groupAddressStyle = xmlProject.getGroupAddressStyle();

        if (groupAddressStyle == XmlGroupAddressStyle.THREE_LEVEL ||
                groupAddressStyle == XmlGroupAddressStyle.TWO_LEVEL) {
            // two-level or three-level
            final var mainRanges = xmlProject.getMainGroupRanges();
            log.debug("All main group ranges found: {}", mainRanges);

            getResponse().ok();
            return limitAndGetAsList(mainRanges);
        } else {
            // not supported for free-level
            getResponse().forbidden();
            return List.of();
        }
    }

    /**
     * Returns the middle group ranges for a three-level project ({@code main}/{@code middle}/{@code sub})
     * <ul>
     * <li>Main: 0 .. 31</li>
     * </ul>
     * Not supported for: two-level and free-level projects
     *
     * @return list of {@link XmlGroupRange} for given {@code main} range
     */
    @GET("/project/ranges/{main: \\d+}")
    @Produces(Produces.JSON)
    public List<XmlGroupRange> getGroupRanges(@Param int main) {
        log.trace("Request for middle group ranges of main group range: {}", main);
        checkArgumentMainGroupRange(main);

        final var xmlProject = getXmlProject();
        if (xmlProject.getGroupAddressStyle() == XmlGroupAddressStyle.THREE_LEVEL) {
            // only three-level
            final var mainRange = xmlProject.getGroupRange(main);
            log.debug("Main group range '{}' found: {}", main, mainRange);

            getResponse().ok();
            return limitAndGetAsList(mainRange.getChildGroupRanges());
        } else {
            // not supported for free-level and two-level
            getResponse().forbidden();
            return List.of();
        }
    }

    /**
     * Returns all group addresses
     * <p/>
     * Supported for all projects: free-level, two-level and three-level
     *
     * @return list of {@link XmlGroupAddress} for given project
     */
    @GET("/project/addresses")
    @Produces(Produces.JSON)
    public List<XmlGroupAddress> getGroupAddresses() {
        log.trace("Request all group addresses");

        getResponse().ok();
        return limitAndGetAsList(getXmlProject().getGroupAddresses());
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
    public List<XmlGroupAddress> getGroupAddresses(@Param int main) {
        log.trace("Request addresses for main group range: {}", main);
        checkArgumentMainGroupRange(main);

        final var xmlProject = getXmlProject();
        if (xmlProject.getGroupAddressStyle() == XmlGroupAddressStyle.TWO_LEVEL) {
            final var middleGroup = xmlProject.getGroupRange(main);
            log.debug("Middle Group Range for main group range '{}' found: {}", main, middleGroup);

            getResponse().ok();
            return limitAndGetAsList(middleGroup.getGroupAddresses());
        } else {
            getResponse().forbidden();
            return List.of();
        }

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
    public List<XmlGroupAddress> getGroupAddresses(@Param int main, @Param int middle) {
        log.trace("Request addresses for middle group: {}/{}", main, middle);
        checkArgumentMainGroupRange(main);
        checkArgumentMiddleGroupRange(middle);

        final var xmlProject = getXmlProject();
        if (xmlProject.getGroupAddressStyle() == XmlGroupAddressStyle.THREE_LEVEL) {
            final var middleGroup = xmlProject.getGroupRange(main, middle);
            log.debug("Middle Group Range for main group range '{}/{}' found: {}", main, middle, middleGroup);

            getResponse().ok();
            return limitAndGetAsList(middleGroup.getGroupAddresses());
        } else {
            getResponse().forbidden();
            return List.of();
        }

    }

    private void checkArgumentMainGroupRange(final int main) {
        Preconditions.checkArgument(main >= 0 && main <= 31,
                "Invalid number of main group provided, should be within range [0-31]: {}", main);
    }

    private void checkArgumentMiddleGroupRange(final int middle) {
        Preconditions.checkArgument(middle >= 0 && middle <= 7,
                "Invalid number of middle group provided, should be within range [0-7]: {}", middle);
    }
}
