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
import li.pitschmann.knx.core.knxproj.XmlGroupAddressStyle;
import li.pitschmann.knx.core.knxproj.XmlProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.zip.ZipFile;

/**
 * Project Info Parser for KNXPROJ FILE
 *
 * @PITSCHR
 */
public final class ProjectInfoParser extends AbstractParser implements ParserStrategy {
    private static final Logger log = LoggerFactory.getLogger(ProjectInfoParser.class);

    @Override
    public void load(final XmlProject xmlProject, final ZipFile zipFile) throws XMLStreamException {
        final var bytes = extractBytes(zipFile, "^P-[\\dA-F]+/project\\.xml$");

        final var reader = createXmlEventReader(bytes);
        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                final var element = event.asStartElement();
                final var elementName = element.getName().getLocalPart();

                // project version
                if ("KNX".equals(elementName)) {
                    // find KNX Project version <KNX /> element and http://knx.org/xml/project/20 --> Version: 20
                    final var xmlNamespace = element.getNamespaceURI("");
                    log.debug("XML Project Namespace: {}", xmlNamespace);
                    final var version = Integer.parseInt(xmlNamespace.substring(xmlNamespace.lastIndexOf("/") + 1));
                    xmlProject.setVersion(version);
                }
                // project id
                else if ("Project".equals(elementName)) {
                    // go to <Project /> element and read @Id
                    xmlProject.setId(readAttributeValue(element, "Id",
                            () -> new KnxProjectParserException("Attribute <Project @Id /> not found.")));
                }
                // project name + group address style
                else if ("ProjectInformation".equals(elementName)) {
                    // go to <ProjectInformation /> element and read @Name and @GroupAddressStyle
                    xmlProject.setName(readAttributeValue(element, "Name",
                            () -> new KnxProjectParserException("Attribute <ProjectInformation @Name /> not found.")));

                    final var groupAddressStyleStr = readAttributeValue(element, "GroupAddressStyle",
                            () -> new KnxProjectParserException("Attribute <ProjectInformation @GroupAddressStyle /> not found."));
                    xmlProject.setGroupAddressStyle(XmlGroupAddressStyle.parse(groupAddressStyleStr));
                }
            }
        }
    }

}
