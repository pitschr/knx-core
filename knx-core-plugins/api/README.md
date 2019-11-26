# KNX Core Plugin: API

This plugin offers third-integration to other systems using RESTful API and
will start up a [Pippo micro web server](https://github.com/pippo-java/pippo) 
in background to serve the JSON requests.

## Available Endpoints

// TODO

## Configuration

### Path

Defines the port that should be used by Pippo micro web server.

**Type:** `IntegerConfigValue` \
**Default Value:** `8338` (default value from Pippo) \
**Config in Code:** `HttpDaemonPlugin.PORT`  \
**Config in File:** `plugin.config.HttpDaemonPlugin.port`
