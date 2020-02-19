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

import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.config.Config;
import li.pitschmann.knx.core.knxproj.XmlGroupAddressStyle;
import li.pitschmann.knx.core.knxproj.XmlProject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.pippo.core.HttpConstants;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ApiPlugin}
 */
public class ApiPluginTest {

    @Test
    @DisplayName("Test the API Plugin life-cycle (with health check)")
    public void testApiPluginDefault() throws IOException, InterruptedException {
        final var mockApiPlugin = new TestApiPlugin();

        //
        // Mocking
        //

        final var knxClientMock = mock(KnxClient.class);
        final var configMock = mock(Config.class);
        final var projectMock = mock(XmlProject.class);
        when(knxClientMock.getConfig()).thenReturn(configMock);
        when(knxClientMock.getConfig(ApiPlugin.PORT)).thenReturn(4711);
        when(projectMock.getGroupAddressStyle()).thenReturn(XmlGroupAddressStyle.FREE_LEVEL);
        when(projectMock.getGroupRanges()).thenReturn(List.of());
        when(projectMock.getGroupAddresses()).thenReturn(List.of());
        when(configMock.getProject()).thenReturn(projectMock);

        //
        // Verification
        //
        try {
            mockApiPlugin.onInitialization(knxClientMock);
            mockApiPlugin.onStart();

            // verify if plugin could be started up
            assertThat(mockApiPlugin.isReady()).isTrue();
            assertThat(mockApiPlugin.getPort()).isNotZero();

            // verify if health check works
            final var httpRequest = mockApiPlugin.newRequestBuilder("/api/ping").build();
            final var httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertThat(httpResponse.statusCode()).isEqualTo(HttpConstants.StatusCode.OK);
            assertThat(httpResponse.body()).isEqualTo("OK");
            assertThat(httpResponse.headers().firstValue("Content-Type").get()).isEqualTo("text/plain; charset=UTF-8");

            // verify if request for project overview returns something
            final var httpRequest2 = mockApiPlugin.newRequestBuilder("/api/v1/project").build();
            final var httpResponse2 = HttpClient.newHttpClient().send(httpRequest2, HttpResponse.BodyHandlers.ofString());
            assertThat(httpResponse2.statusCode()).isEqualTo(HttpConstants.StatusCode.OK);
            assertThat(httpResponse2.body()).isNotEmpty();
        } finally {
            mockApiPlugin.onShutdown();
        }

        assertThat(mockApiPlugin.isReady()).isFalse();
    }
}
