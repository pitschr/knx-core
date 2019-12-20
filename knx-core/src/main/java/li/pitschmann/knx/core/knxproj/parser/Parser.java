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

package li.pitschmann.knx.core.knxproj.parser;

import li.pitschmann.knx.core.exceptions.KnxProjectParserException;
import li.pitschmann.knx.core.knxproj.XmlGroupAddress;
import li.pitschmann.knx.core.knxproj.XmlGroupRange;
import li.pitschmann.knx.core.knxproj.XmlProject;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipFile;

/**
 * KNX Project (*.knxproj) Parser for
 *
 * <ul>
 *      <li>http://knx.org/xml/project/14</li>
 *      <li>http://knx.org/xml/project/15</li>
 *      <li>http://knx.org/xml/project/16</li>
 *      <li>http://knx.org/xml/project/17</li>
 *      <li>http://knx.org/xml/project/18</li>
 *      <li>http://knx.org/xml/project/20</li>
 * </ul>
 *
 * @author PITSCHR
 */
public final class Parser extends AbstractParser {
    private static final Logger log = LoggerFactory.getLogger(Parser.class);
    private static final String FILE_EXTENSION = ".knxproj";

    private Parser() {
        throw new AssertionError("Don't touch me!");
    }

    /**
     * Creates a new {@link XmlProject} parsed from given {@link Path}
     *
     * @param path
     * @return new instance of {@link XmlProject}
     */
    public static XmlProject asXmlProject(final Path path) {
        Preconditions.checkArgument(Files.isReadable(path),
                "File '{}' doesn't exists or is not readable.", path);
        Preconditions.checkArgument(path.toString().toLowerCase().endsWith(FILE_EXTENSION),
                "Only '{}' is supported.", FILE_EXTENSION);

        log.debug("File '{}' to be loaded.", path);

        final var sw = Stopwatch.createStarted();
        final XmlProject xmlProject = new XmlProject();
        try (final var zipFile = new ZipFile(path.toFile())) {
            // get KNX project information
            // reads the 'project.xml' file from 'P-<digit>' folder project overview data
            final ParserStrategy infoParser = new ProjectInfoParser();
            infoParser.load(xmlProject, zipFile);

            if (xmlProject.getVersion() < 14) {
                throw new UnsupportedOperationException("Project Version is not supported: " + xmlProject.getVersion());
            }

            // get KNX project data (e.g. group ranges and addresses) based on strategy
            // reads the '0.xml' file from 'P-<digit>' folder which contains project detail data
            final ParserStrategy dataParser = new ProjectDataParser();
            dataParser.load(xmlProject, zipFile);

            // links the KNX group ranges with KNX group addresses
            linkGroupRanges(xmlProject);
            linkGroupAddresses(xmlProject);
        } catch (final IOException | XMLStreamException ex) {
            throw new KnxProjectParserException("Something went wrong during parsing the zip file: " + path);
        }
        log.info("KNX Project '{}' parse took {} ms:\n{}", path, sw.elapsed(TimeUnit.MILLISECONDS), xmlProject);

        return xmlProject;
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

}
