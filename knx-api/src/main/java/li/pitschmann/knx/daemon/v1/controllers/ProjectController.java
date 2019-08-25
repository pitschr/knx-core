package li.pitschmann.knx.daemon.v1.controllers;

import com.google.common.base.Preconditions;
import li.pitschmann.knx.daemon.v1.json.ProjectStructureRequest;
import li.pitschmann.knx.parser.XmlGroupAddress;
import li.pitschmann.knx.parser.XmlGroupRange;
import ro.pippo.controller.GET;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            response.setGroupAddressStyle(xmlProject.getGroupAddressStyle());
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
        if ("TwoLevel".equals(groupAddressStyle)
                || "ThreeLevel".equals(groupAddressStyle)) {
            final var mainGroups = xmlProject.getMainGroups();
            log.debug("Request for get '{}' found: {}", getRequest().getPath(), mainGroups);

            getResponse().ok();
            return limitAndGetAsList(mainGroups);
        } else {
            log.warn("Bad Request for get '/group' and group address style: {}", groupAddressStyle);

            getResponse().badRequest();
            return Collections.emptyList();
        }
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

        // valid main group number?
        final var xmlProject = getXmlProject();

        final var groupAddressStyle = xmlProject.getGroupAddressStyle();
        if ("ThreeLevel".equals(groupAddressStyle)) {
            final var mainGroup = xmlProject.getMainGroup(main);
            log.debug("Request for get '{}' found: {}", getRequest().getPath(), mainGroup);

            getResponse().ok();
            return limitAndGetAsList(mainGroup.getChildGroupRanges());

            // return mainGroup.getChildGroupRanges();
        } else {
            log.warn("Bad Request for get '{}' and group address style: {}", getRequest().getPath(), groupAddressStyle);

            getResponse().badRequest();
            return null;
        }
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
        final var groupAddressStyle = xmlProject.getGroupAddressStyle();
        if ("TwoLevel".equals(groupAddressStyle)) {
            final var middleGroup = xmlProject.getMainGroup(main);
            log.debug("Request for get '{}' found: {}", getRequest().getPath(), middleGroup);

            getResponse().ok();
            return limitAndGetAsList(middleGroup.getGroupAddresses());
        } else {
            log.warn("Bad Request for get '{}' and group address style: {}", getRequest().getPath(), groupAddressStyle);

            getResponse().badRequest();
            return null;
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
    public List<XmlGroupAddress> getAddresses(@Param int main, @Param int middle) {
        log.trace("Request for middle group of main group '{}' in project: {}", main);
        checkArgumentMainGroup(main);
        checkArgumentMiddleGroup(middle);

        final var xmlProject = getXmlProject();
        final var groupAddressStyle = xmlProject.getGroupAddressStyle();
        if ("ThreeLevel".equals(groupAddressStyle)) {
            final var middleGroup = xmlProject.getMiddleGroup(main, middle);
            log.debug("Request for get '{}' found: {}", getRequest().getPath(), middleGroup);

            getResponse().ok();
            return limitAndGetAsList(middleGroup.getGroupAddresses());
        } else {
            log.warn("Bad Request for get '{}' and group address style: {}", getRequest().getPath(), groupAddressStyle);

            getResponse().badRequest();
            return null;
        }

    }

    /**
     * Returns a range of {@code T} elements from {@link Collection}.
     * May be limited using {@code start} and {@code length} request parameters.
     *
     * @param collection
     * @param <T>
     * @return a new list of elements from {@link Collection}
     */
    private <T> List<T> limitAndGetAsList(final Collection<T> collection) {
        final int start = getRequest().getParameter("start").toInt(0);
        final int length = getRequest().getParameter("limit").toInt(Integer.MAX_VALUE);

        if (start == 0 && length == Integer.MAX_VALUE) {
            log.trace("No range defined.");
            // no limit
            return new ArrayList<>(collection);
        } else {
            log.trace("Range defined: start={}, limit={}", start, length);
            // limit
            return collection.stream().skip(start).limit(length).collect(Collectors.toList());
        }
    }

    private void checkArgumentMainGroup(final int main) {
        Preconditions.checkArgument(main >= 0 && main <= 31,
                "Invalid number of main group provided, should be within range [0-31]: %s", main);
    }

    private void checkArgumentMiddleGroup(final int middle) {
        Preconditions.checkArgument(middle >= 0 && middle <= 7,
                "Invalid number of middle group provided, should be within range [0-7]: %s", middle);
    }
}
