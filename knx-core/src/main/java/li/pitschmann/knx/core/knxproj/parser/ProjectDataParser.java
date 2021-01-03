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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.LinkedList;
import java.util.zip.ZipFile;

/**
 * Project Data Parser for KNXPROJ file
 *
 * @PITSCHR
 */
final class ProjectDataParser extends AbstractParser implements ParserStrategy {

    @Override
    public void load(final XmlProject xmlProject, final ZipFile zipFile) throws XMLStreamException {
        final var xmlGroupRanges = new LinkedList<XmlGroupRange>();
        final var xmlGroupAddresses = new LinkedList<XmlGroupAddress>();

        // memory for GroupRange parent ids
        // 0 = main
        // 1 = middle
        // 2 = sub
        int currentGroupRangeLevel = 0;
        final var groupRangeParentId = new String[3];

        final var reader = createXmlEventReader(extractBytes(zipFile, "^P-[\\dA-F]+/0\\.xml$"));
        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                final var element = event.asStartElement();
                final var elementName = element.getName().getLocalPart();

                // Group Ranges
                if ("GroupRange".equals(elementName)) {
                    final var xmlGroupRange = new XmlGroupRange();

                    // obtain required @Id, @RangeStart, @RangeEnd and @Name
                    xmlGroupRange.setId(readAttributeValue(element, "Id",
                            () -> new KnxProjectParserException("Attribute <GroupRange @Id /> not found.")));

                    xmlGroupRange.setRangeStart(Integer.parseInt(readAttributeValue(element, "RangeStart",
                            () -> new KnxProjectParserException("Attribute <GroupRange @RangeStart /> not found for: " + xmlGroupRange.getId()))));

                    xmlGroupRange.setRangeEnd(Integer.parseInt(readAttributeValue(element, "RangeEnd",
                            () -> new KnxProjectParserException("Attribute <GroupRange @RangeEnd /> not found for: " + xmlGroupRange.getId()))));

                    xmlGroupRange.setName(readAttributeValue(element, "Name",
                            () -> new KnxProjectParserException("Attribute <GroupRange @Name /> not found for: " + xmlGroupRange.getId())));

                    // -1 because we don't want to count the <GroupRanges />
                    xmlGroupRange.setLevel(currentGroupRangeLevel);

                    // store parent id for later usage + get parent id
                    groupRangeParentId[currentGroupRangeLevel] = xmlGroupRange.getId();
                    if (currentGroupRangeLevel == 0) {
                        xmlGroupRange.setParentId(null);
                    } else {
                        xmlGroupRange.setParentId(groupRangeParentId[currentGroupRangeLevel - 1]);
                    }

                    xmlGroupRanges.add(xmlGroupRange);
                    currentGroupRangeLevel++;
                }
                // Group Addresses
                else if ("GroupAddress".equals(elementName)) {
                    final var xmlGroupAddress = new XmlGroupAddress();

                    // obtain required @Id, @Address, @Name
                    xmlGroupAddress.setId(readAttributeValue(element, "Id",
                            () -> new KnxProjectParserException("Attribute <GroupAddress @Id /> not found.")));

                    xmlGroupAddress.setAddress(readAttributeValue(element, "Address",
                            () -> new KnxProjectParserException("Attribute <GroupAddress @Address /> not found for: " + xmlGroupAddress.getId())));

                    xmlGroupAddress.setName(readAttributeValue(element, "Name",
                            () -> new KnxProjectParserException("Attribute <GroupAddress @Name /> not found for: " + xmlGroupAddress.getId())));

                    // obtain optional @Description and @DatapointType
                    xmlGroupAddress.setDescription(readAttributeValue(element, "Description"));
                    xmlGroupAddress.setDataPointType(readAttributeValue(element, "DatapointType"));

                    // set the parent id from GroupRange
                    xmlGroupAddress.setParentId(groupRangeParentId[currentGroupRangeLevel - 1]);

                    xmlGroupAddresses.add(xmlGroupAddress);
                }
            } else if (event.isEndElement()) {
                // Group Ranges
                if ("GroupRange".equals(event.asEndElement().getName().getLocalPart())) {
                    currentGroupRangeLevel--;
                }
            }
        }

        xmlProject.setGroupRanges(xmlGroupRanges);
        xmlProject.setGroupAddresses(xmlGroupAddresses);
    }

}
