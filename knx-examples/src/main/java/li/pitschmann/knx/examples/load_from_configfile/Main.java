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

package li.pitschmann.knx.examples.load_from_configfile;

import li.pitschmann.knx.examples.AbstractKnxMain;

import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Demo class how to establish connection based on configuration.
 *
 * @author PITSCHR
 */
public final class Main extends AbstractKnxMain {
    public static void main(final String[] args) throws URISyntaxException {
        new Main().startFromConfigFiles();
    }

    private void startFromConfigFiles() throws URISyntaxException {
        // Routing
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" R O U T I N G");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startFromConfigFile("routing.config");

        // Tunneling
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" T U N N E L I N G    ( D I S C O V E R Y )");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startFromConfigFile("discovery.config");

        // Tunneling (NAT Mode)
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" T U N N E L I N G    ( N A T - M O D E )");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startFromConfigFile("tunneling-and-nat.config");

        // Tunneling (endpoint defined)
        log.info("##########################################################################");
        log.info("##########################################################################");
        log.info(" T U N N E L I N G    ( E N D P O I N T )");
        log.info("##########################################################################");
        log.info("##########################################################################");
        startFromConfigFile("tunneling-endpoint.config");
    }

    private void startFromConfigFile(final String fileName) throws URISyntaxException {
        // start KNX communication
        log.trace("START");

        // take config file from resources/load_from_configfile/<fileName>
        final var configFilePath = Paths.get(ConfigFileUtil.class.getResource("/load_from_configfile/" + fileName).toURI());
        log.debug("Config File: {}", configFilePath);

        // build config from file
        final var config = ConfigFileUtil.loadFile(configFilePath).build();

        log.debug("Endpoint: {}:{}", config.getRemoteControlAddress(), config.getRemoteControlPort());
        log.debug("Discovery?: {}", config.getRemoteControlAddress().isAnyLocalAddress());
        log.debug("NAT?: {}", config.isNatEnabled());
        log.debug("Routing?: {}", config.isRoutingEnabled());

        // implement KNX Client ...
    }
}
