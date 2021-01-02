package li.pitschmann.knx.core.knxproj.parser;

import li.pitschmann.knx.core.exceptions.KnxProjectParserException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link AbstractParser}
 */
class AbstractParserTest {
    @Test
    @DisplayName("Test #extractBytes(ZipFile, String) with IOException")
    void testExtractBytes() throws IOException {
        final var abstractParser = new AbstractParser() {
        };

        final var zipEntryMock = mock(ZipEntry.class);
        when(zipEntryMock.getName()).thenReturn("foobar");
        final var zipFileMock = mock(ZipFile.class);
        doReturn(Stream.of(zipEntryMock)).when(zipFileMock).stream();
        when(zipFileMock.getInputStream(any(ZipEntry.class))).thenThrow(IOException.class);

        Assertions.assertThatThrownBy(() -> abstractParser.extractBytes(zipFileMock, "foobar"))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Could not read the file: foobar");
    }
}
