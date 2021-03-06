/*
 * Copyright (C) 2021 Pitschmann Christoph
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

package li.pitschmann.knx.core.header;

import li.pitschmann.knx.core.dib.ServiceTypeFamily;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxServiceTypeHasNoResponseIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link ServiceType} enum class
 *
 * @author PITSCHR
 */
final class ServiceTypeTest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(ServiceType.values()).hasSize(17);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(ServiceType.valueOf(0x0201)).isEqualTo(ServiceType.SEARCH_REQUEST);
        assertThat(ServiceType.valueOf(0x0202)).isEqualTo(ServiceType.SEARCH_RESPONSE);
        assertThat(ServiceType.valueOf(0x0203)).isEqualTo(ServiceType.DESCRIPTION_REQUEST);
        assertThat(ServiceType.valueOf(0x0204)).isEqualTo(ServiceType.DESCRIPTION_RESPONSE);
        assertThat(ServiceType.valueOf(0x0205)).isEqualTo(ServiceType.CONNECT_REQUEST);
        assertThat(ServiceType.valueOf(0x0206)).isEqualTo(ServiceType.CONNECT_RESPONSE);
        assertThat(ServiceType.valueOf(0x0207)).isEqualTo(ServiceType.CONNECTION_STATE_REQUEST);
        assertThat(ServiceType.valueOf(0x0208)).isEqualTo(ServiceType.CONNECTION_STATE_RESPONSE);
        assertThat(ServiceType.valueOf(0x0209)).isEqualTo(ServiceType.DISCONNECT_REQUEST);
        assertThat(ServiceType.valueOf(0x020A)).isEqualTo(ServiceType.DISCONNECT_RESPONSE);
        assertThat(ServiceType.valueOf(0x0310)).isEqualTo(ServiceType.DEVICE_CONFIGURATION_REQUEST);
        assertThat(ServiceType.valueOf(0x0311)).isEqualTo(ServiceType.DEVICE_CONFIGURATION_ACK);
        assertThat(ServiceType.valueOf(0x0420)).isEqualTo(ServiceType.TUNNELING_REQUEST);
        assertThat(ServiceType.valueOf(0x0421)).isEqualTo(ServiceType.TUNNELING_ACK);
        assertThat(ServiceType.valueOf(0x0530)).isEqualTo(ServiceType.ROUTING_INDICATION);
        assertThat(ServiceType.valueOf(0x0531)).isEqualTo(ServiceType.ROUTING_LOST_MESSAGE);
        assertThat(ServiceType.valueOf(0x0532)).isEqualTo(ServiceType.ROUTING_BUSY);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> ServiceType.valueOf(-1)).isInstanceOf(KnxEnumNotFoundException.class);
        assertThatThrownBy(() -> ServiceType.valueOf(0x200)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(ServiceType.SEARCH_REQUEST.getFriendlyName()).isEqualTo("Search Request");
        assertThat(ServiceType.SEARCH_RESPONSE.getFriendlyName()).isEqualTo("Search Response");
        assertThat(ServiceType.DESCRIPTION_REQUEST.getFriendlyName()).isEqualTo("Description Request");
        assertThat(ServiceType.DESCRIPTION_RESPONSE.getFriendlyName()).isEqualTo("Description Response");
        assertThat(ServiceType.CONNECT_REQUEST.getFriendlyName()).isEqualTo("Connect Request");
        assertThat(ServiceType.CONNECT_RESPONSE.getFriendlyName()).isEqualTo("Connect Response");
        assertThat(ServiceType.CONNECTION_STATE_REQUEST.getFriendlyName()).isEqualTo("Connection State Request");
        assertThat(ServiceType.CONNECTION_STATE_RESPONSE.getFriendlyName()).isEqualTo("Connection State Response");
        assertThat(ServiceType.DISCONNECT_REQUEST.getFriendlyName()).isEqualTo("Disconnect Request");
        assertThat(ServiceType.DISCONNECT_RESPONSE.getFriendlyName()).isEqualTo("Disconnect Response");
        assertThat(ServiceType.DEVICE_CONFIGURATION_REQUEST.getFriendlyName()).isEqualTo("Device Configuration Request");
        assertThat(ServiceType.DEVICE_CONFIGURATION_ACK.getFriendlyName()).isEqualTo("Device Configuration Acknowledge");
        assertThat(ServiceType.TUNNELING_REQUEST.getFriendlyName()).isEqualTo("Tunneling Request");
        assertThat(ServiceType.TUNNELING_ACK.getFriendlyName()).isEqualTo("Tunneling Acknowledgement");
        assertThat(ServiceType.ROUTING_INDICATION.getFriendlyName()).isEqualTo("Routing indication");
        assertThat(ServiceType.ROUTING_LOST_MESSAGE.getFriendlyName()).isEqualTo("Routing lost message");
        assertThat(ServiceType.ROUTING_BUSY.getFriendlyName()).isEqualTo("Routing busy message");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        // with response identifier
        assertThat(ServiceType.TUNNELING_REQUEST).hasToString(
                "ServiceType{name=TUNNELING_REQUEST, friendlyName=Tunneling Request, code=1056, family=TUNNELING, responseIdentifier=TUNNELING_ACK}"
        );

        // without response identifier
        assertThat(ServiceType.SEARCH_RESPONSE).hasToString(
                "ServiceType{name=SEARCH_RESPONSE, friendlyName=Search Response, code=514, family=CORE, responseIdentifier=}"
        );
    }

    @Test
    @DisplayName("Test #getCodeAsBytes()")
    void testGetCodeAsByte() {
        // 0x02 xx Core
        assertThat(ServiceType.SEARCH_REQUEST.getCodeAsBytes()).containsExactly(0x02, 0x01);
        assertThat(ServiceType.SEARCH_RESPONSE.getCodeAsBytes()).containsExactly(0x02, 0x02);
        assertThat(ServiceType.DESCRIPTION_REQUEST.getCodeAsBytes()).containsExactly(0x02, 0x03);
        assertThat(ServiceType.DESCRIPTION_RESPONSE.getCodeAsBytes()).containsExactly(0x02, 0x04);
        assertThat(ServiceType.CONNECT_REQUEST.getCodeAsBytes()).containsExactly(0x02, 0x05);
        assertThat(ServiceType.CONNECT_RESPONSE.getCodeAsBytes()).containsExactly(0x02, 0x06);
        assertThat(ServiceType.CONNECTION_STATE_REQUEST.getCodeAsBytes()).containsExactly(0x02, 0x07);
        assertThat(ServiceType.CONNECTION_STATE_RESPONSE.getCodeAsBytes()).containsExactly(0x02, 0x08);
        assertThat(ServiceType.DISCONNECT_REQUEST.getCodeAsBytes()).containsExactly(0x02, 0x09);
        assertThat(ServiceType.DISCONNECT_RESPONSE.getCodeAsBytes()).containsExactly(0x02, 0x0A);

        // 0x03 xx Device Management
        assertThat(ServiceType.DEVICE_CONFIGURATION_REQUEST.getCodeAsBytes()).containsExactly(0x03, 0x10);
        assertThat(ServiceType.DEVICE_CONFIGURATION_ACK.getCodeAsBytes()).containsExactly(0x03, 0x11);

        // 0x04 xx Tunneling
        assertThat(ServiceType.TUNNELING_REQUEST.getCodeAsBytes()).containsExactly(0x04, 0x20);
        assertThat(ServiceType.TUNNELING_ACK.getCodeAsBytes()).containsExactly(0x04, 0x21);

        // 0x05 xx Routing
        assertThat(ServiceType.ROUTING_INDICATION.getCodeAsBytes()).containsExactly(0x05, 0x30);
        assertThat(ServiceType.ROUTING_LOST_MESSAGE.getCodeAsBytes()).containsExactly(0x05, 0x31);
        assertThat(ServiceType.ROUTING_BUSY.getCodeAsBytes()).containsExactly(0x05, 0x32);
    }

    @Test
    @DisplayName("Test #getFamily()")
    void testGetFamily() {
        // Core
        assertThat(ServiceType.SEARCH_REQUEST.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceType.SEARCH_RESPONSE.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceType.DESCRIPTION_REQUEST.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceType.DESCRIPTION_RESPONSE.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceType.CONNECT_REQUEST.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceType.CONNECT_RESPONSE.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceType.CONNECTION_STATE_REQUEST.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceType.CONNECTION_STATE_RESPONSE.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceType.DISCONNECT_REQUEST.getFamily()).isEqualTo(ServiceTypeFamily.CORE);
        assertThat(ServiceType.DISCONNECT_RESPONSE.getFamily()).isEqualTo(ServiceTypeFamily.CORE);

        // Device Management
        assertThat(ServiceType.DEVICE_CONFIGURATION_REQUEST.getFamily()).isEqualTo(ServiceTypeFamily.DEVICE_MANAGEMENT);
        assertThat(ServiceType.DEVICE_CONFIGURATION_ACK.getFamily()).isEqualTo(ServiceTypeFamily.DEVICE_MANAGEMENT);

        // Tunneling
        assertThat(ServiceType.TUNNELING_REQUEST.getFamily()).isEqualTo(ServiceTypeFamily.TUNNELING);
        assertThat(ServiceType.TUNNELING_ACK.getFamily()).isEqualTo(ServiceTypeFamily.TUNNELING);

        // Routing
        assertThat(ServiceType.ROUTING_INDICATION.getFamily()).isEqualTo(ServiceTypeFamily.ROUTING);
        assertThat(ServiceType.ROUTING_LOST_MESSAGE.getFamily()).isEqualTo(ServiceTypeFamily.ROUTING);
        assertThat(ServiceType.ROUTING_BUSY.getFamily()).isEqualTo(ServiceTypeFamily.ROUTING);
    }

    @Test
    @DisplayName("Test #hasResponseIdentifier()")
    void testHasResponseIdentifier() {
        // has response identifier
        assertThat(ServiceType.SEARCH_REQUEST.hasResponseIdentifier()).isTrue();
        assertThat(ServiceType.DESCRIPTION_REQUEST.hasResponseIdentifier()).isTrue();
        assertThat(ServiceType.CONNECT_REQUEST.hasResponseIdentifier()).isTrue();
        assertThat(ServiceType.CONNECTION_STATE_REQUEST.hasResponseIdentifier()).isTrue();
        assertThat(ServiceType.DISCONNECT_REQUEST.hasResponseIdentifier()).isTrue();
        assertThat(ServiceType.DEVICE_CONFIGURATION_REQUEST.hasResponseIdentifier()).isTrue();
        assertThat(ServiceType.TUNNELING_REQUEST.hasResponseIdentifier()).isTrue();

        // no response identifier
        assertThat(ServiceType.SEARCH_RESPONSE.hasResponseIdentifier()).isFalse();
        assertThat(ServiceType.DESCRIPTION_RESPONSE.hasResponseIdentifier()).isFalse();
        assertThat(ServiceType.CONNECT_RESPONSE.hasResponseIdentifier()).isFalse();
        assertThat(ServiceType.CONNECTION_STATE_RESPONSE.hasResponseIdentifier()).isFalse();
        assertThat(ServiceType.DISCONNECT_RESPONSE.hasResponseIdentifier()).isFalse();
        assertThat(ServiceType.DEVICE_CONFIGURATION_ACK.hasResponseIdentifier()).isFalse();
        assertThat(ServiceType.TUNNELING_ACK.hasResponseIdentifier()).isFalse();
        assertThat(ServiceType.ROUTING_INDICATION.hasResponseIdentifier()).isFalse();
        assertThat(ServiceType.ROUTING_LOST_MESSAGE.hasResponseIdentifier()).isFalse();
        assertThat(ServiceType.ROUTING_BUSY.hasResponseIdentifier()).isFalse();
    }

    @Test
    @DisplayName("Test #getResponseIdentifier()")
    void testGetResponseIdentifier() {
        // requests with response identifiers
        assertThat(ServiceType.SEARCH_REQUEST.getResponseIdentifier()).isEqualTo(ServiceType.SEARCH_RESPONSE);
        assertThat(ServiceType.DESCRIPTION_REQUEST.getResponseIdentifier()).isEqualTo(ServiceType.DESCRIPTION_RESPONSE);
        assertThat(ServiceType.CONNECT_REQUEST.getResponseIdentifier()).isEqualTo(ServiceType.CONNECT_RESPONSE);
        assertThat(ServiceType.CONNECTION_STATE_REQUEST.getResponseIdentifier()).isEqualTo(ServiceType.CONNECTION_STATE_RESPONSE);
        assertThat(ServiceType.DISCONNECT_REQUEST.getResponseIdentifier()).isEqualTo(ServiceType.DISCONNECT_RESPONSE);
        assertThat(ServiceType.DEVICE_CONFIGURATION_REQUEST.getResponseIdentifier()).isEqualTo(ServiceType.DEVICE_CONFIGURATION_ACK);
        assertThat(ServiceType.TUNNELING_REQUEST.getResponseIdentifier()).isEqualTo(ServiceType.TUNNELING_ACK);

        // response itself
        assertThatThrownBy(() -> ServiceType.SEARCH_RESPONSE.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);
        assertThatThrownBy(() -> ServiceType.DESCRIPTION_RESPONSE.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);
        assertThatThrownBy(() -> ServiceType.CONNECT_RESPONSE.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);
        assertThatThrownBy(() -> ServiceType.CONNECTION_STATE_RESPONSE.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);
        assertThatThrownBy(() -> ServiceType.DISCONNECT_RESPONSE.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);
        assertThatThrownBy(() -> ServiceType.DEVICE_CONFIGURATION_ACK.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);
        assertThatThrownBy(() -> ServiceType.TUNNELING_ACK.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);

        // services without response identifier
        assertThatThrownBy(() -> ServiceType.ROUTING_INDICATION.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);
        assertThatThrownBy(() -> ServiceType.ROUTING_LOST_MESSAGE.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);
        assertThatThrownBy(() -> ServiceType.ROUTING_BUSY.getResponseIdentifier())
                .isInstanceOf(KnxServiceTypeHasNoResponseIdentifier.class);
    }
}
