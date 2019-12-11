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

package li.pitschmann.knx.core.plugin.api;

import com.google.gson.JsonParser;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.plugin.api.gson.ApiGsonEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test Utility
 */
public final class TestUtils {
    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Returns the given response object as JSON
     *
     * @param response
     * @return json
     */
    public static final String asJson(final Object response) {
        return ApiGsonEngine.INSTANCE.toString(response);
    }

    /**
     * Reads the given test resource on {@code filePath} and returns the content
     * as an UTF-8 compliant String representation.
     *
     * @param filePath
     * @return content (UTF-8 decoded)
     */
    public static String readJsonFile(final String filePath) {
        log.debug("File: {}", filePath);
        try {
            final var path = Paths.get(TestUtils.class.getResource(filePath).toURI());
            if (Files.isReadable(path)) {
                final var content = Files.readString(path);
                log.debug("Content of file '{}': {}", filePath, content);
                // minify json
                return new JsonParser().parse(content).toString();
            }
            throw new AssertionError("File not found or cannot be read: " + filePath);
        } catch (final URISyntaxException | IOException ex) {
            fail(ex);
            throw new AssertionError(ex);
        }
    }

    /**
     * Randomize a {@link GroupAddress}
     * <p>
     * The group address should not matter in the unit testing when
     * not asserting itself.
     *
     * @return a randomized {@link GroupAddress}
     */
    public static GroupAddress randomGroupAddress() {
        // a range between between 1 and 65535
        int randomInt = new Random().nextInt(65534) + 1;
        return GroupAddress.of(randomInt);
    }
}
