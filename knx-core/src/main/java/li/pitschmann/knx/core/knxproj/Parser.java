/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2019 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.knx.core.knxproj;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import li.pitschmann.knx.core.exceptions.KnxProjectParserException;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.zip.ZipFile;

/**
 * This parser is indented to parse the *.knxproj files provided by the
 * KNX Association which is supported by ETS 4+ and should be used for future.
 * <p>
 * Previous files like pr5, pr4, ..., prx and including OFS Export
 * (which exports as *.efs) should not be used anymore as it is not
 * longer maintained by the KNX Association.
 * <p>
 * The specification of XML Project Format Description can be downloaded on
 * https://my.knx.org/ > Downloads > Special
 * <p>
 * Here we are not parsing the full project - we will obtain only relevant
 * data from *.knxproj file which might be helpful for us like
 * <ul>
 * <li>Project Overview (Name, Description, ...)</li>
 * <li>Group Ranges (Name, Description, Level, ...)</li>
 * <li>Group Addresses (Name, Description, Datapoint Type, ...)</li>
 * <li>Group Address Metadata (Flags, ...)</li>
 * </ul>
 *
 * @author pitschr
 */
final class Parser {
    private static final Logger log = LoggerFactory.getLogger(Parser.class);
    private static final String FILE_EXTENSION = ".knxproj";

    private Parser() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Parses the given *.knxproj file
     *
     * @param path location of *.knxproj file
     * @return KNX project
     */
    static XmlProject parse(final Path path) {
        Preconditions.checkArgument(Files.isReadable(path),
                "File '{}' doesn't exists or is not readable.", path);
        Preconditions.checkArgument(path.toString().toLowerCase().endsWith(FILE_EXTENSION),
                "Only '{}' is supported.", FILE_EXTENSION);

        log.debug("File '{}' to be parsed.", path);

        final var sw = Stopwatch.createStarted();
        final XmlProject project;
        try (final var zipFile = new ZipFile(path.toFile())) {
            // get KNX project information
            // reads the 'project.xml' file from 'P-<digit>' folder
            final var projectOverview = toVTDNav(zipFile, "^P-[\\dA-F]+/project\\.xml$");
            project = getKnxProjectInformation(projectOverview);

            // get KNX group ranges and addresses
            // reads the '0.xml' file from 'P-<digit>' folder
            final var projectData = toVTDNav(zipFile, "^P-[\\dA-F]+/0\\.xml$");
            final var groupRanges = parseGroupRanges(projectData);
            project.setGroupRanges(groupRanges);

            final var groupAddresses = parseGroupAddresses(projectData);
            project.setGroupAddresses(groupAddresses);

            // links the KNX group addresses with the KNX group address meta data / flags
            updateGroupAddressWithMetadatas(project, projectData);

            // links the KNX group ranges with KNX group addresses
            linkGroupRanges(project);
            linkGroupAddresses(project);
        } catch (final IOException | VTDException ex) {
            throw new KnxProjectParserException("Something went wrong during parsing the zip file: " + path);
        }
        log.info("KNX Project '{}' parse took {} ms:\n{}", path, sw.elapsed(TimeUnit.MILLISECONDS), project);

        return project;
    }

    /**
     * Returns {@link XmlProject} based on project information from '*.knxproj' file
     *
     * @param originalVTDNav original VTDNav instance
     * @return KNX project
     * @throws VTDException exception from VTD-XML
     */
    private static XmlProject getKnxProjectInformation(final VTDNav originalVTDNav) throws VTDException {
        final var vtdNav = originalVTDNav.duplicateNav();
        final var project = new XmlProject();

        // find KNX Project version <KNX /> element and http://knx.org/xml/project/20 --> Version: 20
        final var xmlNamespace = readAttributeValue(vtdNav, "xmlns",
                () -> new KnxProjectParserException("Attribute <KNX @xmlns /> not found."));
        log.debug("XML Project Namespace: {}", xmlNamespace);
        final var version = Integer.parseInt(xmlNamespace.substring(xmlNamespace.lastIndexOf("/") + 1));
        project.setVersion(version);

        // go to <Project /> element and read @Id
        vtdNav.toElement(VTDNav.FIRST_CHILD);
        project.setId(readAttributeValue(vtdNav, "Id",
                () -> new KnxProjectParserException("Attribute <Project @Id /> not found.")));

        // go to <ProjectInformation /> element and read @Name and @GroupAddressStyle
        vtdNav.toElement(VTDNav.FIRST_CHILD);
        project.setName(readAttributeValue(vtdNav, "Name",
                () -> new KnxProjectParserException("Attribute <ProjectInformation @Name /> not found.")));

        final var groupAddressStyleStr = readAttributeValue(vtdNav, "GroupAddressStyle",
                () -> new KnxProjectParserException("Attribute <ProjectInformation @GroupAddressStyle /> not found."));
        project.setGroupAddressStyle(XmlGroupAddressStyle.parse(groupAddressStyleStr));

        return project;
    }

    /**
     * Returns a list of {@link XmlGroupRange} based on project information
     * from '*.knxproj' file in the same order.
     *
     * @param originalVTDNav original VTDNav instance
     * @return list of {@link XmlGroupRange}
     * @throws VTDException exception from VTD-XML
     */
    private static List<XmlGroupRange> parseGroupRanges(final VTDNav originalVTDNav) throws VTDException {
        final var vtdNav = originalVTDNav.duplicateNav();
        final var vtdAutoPilot = new AutoPilot(vtdNav);
        final var groupRanges = new LinkedList<XmlGroupRange>();

        // find the depth of <GroupRanges /> (used for level calculation)
        vtdAutoPilot.selectXPath("/KNX/Project/Installations//GroupAddresses/GroupRanges");
        vtdAutoPilot.evalXPath();
        final var rootGroupRangesDepth = vtdNav.getCurrentDepth() + 1; // +1 because we don't want to count <GroupRanges /> itself

        // find all <GroupRange /> elements and iterate through all group ranges
        vtdAutoPilot.selectXPath("/KNX/Project/Installations//GroupAddresses//GroupRange");
        while (vtdAutoPilot.evalXPath() != -1) {
            final var groupRange = new XmlGroupRange();

            // obtain required @Id, @RangeStart, @RangeEnd and @Name
            groupRange.setId(readAttributeValue(vtdNav, "Id",
                    () -> new KnxProjectParserException("Attribute <GroupRange @Id /> not found.")));

            groupRange.setRangeStart(Integer.parseInt(readAttributeValue(vtdNav, "RangeStart",
                    () -> new KnxProjectParserException("Attribute <GroupRange @RangeStart /> not found for: " + groupRange.getId()))));

            groupRange.setRangeEnd(Integer.parseInt(readAttributeValue(vtdNav, "RangeEnd",
                    () -> new KnxProjectParserException("Attribute <GroupRange @RangeEnd /> not found for: " + groupRange.getId()))));

            groupRange.setName(readAttributeValue(vtdNav, "Name",
                    () -> new KnxProjectParserException("Attribute <GroupRange @Name /> not found for: " + groupRange.getId())));

            groupRange.setLevel(vtdNav.getCurrentDepth() - rootGroupRangesDepth);

            // check if group range is on main level (below <GroupRanges /> element)
            if (vtdNav.toElement(VTDNav.PARENT) && vtdNav.matchElement("GroupRange")) {
                // current group range is not on main level

                // we know that <GroupRange @Id /> exists, because it is already checked few lines above
                final var parentId = Objects.requireNonNull(readAttributeValue(vtdNav, "Id"));
                if (log.isDebugEnabled()) {
                    log.debug("Not a root level as parent element is {} ({}): {}", vtdNav.toRawString(vtdNav.getCurrentIndex()), parentId, groupRange);
                }
                groupRange.setParentId(parentId);
            } else {
                // current group range is on main line
                log.debug("Root level as parent element is 'GroupRanges': {}", groupRange);
                groupRange.setParentId(null);
            }

            groupRanges.add(groupRange);
        }

        return groupRanges;
    }

    /**
     * Returns a list of {@link XmlGroupAddress} based on project information
     * from '*.knxproj' file in the same order.
     *
     * @param originalVTDNav original VTDNav instance
     * @return list of {@link XmlGroupAddress}
     * @throws VTDException exception from VTD-XML
     */
    private static List<XmlGroupAddress> parseGroupAddresses(final VTDNav originalVTDNav) throws VTDException {
        final var vtdNav = originalVTDNav.duplicateNav();
        final var vtdAutoPilot = new AutoPilot(vtdNav);
        final var groupAddresses = new LinkedList<XmlGroupAddress>();

        // find all <GroupAddress /> elements
        vtdAutoPilot.selectXPath("/KNX/Project/Installations/Installation/GroupAddresses//GroupAddress");

        // iterate through all group addresses
        while (vtdAutoPilot.evalXPath() != -1) {
            final var groupAddress = new XmlGroupAddress();

            // obtain required @Id, @Address, @Name
            groupAddress.setId(readAttributeValue(vtdNav, "Id",
                    () -> new KnxProjectParserException("Attribute <GroupAddress @Id /> not found.")));

            groupAddress.setAddress(readAttributeValue(vtdNav, "Address",
                    () -> new KnxProjectParserException("Attribute <GroupAddress @Address /> not found for: " + groupAddress.getId())));

            groupAddress.setName(readAttributeValue(vtdNav, "Name",
                    () -> new KnxProjectParserException("Attribute <GroupAddress @Name /> not found for: " + groupAddress.getId())));

            // obtain optional @Description and @DatapointType
            groupAddress.setDescription(readAttributeValue(vtdNav, "Description"));
            groupAddress.setDataPointType(readAttributeValue(vtdNav, "DatapointType"));

            // move to parent element and verify if it is 'GroupRange'
            Preconditions.checkState(
                    vtdNav.toElement(VTDNav.PARENT) && vtdNav.matchElement("GroupRange"),
                    "Parent of <GroupAddress /> should be a <GroupRange />");

            // we are currently on GroupRange level
            // GroupRange Id cannot be empty because we already checked it in 'parseGroupRanges' method
            groupAddress.setParentId(Objects.requireNonNull(readAttributeValue(vtdNav, "Id")));

            groupAddresses.add(groupAddress);
        }

        return groupAddresses;
    }

    /**
     * Links between parent {@link XmlGroupRange} and child {@link XmlGroupRange}
     *
     * @param xmlProject
     */
    private static void linkGroupRanges(final XmlProject xmlProject) {
        // create temporary map of parent group range (key) and list of child group ranges (values)
        final var tmp = new LinkedHashMap<String, List<XmlGroupRange>>();
        for (final var xmlGroupRange : xmlProject.getGroupRanges()) {
            final var parentId = xmlGroupRange.getParentId();
            if (parentId != null) {
                tmp.computeIfAbsent(parentId, k -> new LinkedList<>()).add(xmlGroupRange);
            }
        }

        // link Group Range (parent) with Group Ranges (child)
        for (final var entry : tmp.entrySet()) {
            xmlProject.getGroupRangeById(entry.getKey()).setChildGroupRanges(entry.getValue());
        }
    }

    /**
     * Links between {@link XmlGroupRange} and {@link XmlGroupAddress}
     *
     * @param xmlProject
     */
    private static void linkGroupAddresses(final XmlProject xmlProject) {
        // create temporary map of group range (key) and child group addresses (values)
        final var tmp = new LinkedHashMap<String, List<XmlGroupAddress>>();
        for (final var xmlGroupAddress : xmlProject.getGroupAddresses()) {
            final var parentId = Objects.requireNonNull(xmlGroupAddress.getParentId());
            tmp.computeIfAbsent(parentId, k -> new LinkedList<>()).add(xmlGroupAddress);
        }
        // link Group Range (parent) with Group Addresses (child)
        for (final var entry : tmp.entrySet()) {
            xmlProject.getGroupRangeById(entry.getKey()).setGroupAddresses(entry.getValue());
        }
    }


    /**
     * Parses for group address meta data and links to group addresses
     * based on project information from '*.knxproj' file in the same order.
     *
     * @param xmlProject
     * @param originalVTDNav original VTDNav instance
     * @throws VTDException exception from VTD-XML
     */
    private static void updateGroupAddressWithMetadatas(final XmlProject xmlProject,
                                                        final VTDNav originalVTDNav) throws VTDException {
        final var vtdNav = originalVTDNav.duplicateNav();
        final var vtdAutoPilot = new AutoPilot(vtdNav);

        // bi-consumer to set the flags to KNX group address instance
        final BiConsumer<String, VTDNav> xmlGroupAddressConsumer = (gaId, nav) -> {
            final var xmlGroupAddress = xmlProject.getGroupAddressById(gaId);
            if (xmlGroupAddress != null) {
                xmlGroupAddress.setCommunicationFlag(readAttributeValue(nav, "CommunicationFlag"));
                xmlGroupAddress.setReadFlag(readAttributeValue(nav, "ReadFlag"));
                xmlGroupAddress.setWriteFlag(readAttributeValue(nav, "WriteFlag"));
                xmlGroupAddress.setTransmitFlag(readAttributeValue(nav, "TransmitFlag"));
                xmlGroupAddress.setUpdateFlag(readAttributeValue(nav, "UpdateFlag"));
            }
        };


        if (xmlProject.getVersion() >= 20) {
            //
            // for project version 20 and newer (XML 2.0 and above)
            //

            // find all <ComObjectInstanceRef /> elements which have 'Links' attribute present
            vtdAutoPilot.selectXPath("/KNX/Project/Installations/Installation/Topology//ComObjectInstanceRef[@Links]");

            // iterate through all group addresses
            while (vtdAutoPilot.evalXPath() != -1) {
                // get links
                final var links = readAttributeValue(vtdNav, "Links",
                        () -> new KnxProjectParserException("Attribute <ComObjectInstanceRef @Links /> not found."));

                // create meta data for each link (link is separated by space character)
                final var linkTokens = new StringTokenizer(links, " ");
                while (linkTokens.hasMoreTokens()) {
                    // link is a shortened e.g. "GA-277" belongs to "P-0503-0_GA-277"
                    // (pattern: <projectId>-0_<groupAddressId>)
                    final var link = linkTokens.nextToken();
                    final var groupAddressId = xmlProject.getId() + "-0_" + link;

                    xmlGroupAddressConsumer.accept(groupAddressId, vtdNav);
                }
            }
        } else {
            //
            // for older project versions (XML 1.4 and below)
            //

            // find all <ComObjectInstanceRef /> elements which have 'GroupAddressRefId' attribute present
            vtdAutoPilot.selectXPath("/KNX/Project/Installations/Installation/Topology//ComObjectInstanceRef[Connectors/Send[@GroupAddressRefId]]");

            // iterate through all group addresses
            while (vtdAutoPilot.evalXPath() != -1) {
                final var xmlGroupAddressRefIds = new LinkedList<String>();

                // get group address ref id (this is 2 levels deeper: ./Connectors/Send)
                final var tmpIndex = vtdNav.getCurrentIndex();
                vtdNav.toElement(VTDNav.FIRST_CHILD, "Connectors");
                vtdNav.toElement(VTDNav.FIRST_CHILD, "Send");
                do {
                    final var groupAddressId = readAttributeValue(vtdNav, "GroupAddressRefId",
                            () -> new KnxProjectParserException("Attribute <ComObjectInstanceRef @GroupAddressRefId /> not found."));
                    xmlGroupAddressRefIds.add(groupAddressId);
                } while (vtdNav.toElement(VTDNav.NEXT_SIBLING));

                // back to the previous position
                vtdNav.recoverNode(tmpIndex);

                // apply for all xml group addresses
                xmlGroupAddressRefIds.forEach(id -> xmlGroupAddressConsumer.accept(id, vtdNav));
            }
        }

    }

    /**
     * Returns value of required attribute
     *
     * @param vtdNav    current instance of {@link VTDNav}
     * @param attribute attribute name to look up
     * @param throwable exception to thrown in case the attribute doesn't exists
     * @return value of attribute, otherwise {@link KnxProjectParserException}
     */
    private static String readAttributeValue(final VTDNav vtdNav,
                                             final String attribute,
                                             final Supplier<KnxProjectParserException> throwable) {
        final var value = readAttributeValue(vtdNav, attribute);
        if (value == null) {
            throw throwable.get();
        }
        return value;
    }

    /**
     * Returns value of optional attribute
     *
     * @param vtdNav    current instance of {@link VTDNav}
     * @param attribute attribute name to look up
     * @return value of attribute, otherwise {@code null}
     */
    @Nullable
    private static String readAttributeValue(final VTDNav vtdNav,
                                             final String attribute) {
        return readAttributeValue(vtdNav, attribute, (String) null);
    }

    /**
     * Returns value of optional attribute
     *
     * @param vtdNav    current instance of {@link VTDNav}
     * @param attribute attribute name to look up
     * @return value of attribute, otherwise {@code defaultValue}
     */
    @Nullable
    private static String readAttributeValue(final VTDNav vtdNav,
                                             final String attribute,
                                             final @Nullable String defaultValue) {
        try {
            final var index = vtdNav.getAttrVal(Objects.requireNonNull(attribute));
            return index > 0 ? vtdNav.toString(index) : defaultValue;
        } catch (final NavException navEx) {
            // should never happen
            throw new AssertionError(navEx);
        }
    }

    /**
     * Find file that matches the regular expression {@code filePathRegEx}, reads as {@link InputStream} and
     * converts to a byte array. The internal open stream will be closed.
     *
     * @param zipFile
     * @param filePathRegEx
     * @return byte array
     * @throws IOException
     * @throws ParseException
     */
    private static VTDNav toVTDNav(final ZipFile zipFile,
                                   final String filePathRegEx) throws IOException, ParseException {
        Preconditions.checkNonNull(zipFile);
        Preconditions.checkNonNull(filePathRegEx);
        // find file that matches filePathRegEx in ZIP file
        final var zipEntry = zipFile.stream().filter(f -> f.getName().matches(filePathRegEx))
                .findFirst()
                .orElseThrow(() -> new KnxProjectParserException("File '" + filePathRegEx + "' not found in ZIP file"));
        log.debug("File in ZIP File found: {}", zipEntry.getName());

        byte[] bytes;
        try (final var in = zipFile.getInputStream(zipEntry)) {
            bytes = in.readAllBytes();
            if (log.isDebugEnabled()) {
                log.debug("Data stream from file '{}':\n{}", zipEntry.getName(), new String(bytes, StandardCharsets.UTF_8));
            }
        }

        final var vtdGen = new VTDGen();
        vtdGen.setDoc(bytes);
        vtdGen.parse(false);
        return vtdGen.getNav();
    }

//    /**
//     * Only for debug purposes!
//     * @param vtdNav
//     * @return string representation of current element incl. attributes and values
//     * @throws VTDException
//     */
//    private static String getVTDNavDebugString(final VTDNav vtdNav) throws VTDException {
//        final var sb = new StringBuilder();
//        sb.append(vtdNav.toString(vtdNav.getCurrentIndex())).append('{');
//        int attrCount = vtdNav.getAttrCount();
//        for (int i=0; i<attrCount; i++) {
//            if (i!=0) {
//                sb.append(", ");
//            }
//            sb.append(vtdNav.toString(vtdNav.getCurrentIndex()+1+i*2))
//                    .append('=')
//                    .append('"')
//                    .append(vtdNav.toString(vtdNav.getCurrentIndex()+2+i*2))
//                    .append('"');
//        }
//        sb.append('}');
//        return sb.toString();
//    }

}
