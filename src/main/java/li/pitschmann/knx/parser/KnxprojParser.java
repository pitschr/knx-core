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
import com.google.common.io.ByteStreams;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(KnxprojParser.class);
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

        LOGGER.debug("File '{}' to be parsed.", path);

        final Stopwatch sw = Stopwatch.createStarted();
        final XmlProject project;
        try (var zipFile = new ZipFile(path.toFile())) {
            // get KNX group address data
            var groupAddresses = getKnxProjectGroupAddresses(zipFile);

            // get KNX project information
            project = getKnxProjectInformation(zipFile);
            project.setGroupAddresses(groupAddresses);
        } catch (final IOException | VTDException ex) {
            throw new KnxprojParserException("Something went wrong during parsing the zip file: " + path);
        }
        LOGGER.info("KNX Project '{}' parse took {} ms:\n{}", path, sw.elapsed(TimeUnit.MILLISECONDS), project);

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
        // find 'project.xml' in ZIP file
        var zipEntry = zipFile.stream().filter(f -> f.getName().matches("^P-\\d+/project\\.xml$"))
                .findFirst()
                .orElseThrow(() -> new KnxprojParserException("File 'project.xml' not found in ZIP file"));
        LOGGER.debug("Project Information file found: {}", zipEntry.getName());

        // convert InputStream into byte array (it will be closed automatically, when ZIP file is closed)
        final var bytes = ByteStreams.toByteArray(zipFile.getInputStream(zipEntry));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Project Information stream:\n{}", new String(bytes, StandardCharsets.UTF_8));
        }

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
     * Returns a list of {@link XmlGroupAddress} based on project information from '*.knxproj' file
     *
     * @param zipFile to be parsed
     * @return list of KNX Group Addresses
     * @throws IOException    I/O exception when reading ZIP stream
     * @throws ParseException parse exception from VTD-XML
     * @throws NavException   navigation exception from VTD-XML
     */
    private static List<XmlGroupAddress> getKnxProjectGroupAddresses(final ZipFile zipFile) throws IOException, VTDException {
        // find 'project.xml' in ZIP file
        final var zipEntry = zipFile.stream().filter(f -> f.getName().matches("^P-\\d+/0\\.xml$"))
                .findFirst()
                .orElseThrow(() -> new KnxprojParserException("File '0.xml' not found in ZIP file"));
        LOGGER.debug("Project Data file found: {}", zipEntry.getName());

        // convert InputStream into byte array (it will be closed automatically, when ZIP file is closed)
        final var bytes = ByteStreams.toByteArray(zipFile.getInputStream(zipEntry));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Project Data stream:\n{}", new String(bytes, StandardCharsets.UTF_8));
        }

        // create parser (without namespace, we don't need it for *.knxproj file)
        final var vtdGen = new VTDGen();
        vtdGen.setDoc(bytes);
        vtdGen.parse(false);
        final var vtdNav = vtdGen.getNav();
        final var vtdAutoPilot = new AutoPilot(vtdNav);
        final var groupAddresses = new LinkedList<XmlGroupAddress>();

        // find all <GroupAddress /> elements
        vtdAutoPilot.selectXPath("/KNX/Project/Installations/Installation/GroupAddresses//GroupAddress");

        // iterate through all group addresses
        while (vtdAutoPilot.evalXPath() != -1) {
            final var groupAddress = new XmlGroupAddress();

            // obtain required @Id, @Address, @Name
            groupAddress.setId(readAttributeValue(vtdNav, "Id",
                    () -> new KnxprojParserException("Attribute <GroupAddress @Id /> not found.")));

            groupAddress.setAddress(Integer.parseInt(readAttributeValue(vtdNav, "Address",
                    () -> new KnxprojParserException("Attribute <GroupAddress @Address /> not found."))));

            groupAddress.setName(readAttributeValue(vtdNav, "Name",
                    () -> new KnxprojParserException("Attribute <GroupAddress @Name /> not found.")));

            // obtain optional @DatapointType
            groupAddress.setDatapointType(readAttributeValue(vtdNav, "DatapointType"));

            // add to list
            groupAddresses.add(groupAddress);
        }

        return groupAddresses;
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
     * Returns value of required attribute
     *
     * @param vtdNav    current instance of {@link VTDNav}
     * @param attribute attribute name to look up
     * @return value of attribute, otherwise {@code defaultValue}
     * @throws NavException navigation exception by VTD-XML
     */
    private static String readAttributeValue(final VTDNav vtdNav, final String attribute) throws NavException {
        final int index = vtdNav.getAttrVal(attribute);
        return index > 0 ? vtdNav.toString(index) : null;
    }
}
