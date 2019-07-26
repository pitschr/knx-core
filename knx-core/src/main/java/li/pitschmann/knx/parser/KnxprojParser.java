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

package li.pitschmann.knx.parser;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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
 * <li>Group Addresses</li>
 * <li>Name</li>
 * <li>Datapoint Type</li>
 * <li>Flags</li>
 * </ul>
 *
 * @author pitschr
 */
public final class KnxprojParser {
    private static final Logger log = LoggerFactory.getLogger(KnxprojParser.class);
    private static final String FILE_EXTENSION = ".knxproj";

    private KnxprojParser() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Parses the given *.knxproj file
     *
     * @param path location of *.knxproj file
     * @return KNX project
     */
    public static XmlProject parse(final Path path) {
        Preconditions.checkArgument(Files.exists(path), "File '" + path + "' doesn't exists.");
        Preconditions.checkArgument(path.toString().toLowerCase().endsWith(FILE_EXTENSION), "Only '" + FILE_EXTENSION + "' is supported.");

        log.debug("File '{}' to be parsed.", path);

        final var sw = Stopwatch.createStarted();
        final XmlProject project;
        try (final var zipFile = new ZipFile(path.toFile())) {
            // get KNX project information
            project = getKnxProjectInformation(zipFile);

            // get KNX group ranges and addresses
            final var groupRanges = parseGroupRanges(zipFile);
            project.setGroupRangeMap(groupRanges);

            final var groupAddresses = parseGroupAddresses(zipFile);
            project.setGroupAddressMap(groupAddresses);

            // links the KNX group ranges with KNX group addresses
            linkGroupRangeAndAddresses(groupRanges, groupAddresses);
        } catch (final IOException | VTDException ex) {
            throw new KnxprojParserException("Something went wrong during parsing the zip file: " + path);
        }
        log.info("KNX Project '{}' parse took {} ms:\n{}", path, sw.elapsed(TimeUnit.MILLISECONDS), project);

        return project;
    }

    /**
     * Returns {@link XmlProject} based on project information from '*.knxproj' file
     *
     * @param zipFile to be parsed
     * @return KNX project information
     * @throws IOException  I/O exception when reading ZIP stream
     * @throws VTDException exception from VTD-XML
     */
    private static XmlProject getKnxProjectInformation(final ZipFile zipFile) throws IOException, VTDException {
        // reads the 'project.xml' file from 'P-<digit>' folder
        final var bytes = findAndReadToBytes(zipFile, "^P-[\\dA-F]+/project\\.xml$");

        // create parser (without namespace, we don't need it for *.knxproj file)
        final var vtdGen = new VTDGen();
        vtdGen.setDoc(bytes);
        vtdGen.parse(false);
        final var vtdNav = vtdGen.getNav();

        final var project = new XmlProject();
        // go to <Project /> element and read @Id
        vtdNav.toElement(VTDNav.FIRST_CHILD);
        project.setId(readAttributeValue(vtdNav, "Id",
                () -> new KnxprojParserException("Attribute <Project @Id /> not found.")));

        // go to <ProjectInformation /> element and read @Name and @GroupAddressStyle
        vtdNav.toElement(VTDNav.FIRST_CHILD);
        project.setName(readAttributeValue(vtdNav, "Name",
                () -> new KnxprojParserException("Attribute <ProjectInformation @Name /> not found.")));

        project.setGroupAddressStyle(readAttributeValue(vtdNav, "GroupAddressStyle",
                () -> new KnxprojParserException("Attribute <ProjectInformation @GroupAddressStyle /> not found.")));

        return project;
    }

    /**
     * Returns a map of {@link XmlGroupRange} based on project information from '*.knxproj' file
     *
     * @param zipFile to be parsed
     * @return map of KNX Group Ranges (id of map is group range id)
     * @throws IOException
     * @throws VTDException
     */
    @Nonnull
    private static Map<String, XmlGroupRange> parseGroupRanges(final @Nonnull ZipFile zipFile) throws IOException, VTDException {
        // reads the '0.xml' file from 'P-<digit>' folder
        final var bytes = findAndReadToBytes(zipFile, "^P-[\\dA-F]+/0\\.xml$");

        // create parser (without namespace, we don't need it for *.knxproj file)
        final var vtdGen = new VTDGen();
        vtdGen.setDoc(bytes);
        vtdGen.parse(false);
        final var vtdNav = vtdGen.getNav();
        final var vtdAutoPilot = new AutoPilot(vtdNav);
        final var groupRanges = Maps.<String, XmlGroupRange>newLinkedHashMap();

        // find all <GroupRange /> elements
        vtdAutoPilot.selectXPath("/KNX/Project/Installations//GroupAddresses//GroupRange");

        // iterate through all group ranges
        while (vtdAutoPilot.evalXPath() != -1) {
            final var groupRange = new XmlGroupRange();

            // obtain required @Id, @RangeStart, @RangeEnd and @Name
            groupRange.setId(readAttributeValue(vtdNav, "Id",
                    () -> new KnxprojParserException("Attribute <GroupRange @Id /> not found.")));

            groupRange.setRangeStart(Integer.parseInt(readAttributeValue(vtdNav, "RangeStart",
                    () -> new KnxprojParserException("Attribute <GroupRange @RangeStart /> not found for: " + groupRange.getId()))));

            groupRange.setRangeEnd(Integer.parseInt(readAttributeValue(vtdNav, "RangeEnd",
                    () -> new KnxprojParserException("Attribute <GroupRange @RangeEnd /> not found for: " + groupRange.getId()))));

            groupRange.setName(readAttributeValue(vtdNav, "Name",
                    () -> new KnxprojParserException("Attribute <GroupRange @Name /> not found for: " + groupRange.getId())));

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

            groupRanges.put(groupRange.getId(), groupRange);
        }

        return groupRanges;
    }


    /**
     * Returns a map of {@link XmlGroupAddress} based on project information from '*.knxproj' file
     *
     * @param zipFile to be parsed
     * @return map of KNX Group Addresses (id of group address is the key)
     * @throws IOException  I/O exception when reading ZIP stream
     * @throws VTDException exception from VTD-XML
     */
    @Nonnull
    private static Map<String, XmlGroupAddress> parseGroupAddresses(final @Nonnull ZipFile zipFile) throws IOException, VTDException {
        // reads the '0.xml' file from 'P-<digit>' folder
        final var bytes = findAndReadToBytes(zipFile, "^P-[\\dA-F]+/0\\.xml$");

        // create parser (without namespace, we don't need it for *.knxproj file)
        final var vtdGen = new VTDGen();
        vtdGen.setDoc(bytes);
        vtdGen.parse(false);
        final var vtdNav = vtdGen.getNav();
        final var vtdAutoPilot = new AutoPilot(vtdNav);
        final var groupAddresses = Maps.<String, XmlGroupAddress>newLinkedHashMap();

        // find all <GroupAddress /> elements
        vtdAutoPilot.selectXPath("/KNX/Project/Installations/Installation/GroupAddresses//GroupAddress");

        // iterate through all group addresses
        while (vtdAutoPilot.evalXPath() != -1) {
            final var groupAddress = new XmlGroupAddress();

            // obtain required @Id, @Address, @Name
            groupAddress.setId(readAttributeValue(vtdNav, "Id",
                    () -> new KnxprojParserException("Attribute <GroupAddress @Id /> not found.")));

            groupAddress.setAddress(readAttributeValue(vtdNav, "Address",
                    () -> new KnxprojParserException("Attribute <GroupAddress @Address /> not found for: " + groupAddress.getId())));

            groupAddress.setName(readAttributeValue(vtdNav, "Name",
                    () -> new KnxprojParserException("Attribute <GroupAddress @Name /> not found for: " + groupAddress.getId())));

            // obtain optional @Description and @DatapointType
            groupAddress.setDescription(readAttributeValue(vtdNav, "Description"));
            groupAddress.setDatapointType(readAttributeValue(vtdNav, "DatapointType"));

            // get flags (via ComObjectInstanceRef)
            readFlags(vtdNav.cloneNav(), groupAddress);

            // move to parent element and verify if it is 'GroupRange'
            Preconditions.checkState(
                    vtdNav.toElement(VTDNav.PARENT) && vtdNav.matchElement("GroupRange"),
                    "Parent of <GroupAddress /> should be a <GroupRange />");

            // we are currently on GroupRange level
            // GroupRange Id cannot be empty because we already checked it in 'parseGroupRanges' method
            groupAddress.setParentId(Objects.requireNonNull(readAttributeValue(vtdNav, "Id")));

            groupAddresses.put(groupAddress.getId(), groupAddress);
        }

        return groupAddresses;
    }

    /**
     * Creates a link between {@link XmlGroupRange} and {@link XmlGroupAddress}
     *
     * @param xmlGroupRangeMap
     * @param xmlGroupAddressMap
     */
    private static void linkGroupRangeAndAddresses(final @Nonnull Map<String, XmlGroupRange> xmlGroupRangeMap, final @Nonnull Map<String, XmlGroupAddress> xmlGroupAddressMap) {
        // link Group Range (parent) with Group Ranges (child)
        xmlGroupRangeMap.values().forEach(xgr -> {
            if (xgr.getParentId()!=null) {
                xmlGroupRangeMap.get(xgr.getParentId()).getChildGroupRanges().add(xgr);
            }
        });

        // link Group Range with Group Addresses
        xmlGroupAddressMap.values().forEach(xga -> xmlGroupRangeMap.get(xga.getParentId()).getGroupAddresses().add(xga));
    }

    /**
     * Reads the flag for given {@link XmlGroupAddress}
     *
     * @param vtdNav       VTD navigator (must be cloned)
     * @param groupAddress look up for flags that is connected to this group address
     * @throws VTDException exception from VTD-XML
     */
    private static final void readFlags(final VTDNav vtdNav, final XmlGroupAddress groupAddress) throws VTDException {
        final var vtdAutoPilot = new AutoPilot(vtdNav);
        // select xpath and evaluate
        vtdAutoPilot.selectXPath("//ComObjectInstanceRef[Connectors/Send[@GroupAddressRefId='" + groupAddress.getId() + "']]");
        vtdAutoPilot.evalXPath();
        // flags
        groupAddress.setCommunicationFlag(readAttributeValue(vtdNav, "CommunicationFlag"));
        groupAddress.setReadFlag(readAttributeValue(vtdNav, "ReadFlag"));
        groupAddress.setWriteFlag(readAttributeValue(vtdNav, "WriteFlag"));
        groupAddress.setTransmitFlag(readAttributeValue(vtdNav, "TransmitFlag"));
        groupAddress.setUpdateFlag(readAttributeValue(vtdNav, "UpdateFlag"));
    }

    /**
     * Returns value of required attribute
     *
     * @param vtdNav    current instance of {@link VTDNav}
     * @param attribute attribute name to look up
     * @param throwable exception to thrown in case the attribute doesn't exists
     * @return value of attribute, otherwise {@link KnxprojParserException}
     * @throws NavException navigation exception by VTD-XML
     */
    private static String readAttributeValue(final VTDNav vtdNav, final String attribute, final Supplier<KnxprojParserException> throwable) throws NavException {
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
     * @throws NavException navigation exception by VTD-XML
     */
    private static String readAttributeValue(final VTDNav vtdNav, final String attribute) throws NavException {
        return readAttributeValue(vtdNav, attribute, (String) null);
    }

    /**
     * Returns value of optional attribute
     *
     * @param vtdNav    current instance of {@link VTDNav}
     * @param attribute attribute name to look up
     * @return value of attribute, otherwise {@code defaultValue}
     * @throws NavException navigation exception by VTD-XML
     */
    private static String readAttributeValue(final VTDNav vtdNav, final String attribute, final String defaultValue) throws NavException {
        final var index = vtdNav.getAttrVal(attribute);
        return index > 0 ? vtdNav.toString(index) : defaultValue;
    }

    /**
     * Find file that matches the regular expression {@code filePathRegEx}, reads as {@link InputStream} and
     * converts to a byte array. The internal open stream will be closed.
     *
     * @param zipFile
     * @param filePathRegEx
     * @return byte array
     * @throws IOException
     */
    private static byte[] findAndReadToBytes(final ZipFile zipFile, final String filePathRegEx) throws IOException {
        // find file that matches filePathRegEx in ZIP file
        final var zipEntry = zipFile.stream().filter(f -> f.getName().matches(filePathRegEx))
                .findFirst()
                .orElseThrow(() -> new KnxprojParserException("File '" + filePathRegEx + "' not found in ZIP file"));
        log.debug("File in ZIP File found: {}", zipEntry.getName());

        byte[] bytes;
        try (final var in = zipFile.getInputStream(zipEntry)) {
            bytes = ByteStreams.toByteArray(in);
            if (log.isDebugEnabled()) {
                log.debug("Data stream from file '{}':\n{}", zipEntry.getName(), new String(bytes, StandardCharsets.UTF_8));
            }
        }
        return bytes;
    }
}
