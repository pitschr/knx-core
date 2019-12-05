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

import li.pitschmann.knx.core.body.address.GroupAddress;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.config.ConfigValue;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.datapoint.DPT1;
import li.pitschmann.knx.core.plugin.api.gson.ApiGsonEngine;
import li.pitschmann.knx.core.plugin.api.test.MockApiPlugin;
import li.pitschmann.knx.core.plugin.api.test.MockApiTest;
import li.pitschmann.knx.core.plugin.api.v1.json.ReadRequest;
import li.pitschmann.knx.core.plugin.api.v1.json.WriteRequest;
import li.pitschmann.knx.core.test.MockServerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.pippo.core.HttpConstants;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

import static li.pitschmann.knx.core.plugin.api.test.TestUtils.readJsonFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ApiPlugin}
 */
public class ApiPluginTest {
    @Test
    @DisplayName("Test the API Plugin with default values")
    public void testApiPluginDefault() {
        final var mockApiPlugin = new MockApiPlugin();

        final var knxClientMock = mock(KnxClient.class);
        when(knxClientMock.getConfig(any(ConfigValue.class))).thenCallRealMethod();
        when(knxClientMock.getConfig(eq(CoreConfigs.PROJECT_PATH))).thenReturn(Paths.get("src/test/resources/Project (3-Level, v20).knxproj"));

        // simulate plugin
        try {
            mockApiPlugin.onInitialization(knxClientMock);
            mockApiPlugin.onStart();
        } finally {
            mockApiPlugin.onShutdown();
        }
    }
}
