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
import li.pitschmann.knx.core.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.zip.ZipFile;

/**
 * Abstract StAX Parser with most basic functionality
 *
 * @author PITSCHR
 */
abstract class AbstractParser {
    private static final Logger log = LoggerFactory.getLogger(AbstractParser.class);

    /**
     * Returns byte array of {@code filePathRegExp} file inside the {@link ZipFile}.
     * The byte array are encoded with {@link StandardCharsets#UTF_8}.
     *
     * @param zipFile
     * @param filePathRegExp
     * @return byte array, file content
     */
    protected byte[] extractBytes(final ZipFile zipFile,
                                  final String filePathRegExp) {
        Preconditions.checkNonNull(zipFile);
        Preconditions.checkNonNull(filePathRegExp);

        // find file that matches the regular expression in ZIP file
        final var zipEntry = zipFile.stream().filter(f -> f.getName().matches(filePathRegExp))
                .findFirst()
                .orElseThrow(() -> new KnxProjectParserException("File '" + filePathRegExp + "' not found in ZIP file"));
        log.debug("File in ZIP File found: {}", zipEntry.getName());

        byte[] bytes;
        try (final var in = zipFile.getInputStream(zipEntry)) {
            bytes = in.readAllBytes();
            if (log.isDebugEnabled()) {
                log.debug("Data stream from file '{}':\n{}", zipEntry.getName(), new String(bytes, StandardCharsets.UTF_8));
            }

            return bytes;
        } catch (final IOException ex) {
            log.error("Could not read the file: '{}'", zipEntry.getName());
            throw new KnxProjectParserException("Could not read the file: " + zipEntry.getName(), ex);
        }

    }

    /**
     * Creates a new instance of {@link XMLEventReader} with {@code bytes} as input stream.
     *
     * @param bytes
     * @return new instance of {@link XMLEventReader}
     */
    protected XMLEventReader createXmlEventReader(final byte[] bytes) {
        try {
            return XMLInputFactory.newInstance().createXMLEventReader(new ByteArrayInputStream(bytes));
        } catch (final XMLStreamException ex) {
            throw new KnxProjectParserException("Could not create XMLEventReader based on bytes: " +
                    Arrays.toString(bytes), ex);
        }
    }

    /**
     * Returns value of required attribute
     *
     * @param element   XML {@link StartElement}
     * @param attribute attribute name to look up
     * @param throwable exception to thrown in case the attribute doesn't exists
     * @return value of attribute, otherwise {@link KnxProjectParserException} from supplier
     */
    protected String readAttributeValue(final StartElement element,
                                        final String attribute,
                                        final Supplier<KnxProjectParserException> throwable) {
        final var value = readAttributeValue(element, attribute);
        if (value == null) {
            throw throwable.get();
        }
        return value;
    }

    /**
     * Returns value of optional attribute
     *
     * @param element   XML {@link StartElement}
     * @param attribute attribute name to look up
     * @return value of attribute, otherwise {@code null}
     */
    @Nullable
    protected String readAttributeValue(final StartElement element,
                                        final String attribute) {
        return readAttributeValue(element, attribute, (String) null);
    }

    /**
     * Returns value of optional attribute
     *
     * @param element   XML {@link StartElement}
     * @param attribute attribute name to look up
     * @return value of attribute, otherwise {@code defaultValue}
     */
    @Nullable
    protected String readAttributeValue(final StartElement element,
                                        final String attribute,
                                        final @Nullable String defaultValue) {
        final var xmlAttribute = element.getAttributeByName(new QName(attribute));
        return xmlAttribute == null ? defaultValue : xmlAttribute.getValue();
    }

}
