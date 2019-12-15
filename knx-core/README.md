# KNX Core

KNX Core is the minimum library that is need to communicate with your KNX Net/IP device.

## Core Development

This tutorial is written for experienced Java developers and explains how to integrate the 
KNX client with your own application. It looks like:

```
// Creates KNX client and start communication with your KNX Net/IP.
// The closure of KNX communication will be handled automatically by KNX client.
try (final var client = DefaultKnxClient.createStarted("address:port")) {
    // do your stuff you want to do ...
} catch (final Throwable t) {
    // catch all throwable you want to handle here (optional)
} finally {
    // do final actions (optional)
}
```

#### Example: Switch on/off lamp with boolean values

Let's start with an easy sample: You want to switch `on` a lamp. The KNX actuator listens 
on group address `1/2/110` which is configured for switching on/off a lamp. 

```java
public final class LampOnExample {
   public static void main(final String[] args) {
        // this is the group address where the KNX actuator listens to switch on/off a lamp
        final var groupAddress = GroupAddress.of(1, 2, 110);

        // create KNX client and connect to KNX Net/IP device using auto-discovery
        try (final var client = DefaultKnxClient.createStarted()) {
            // switch on the lamp (boolean: true) --> translated to '0x01' and sent to KNX Net/IP device
            client.writeRequest(groupAddress, DPT1.SWITCH.toValue(true));  // or DPT1.SWITCH.toValue((byte)0x01)
                                                                           // or DPT1.SWITCH.toValue("on")
        }

        // auto-closed and disconnected by KNX client
    }
}
```

#### Example: Inverse the lamp status

Given sample, you want to inverse the status of your lamp: 
* if the lamp is `on`, the lamp should be `off`
* if the lamp is `off`, the lamp should be `on`

To get the most recent status of lamp, we need this information from the KNX actuator. As
per KNX Project Design Guidelines we have multiple KNX group addresses, here in our example
the group address `1/2/110` is responsible for switching on/off the lamp (=write). The group address
`1/2/113` is used for status feedback of the lamp (=read). 

```java
public final class LampInverseExample {
    public static void main(final String[] args) {
        // this is the group address where the KNX actuator returns the status of lamp
        final var readGroupAddress = GroupAddress.of(1, 2, 113);

        // this is the group address where the KNX actuator listens to switch on/off the lamp
        final var writeGroupAddress = GroupAddress.of(1, 2, 110);

        // create KNX client and connect to KNX Net/IP device using auto-discovery
        try (final var client = DefaultKnxClient.createStarted()) {
            // send a 'read' request to KNX
            client.readRequest(readGroupAddress);

            // wait a bit (usually few milliseconds, but up to 1 second maximum)
            // KNX actuator will send a response to the KNX client with actual lamp status
            final var lampStatus = client.getStatusPool().getValue(readGroupAddress, DPT1.SWITCH).getBooleanValue();

            // lamp status will be inverted (on -> off / off -> on)
            final var lampStatusInverted = !lampStatus;

            // send a 'write' request to KNX
            client.writeRequest(writeGroupAddress, DPT1.SWITCH.toValue(lampStatusInverted));
        }

        // auto-closed and disconnected by KNX client
    }
}
```

#### Example: Work with Data Point Type

Given snippet, we want to convert a date and time objects into KNX compatible byte array:

```
// Saturday, 2013-08-17 04:10:45
final var dayOfWeek = DayOfWeek.SATURDAY;
final var date = LocalDate.of(2013, 8, 17);
final var time = LocalTime.of(04, 10, 45);

DPT19.DATE_TIME.toByteArray(dayOfWeek, date, time);
```

This can be also more simplified using direct string representation:
```
DPT19.DATE_TIME.toByteArray("Saturday", "2013-08-17", "04:10:45");
```

## Core Configuration

The default configuration settings used by KNX Client are as per KNX Specification 
but gives you some freedom to adjust some configurations. One example might be to 
alter the KNX port which differs from the officially registered KNX port `3671` 
(at IANA). Another example might be to increase the timeout of a specific KNX packet.

All KNX Core configurations can be found in: [CoreConfigs.java](src/main/java/li/pitschmann/knx/core/config/CoreConfigs.java) 

| Name | Type | Default Value | Description |
| ---- | ---- | ------------- | ----------- |
| **Search** |
| Request Timeout | `Long` | `10000` milliseconds | Timeout for *search requests* to find KNX Net/IP device in your network using multicast. <br><br> **Communication:** Tunneling (Discovery Channel, multicast)<br> **Config Field:** `CoreConfigs.Search.REQUEST_TIMEOUT` <br> **Config Key:** `client.communication.search.requestTimeout` |
| **Description** |
| Request Timeout | `Long` | `10000` milliseconds | Timeout for *description requests* to fetch device information about KNX Net/IP device. <br><br> **Communication:** Tunneling (Description Channel) <br> **Config Field:** `CoreConfigs.Description.REQUEST_TIMEOUT` <br> **Config Key:** `client.communication.description.requestTimeout` |
| Local Port | `Integer` | `0` (=random free port) | Port that should be bound by KNX Client on local machine. <br><br> **Communication:** Tunneling (Description Channel) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Description.PORT` <br> **Config Key:** `client.communication.description.port` |
| Socket Timeout | `Long` | `3000` milliseconds | Timeout for description channel socket. <br><br> **Communication:** Tunneling (Description Channel) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Description.SOCKET_TIMEOUT` <br> **Config Key:** `client.communication.description.socketTimeout` |
| **Connect** |
| Request Timeout | `Long` | `10000` milliseconds | Timeout for *connect requests* to establish a *tunneling* connection to KNX Net/IP device. <br><br> **Communication:** Tunneling (Control Channel) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Connect.REQUEST_TIMEOUT` <br> **Config Key:** `client.communication.connect.requestTimeout` |
| **Disconnect** |
| Request Timeout | `Long` | `10000` milliseconds | Timeout for *disconnect requests* sent to KNX Net/IP device. <br><br> **Communication:** Tunneling (Control Channel) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Disconnect.REQUEST_TIMEOUT` <br> **Config Key:** `client.communication.disconnect.requestTimeout` |
| Response Timeout | `Long` | `1000` milliseconds | Timeout for *disconnect response* from KNX Net/IP device after disconnect request. <br><br> **Communication:** Tunneling (Control Channel) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Disconnect.RESPONSE_TIMEOUT` <br> **Config Key:** `client.communication.disconnect.responseTimeout` |
| **Connection State** |
| Request Timeout | `Long` | `10000` milliseconds | Timeout for *connection state requests* for request if KNX Net/IP device is still reachable. <br><br> **Communication:** Tunneling (Control Channel) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.ConnectionState.REQUEST_TIMEOUT` <br> **Config Key:** `client.communication.connectionState.requestTimeout` |
| Heartbeat Interval | `Long` | `60000` milliseconds | Interval when *connection state requests* should be sent to KNX Net/IP device. <br><br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.ConnectionState.HEARTBEAT_INTERVAL` <br> **Config Key:** `client.communication.connectionState.heartbeatInterval` |
| Heartbeat Timeout | `Long` | `120000` milliseconds | Ultimate timeout when disconnect sequence should be initiated by KNX Client when no *connection state response* was received. <br><br> **Communication:** Tunneling (Control Channel) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.ConnectionState.HEARTBEAT_TIMEOUT` <br> **Config Key:** `client.communication.connectionState.heartbeatTimeout` |
| **Control Channel**
| Local Port | `Integer` | `0` (=random free port) | Port that should be bound by KNX Client on local machine for all control channel related packets. <br><br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Control.PORT` <br> **Config Key:** `client.communication.control.port` |
| Socket Timeout | `Long` | `3000` milliseconds | Timeout for control channel socket <br><br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Control.SOCKET_TIMEOUT` <br> **Config Key:** `client.communication.control.socketTimeout` |
| **Tunneling** |
| Request Timeout | `Long` | `1000` milliseconds | Timeout for *tunneling requests* sent to KNX Net/IP device. <br><br> **Communication:** Tunneling (Data Channel) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Tunneling.REQUEST_TIMEOUT` <br> **Config Key:** `client.communication.tunneling.requestTimeout` |
| **Data Channel**
| Local Port | `Integer` | `0` (=random free port) | Port that should be bound by KNX Client on local machine for all data channel related packets. <br><br> **Communication:** Tunneling <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Data.PORT` <br> **Config Key:** `client.communication.data.port` |
| Socket Timeout | `Long` | `3000` milliseconds | Timeout for data channel socket. <br><br> **Communication:** Tunneling <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Data.SOCKET_TIMEOUT` <br> **Config Key:** `client.communication.data.socketTimeout` |
| **Remote Endpoint** |
| Address | `InetAddress` | `unbound` (uses discovery) | Remote IP Address of KNX Net/IP device. <br><br> **Communication:** Tunneling (Control and Data Channel) <br> **Config Field:** `not available` <br> **Config Key:** `client.endpoint.address` |
| Port | `Integer` | `3671` | Port of KNX Net/IP device to accept KNX requests. <br><br> **Communication:** Tunneling (Control and Data Channel) <br> **Config Field:** `not available` <br> **Config Key:** `client.endpoint.port` |
| **Multicast** |
| Address | `InetAddress` | `224.0.23.12` | Multicast Address that may be joined by KNX Net/IP device. <br><br> **Communication:** Routing, Tunneling (Search only) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Multicast.ADDRESS` <br> **Config Key:** `client.communication.multicast.address` |
| Port | `Integer` | `3671` | Port of Multicast that may be joined by KNX Net/IP device. <br><br> **Communication:** Routing, Tunneling (Search only) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Multicast.PORT` <br> **Config Key:** `client.communication.multicast.port` |
| Time-To-Live (TTL) | `Integer` | `4` hops | Defines the range over which a multicast packet is propagated in your intranet. <br><br> **Communication:** Routing, Tunneling (Search only) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Multicast.TIME_TO_LIVE` <br> **Config Key:** `client.communication.multicast.timeToLive` |
| Socket Timeout | `Long` | `3000` milliseconds | Timeout for multicast channel socket. <br><br> **Communication:** Routing, Tunneling (Search only) <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Multicast.SOCKET_TIMEOUT` <br> **Config Key:** `client.communication.multicast.socketTimeout` |
| **Communication** |
| Executor Pool Size | `Integer` |`10` | The size of communicator thread pool size that may send packets in parallel. <br><br> **Communication:** Routing, Tunneling <br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Communication.EXECUTOR_POOL_SIZE` <br> **Config Key:** `client.communication.executorPoolSize` |
| **Plugin** |
| Executor Pool Size | `Integer` | `10` | The size of plugin thread pool size that may notify plugins in parallel. <br><br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Plugin.EXECUTOR_POOL_SIZE` <br> **Config Key:** `client.plugin.executorPoolSize` |
| Initialization Timeout | `Long` | `10000` milliseconds | Timeout how long a plugin may take for initialization before it is rejected by the plugin manager <br><br> **Config Field:** `li.pitschmann.knx.core.config.CoreConfigs.Plugin.INITIALIZATION_TIMEOUT` <br> **Config Key:** `client.plugin.initializationTimeout` |
