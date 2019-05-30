package li.pitschmann.knx.daemon.v1.controllers;

import com.google.common.base.Preconditions;
import li.pitschmann.knx.daemon.v1.json.ProjectOverviewResponse;
import li.pitschmann.knx.parser.XmlGroupRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.GET;
import ro.pippo.controller.Produces;
import ro.pippo.controller.extractor.Param;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for data from KNX Project file
 */
public final class ProjectController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    /**
     * Returns the project overview
     *
     * @return
     */
    @GET("/project")
    @Produces(Produces.JSON)
    public ProjectOverviewResponse getProjectOverview() {
        log.trace("Request for project overview");

        final var xmlProject = getXmlProject();

        // get project data
        final var response = new ProjectOverviewResponse();
        response.setId(xmlProject.getId());
        response.setName(xmlProject.getName());
        response.setGroupAddressStyle(xmlProject.getGroupAddressStyle());
        return response;
    }


//    /**
//     * Returns the group addresses for given {@code pattern}.
//     * <p/>
//     * Accepted formats are:
//     * <ul>
//     *   <li>123    (identical group)</li>
//     *   <li>*123   (all groups that ends with '123'; e.g. 0123, 1123, ..)</li>
//     *   <li>123*   (all groups that starts with '123'; e.g. 1234, 1235, ..)</li>
//     *   <li>*123*  (all groups that contains '123'; e.g. 01234, 11235, ..)</li>
//     *   <li>*      (all groups)</li>
//     * </ul>
//     * @param address
//     * @return
//     */
//    @GET("/ga/{address:\\*?[\\d]+\\*?}")
//    public Object getGroupAddressesFreeLevel(@Param String address) {
//        log.trace("Request for specific group address pattern in project: {}", address);
//
//        final var xmlProject = getXmlProject();
//        // check if the right method has been called
//        Preconditions.checkArgument(
//                "FreeLevel".equals(xmlProject.getGroupAddressStyle()), "You requested group address for " +
//                        "'FreeLevel', but your project is configured for: " + xmlProject.getGroupAddressStyle());
//
//        final Predicate<String> validator;
//        // contains?
//        if (address.startsWith("*") && address.endsWith("*")) {
//            // *123*
//            validator = (s) -> s.contains(address.substring(1, address.length()-1));
//        }
//        // ends with?
//        else if (address.startsWith("*")) {
//            // *123
//            validator = (s) -> s.endsWith(address.substring(0, address.length()-1));
//        }
//        // starts with?
//        else if (address.endsWith("*")) {
//            // 123*
//            validator = (s) -> s.startsWith(address.substring(1));
//        }
//        // identical
//        else {
//            // 123
//            validator = (s) -> s.equals(address);
//        }
//
//
//        // todo something with validator
//        validator.toString();
//        xmlProject.getGroupAddressMap();
//
//
//        return null;
//    }
//
//    /**
//     * Returns the group addresses for given {@code main} and {@code sub} patterns
//     * <p/>
//     * Accepted formats are:
//     * <ul>
//     *     <li>0 / 123  (identical group)</li>
//     *     <li>* / 123  (all groups with sub '123' of all main groups; e.g. 0/123, 1/123, .. , 31/123)</li>
//     *     <li>0 / *    (all groups of main group '0'; e.g. 0/1, 0/2, .. , 0/2047)</li>
//     *     <li>* / *    (all groups; e.g. 0/1, 0/2, .. , 31/2047)</li>
//     * </ul>
//     * @param main
//     * @param sub
//     * @return
//     */
//    @GET("/ga/{main:(\\*|[\\d]+)}/{sub:(\\*|[\\d]+)}")
//    public Object getGroupAddressesTwoLevel(@Param String main, @Param String sub) {
//        log.trace("Request for two level group addresses in project: {}/{}", main, sub);
//
//        final var xmlProject = getXmlProject();
//        // check if the right method has been called
//        Preconditions.checkArgument(
//                "TwoLevel".equals(xmlProject.getGroupAddressStyle()), "You requested group address for " +
//                        "'TwoLevel', but your project is configured for: " + xmlProject.getGroupAddressStyle());
//
//
//        return null;
//    }


    /**
     * Returns the group addresses for 1st group hierarchy level (main)
     * <p/>
     * <ul>
     * <li>Two Level: 0 .. 31</li>
     * <li>Three Level: 0 .. 31</li>
     * </ul>
     * <p>
     * Free Level is not supported (will return an empty list with HTTP bad request code)
     *
     * @return
     */
    @GET("/project/groups")
    @Produces(Produces.JSON)
    public List<XmlGroupRange> getGroups() {
        log.trace("Request for all main group in project");

        final var xmlProject = getXmlProject();

        final var groupAddressStyle = xmlProject.getGroupAddressStyle();
        if ("ThreeLevel".equals(groupAddressStyle)
                || "TwoLevel".equals(groupAddressStyle)) {

            final int start = getRequest().getParameter("start").toInt(0);
            final int length = getRequest().getParameter("length").toInt(Integer.MAX_VALUE);

            getResponse().ok();
            return xmlProject.getMainGroups().stream().skip(start).limit(length).collect(Collectors.toList());
        } else {
            getResponse().badRequest();
            log.warn("Bad Request for get '/group' and group address style: {}", groupAddressStyle);
            return Collections.emptyList();
        }
    }

    /**
     * Returns the group addresses for a specific main level
     * (1st hierarchy level)
     * <p/>
     * <ul>
     * <li>Two Level: 0 .. 31</li>
     * <li>Three Level: 0 .. 31</li>
     * </ul>
     * <p>
     * Free Level is not supported (will return an empty list with HTTP bad request code)
     *
     * @return The main group which is an instance of {@link XmlGroupRange}
     */
    @GET("/project/groups/{main: \\d+}")
    @Produces(Produces.JSON)
    public XmlGroupRange getGroups(@Param int main) {
        log.trace("Request for main group in project: {}", main);
        Preconditions.checkArgument(main >= 0 && main <= 31,
                "Invalid number of main group provided, should be within range [0-31]: " + main);

        // valid main group number?
        final var xmlProject = getXmlProject();

        final var groupAddressStyle = xmlProject.getGroupAddressStyle();
        if ("ThreeLevel".equals(groupAddressStyle) || "TwoLevel".equals(groupAddressStyle)) {
            final var mainGroup = xmlProject.getMainGroup(main);
            log.debug("Request for get '{}' found: {}", getRequest().getPath(), mainGroup);

            getResponse().ok();
            return mainGroup;
        } else {
            log.warn("Bad Request for get '{}' and group address style: {}", getRequest().getPath(), groupAddressStyle);

            getResponse().badRequest();
            return null;
        }
    }

    /**
     * Returns the group addresses for a specific main and middle levels
     * (1st and 2nd hierarchy levels)
     * <p/>
     * <ul>
     * <li>Three Level: 0 .. 31 / 0 .. 7</li>
     * </ul>
     * <p>
     * Two Level and Free Level are not supported (will return an empty list with HTTP bad request code)
     *
     * @return The main group which is an instance of {@link XmlGroupRange}
     */
    @GET("/project/groups/{main: \\d+}/{middle: \\d+}")
    @Produces(Produces.JSON)
    public XmlGroupRange getGroups(@Param int main, @Param int middle) {
        log.trace("Request for middle group of main group '{}' in project: {}", main);
        Preconditions.checkArgument(main >= 0 && main <= 31,
                "Invalid number of main group provided, should be within range [0-31]: " + main);
        Preconditions.checkArgument(middle >= 0 && middle <= 7,
                "Invalid number of middle group provided, should be within range [0-7]: " + middle);

        // valid main group number?
        final var xmlProject = getXmlProject();

        final var groupAddressStyle = xmlProject.getGroupAddressStyle();
        if ("ThreeLevel".equals(groupAddressStyle)) {
            final var middleGroup = xmlProject.getMiddleGroup(main, middle);
            log.debug("Request for get '{}' found: {}", getRequest().getPath(), middleGroup);

            getResponse().ok();
            return middleGroup;
        } else {

            log.warn("Bad Request for get '{}' and group address style: {}", getRequest().getPath(), groupAddressStyle);

            getResponse().badRequest();
            return null;
        }

    }
}
