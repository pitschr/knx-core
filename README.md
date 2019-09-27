[![Build Status](https://travis-ci.org/pitschr/knx-link.svg?branch=master)](https://travis-ci.org/pitschr/knx-link)
[![Coverage Status](https://coveralls.io/repos/github/pitschr/knx-link/badge.svg?branch=master)](https://coveralls.io/github/pitschr/knx-link?branch=master)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

# KNX Link

A reactive, non-blocking Java library for KNX Net/IP communication.

This library is indented to allow you writing your own application communicating with your KNX Net/IP device 
(either a router or an interface). It also contains few main classes for a basic understanding how the KNX 
communication can be done in programmatically way.

This project is more intended for developers which are interested in communication with KNX world. 
See examples below to get a quick start using read-to-use classes.

### Known limitations

* No KNX Secure implementation yet (because I do not have a KNX router that supports KNX secure)
* No Additional Info support in CEMI frame yet
 
### Prerequisites

* **Java 11+**
  * Make sure that you have Java 11 installed and running as Java 11
* **Tunneling: KNX Router or Interface**
  * One free tunneling connection available
  * IP-Address (optional, if not provided - the auto-discovery service will be used)
* **Routing: KNX Router (only)**
  * Filter table on your KNX router device is properly configured
  * IP-Multicast Address that is used by your KNX router (default: 224.0.23.12)

### Architecture

![Architecture](./assets/readme_architecture.png)

The communication between KNX and the client is reactive and non-blocking which allows a very fast communication 
and we may have multiple channels simultaneously open: 
* Discovery Channel (for discovery service; multicast)
* Description Channel (for receiving description information)
* Control Channel (for control-related frames like description, connect, disconnect and health-check)
* Data Channel (for data-related frames like read/request frames to/from KNX)
* Multicast Channel (for routing; multicast)

All data requests of all group addresses that is sent by KNX Net/IP will be fetched by the KNX client internally. 
If the KNX client is using NAT, then control channel and data channel are using a shared channel. 


## Ready-to-go Examples

This project is more intended for developers which are interested in KNX communication. 
However, it contains ready-to-use main classes to get a quick start.

### Send a WRITE frame to KNX

**Class:** ``li.pitschmann.knx.main.KnxMainWrite``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP device (default: ``null``, uses auto-discovery) 
* ``-ga`` the group address you want to communicate with (default: ``1/2/100``)
* ``-dpt`` *(advanced)* the Data Point Type (default: ``1.001`` for DPT1 - Switch)
* ``-c`` *(advanced)* a sequence of commands that is compatible with DPT argument (default: ``on off``)

Given example
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainWrite
````
is equivalent to
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainWrite -ga 1/2/100 -dpt 1.001 -c on off
````

and will perform *DPT1 - Switch (DPT 1.001)* actions on group address ``1/2/100`` - connected to a 
KNX switch actuator to switch ``on`` and then switch ``off`` a lamp. For demo purposes the delay 
between commands is hardcoded with 2 seconds.

Given command ``on off on off`` would perform the lamp to switch ``on``, ``off``, ``on`` and then 
``off`` with a delay of 2 seconds between each command.
 
### Send a READ frame to KNX

**Class:** ``li.pitschmann.knx.main.KnxMainRead``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP device (default: ``null``, uses auto-discovery) 
* ``-ga`` the group address you want to communicate with (default: ``1/2/100``)
* ``-n`` number of echos to send to obtain the most recent status from group address (default: ``50``)

Given example
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainRead -n 10
````
 will send a *READ* request frame to KNX Net/IP on group address ``1/2/100`` at first to get the 
 most recent status of group address. Afterwards it will wait up to ``1`` second (hardcoded)
 to get the most recent status of the group address. 

### KNX Monitoring

**Class:** ``li.pitschmann.knx.main.KnxMainMonitoring``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP device (default: ``null``, uses auto-discovery) 
* ``-t`` the time in seconds how long the monitoring should run (default: ``Long#MAX_VALUE``)

Given example
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainMonitoring -t 3600
````
will monitor the traffic of KNX for one hour (``3600`` seconds). After one hour a disconnect 
frame will be sent by the KNX client to the KNX network and application will exit.

## API Programming

This tutorial is written for experienced Java developers and explains how to integrate the API with your
own program. It looks basically like:

````
// Creates KNX client and start communication with your KNX Net/IP.
// The closure of KXN communication will be handled automatically by KNX client.
try (final var client = DefaultKnxClient.createStarted("ip-address")) {
    // do your stuff you want to do ...
} catch (final Throwable t) {
    // catch all throwable you want to handle here (optional)
} finally {
    // do final actions (optional)
}
````

#### Example #1: Switch on/off lamp with boolean values
You want to switch ``on`` a lamp on KNX group address ``1/2/3`` and switch ``off`` after three seconds. 

````java
public class KnxMain {    
    public static void main(final String[] args) {    
        // group address we want to communicate with
        final var ga = GroupAddress.of(1,2,0);
        // connect KNX client to 192.168.0.10
        try (final var client = DefaultKnxClient.createStarted("192.168.0.10")) {   
            // switch on (boolean: true) --> translated to '0x01' and sent to KNX Net/IP device
            client.writeRequest(ga, DPT1.SWITCH.toValue(true));  // or DPT1.SWITCH.toValue((byte)0x01)
                                                                 // or DPT1.SWITCH.toValue("on")
            // wait 3 seconds
            Thread.sleep(3000);
            
            // switch off (boolean: false) --> translated to '0x00' and sent to KNX Net/IP device
            client.writeRequest(ga, DPT1.SWITCH.toValue(false)); // or DPT1.SWITCH.toValue((byte)0x00)
                                                                 // or DPT1.SWITCH.toValue("off")
        } catch (final InterruptedException ie) {
            // do something ...
        }
    }
}
````

#### Example #2: Inverse the status of lamp
You want to inverse the status of your lamp on KNX group address ``1/2/3``:
* if lamp is ``on`` then lamp will be ``off``
* if lamp is ``off`` then lamp will be ``on``
 
````java
public class KnxMain {    
    public static void main(final String[] args) {    
        // group address we want to communicate with
        final var ga = GroupAddress.of(1,2,0);  // group address for on/off request
        final var gaRead = GroupAddress.of(1,2,3);  // group address for read request
        // connect KNX client to a KNX Net/IP device using auto-discovery (no-arg, null or empty)
        try (final var client = DefaultKnxClient.createStarted()) {  
            // Sends the read request
            // The returned instance is the acknowledge sent by KNX Net/IP indicating that read request was received
            final var readRequestAck = client.readRequest(gaRead).get();

            // Wait bit for update (usually few 10ms, but up to 1 sec max)
            // If communication and read flags on KNX group address are set the state of lamp will be forwarded by the
            // KNX Net/IP and status pool will be updated by KNX client with the actual lamp status
            client.getStatusPool().isUpdated(gaRead, 1, TimeUnit.SECONDS);

            // read lamp state from status pool
            final var lampStatus = client.getStatusPool().getValue(gaRead, DPT1.SWITCH).getBooleanValue();

            // Sends the write request
            // The returned instance is the acknowledge sent by KNX Net/IP indicating that write request was received
            final var writeRequestAck = client.writeRequest(ga, DPT1.SWITCH.toValue(!lampStatus)).get();

            // Wait bit for update (usually few 10ms, but up to 1 sec max)
            // If communication and write flags on KNX group address are set the state of lamp will be changed.
            // The state of lamp will be forwarded by the KNX Net/IP and status pool will be updated by KNX client
            client.getStatusPool().isUpdated(ga, 1, TimeUnit.SECONDS);
        } catch (final InterruptedException t) {
            // do something ...
        }
    }
}
````
