# KNX Core Plugin: API

This plugin offers third-integration to other systems using RESTful API and
will start up a [Pippo micro web server](https://github.com/pippo-java/pippo) 
in background to serve the JSON requests.

## Endpoints

See [OpenAPI document @ swagger.io](https://petstore.swagger.io/?url=https://raw.githubusercontent.com/pitschr/knx-link/master/knx-core-plugins/api/knx-core-plugin-api.openapi.yaml) 
to find all endpoints with some examples.

## Configuration

### Path

Defines the port that should be used by Pippo micro web server.

**Type:** `IntegerConfigValue` \
**Default Value:** `8338` (default value from Pippo) \
**Config in Code:** `ApiPlugin.PORT`  \
**Config in File:** `plugin.config.ApiPlugin.port`
