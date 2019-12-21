package li.pitschmann.knx.core.knxproj.parser;

import li.pitschmann.knx.core.knxproj.XmlProject;

import javax.xml.stream.XMLStreamException;
import java.util.zip.ZipFile;

/**
 * Strategy for Project Data Parsers
 *
 * @author PITSCHR
 */
public interface ParserStrategy {

    /**
     * Loads project specific for given {@link XmlProject} and from {@link ZipFile}
     *
     * @param xmlProject the XML project instance
     * @param zipFile    zip file of KNX project file to be parsed
     * @throws XMLStreamException If there was a XML stream error
     */
    void load(final XmlProject xmlProject, final ZipFile zipFile) throws XMLStreamException;

}
