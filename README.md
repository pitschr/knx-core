[![Build Status](https://travis-ci.org/pitschr/knx-link.svg?branch=master)](https://travis-ci.org/pitschr/knx-link)
[![Coverage Status](https://coveralls.io/repos/github/pitschr/knx-link/badge.svg?branch=master)](https://coveralls.io/github/pitschr/knx-link?branch=master)

# KNX Link

A reactive, non-blocking Java library for KNX Net/IP communication.

This library is indented to allow you writing your own application communicating with your KNX Net/IP device 
(either a router or an interface). It also contains few main classes for a basic understanding how the KNX 
communication can be done in programmatically way.

This project is more intended for experienced developers which are interested in communication with KNX world. 
It contains ready-to-use main classes to get a quick start. See examples below.

### Known limitations

* Only *tunneling* supported (no routing!)
* No KNX Secure implementation yet
* No Additional Info support in CEMI frame yet
 
### Prerequisites

* **Java 11+**
  * Make sure that you have Java 11 installed and running as Java 11
* **KNX Net/IP device (Router or Interface)**
  * One free tunneling connection available  
  * IP-Address

### Architecture

![Architecture](./assets/readme_architecture.png)

The communication between KNX and the client is reactive and non-blocking which allows a very fast communication and we have two channels simultaneously open: 
* Control Channel (for control-related frames like description, connect, disconnect and health-check)
* Data Channel (for data-related frames like read/request frames to/from KNX)

## Ready-to-go Examples

This project is more intended for developers which are interested in KNX communication. 
However, it contains ready-to-use main classes to get a quick start.

### Send a WRITE frame to KNX

**Class:** ``li.pitschmann.knx.main.KnxMainWrite``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP device (default: ``192.168.1.16``) 
* ``-ga`` the group address you want to communicate with (default: ``1/2/100``)
* ``-dpt`` *(advanced)* the Data Point Type (default: ``1.001`` for DPT1 - Switch)
* ``-c`` *(advanced)* a sequence of commands that is compatible with DPT argument (default: ``on off``)

Given example
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainWrite
````
is equivalent to
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainWrite -r 192.168.1.16 -ga 1/2/100 -dpt 1.001 -c on off
````

and will perform *DPT1 - Switch (DPT 1.001)* actions on group address ``1/2/100`` - connected to a 
KNX switch actuator to switch ``on`` and then switch ``off`` a lamp. For demo purposes the delay 
between commands is hardcoded with 2 seconds.

Given command ``on off on off`` would perform the lamp to switch ``on``, ``off``, ``on`` and then 
``off`` with a delay of 2 seconds between each command.
 
### Send a READ frame to KNX

**Class:** ``li.pitschmann.knx.main.KnxMainRead``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP device (default: ``192.168.1.16``) 
* ``-ga`` the group address you want to communicate with (default: ``1/2/100``)
* ``-n`` number of echos to send to obtain the most recent status from group address (default: ``50``)

Given example
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainRead -n 10
````
 will send a *READ* request frame to KNX Net/IP on group address ``1/2/100`` at first to get the 
 most recent status of group address. Afterwards it will poll up to ``10`` times with a hardcoded 
 interval of ``1`` second to get the most recent status of the group address. All data requests of 
 all group addresses that is sent by KNX Net/IP will be fetched by the KNX client internally.


### KNX Monitoring

**Class:** ``li.pitschmann.knx.main.KnxMainMonitoring``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP device (default: ``192.168.1.16``) 
* ``-t`` the time in seconds how long the monitoring should run (default: ``Long#MAX_VALUE``)

Given example
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainMonitoring -t 3600
````
will monitor the traffic of KNX on default ``192.168.1.16`` for one hour (``3600`` seconds). 
After one hour a disconnect frame will be sent by the KNX client to the KNX network and application will exit.

## API Programming

This tutorial is written for experienced Java developers and explains how to integrate the API with your
own program. It looks basically like:

````
// Creates KNX client and start communication with your KNX Net/IP.
// The closure of KXN communication will be handled automatically by KNX client.
try (final var client = DefaultKnxClient.createStarted("192.168.0.10")) {
    // do your stuff you want to do ...
} catch (final Throwable t) {
    // catch all throwable you want to handle here (optional)
} finally {
    // do final actions (optional)
}
````

Let's start with some examples. Given assumptions:
* You want to switch ``on`` a lamp on KNX group address ``1/2/3`` and switch ``off`` after three seconds 
* The IP-Address of your KNX Net/IP device is ``192.168.0.10`` and KNX default port is used
* Your KNX group address has proper KNX flags set (e.g. communication, write and read)

#### Example #1: Switch on/off lamp with boolean values
* boolean ``true`` => translated to ``0x01`` and sent to KNX Net/IP
* boolean ``false`` => translated to ``0x00`` and sent to KNX Net/IP

````java
public class KnxMain {
    private static final String IP_ADDRESS = "192.168.0.10";
    private static final GroupAddress GROUP_ADDRESS = GroupAddress.of(1,2,3);
    
    public static void main(final String[] args) {     
        try (final var client = DefaultKnxClient.createStarted(IP_ADDRESS)) {   
            // switch on (boolean: true)
            client.writeRequest(GROUP_ADDRESS, DPT1.SWITCH.toValue(true));
            // wait 3 seconds
            Thread.sleep(3000);
            // switch off (boolean: false)
            client.writeRequest(GROUP_ADDRESS, DPT1.SWITCH.toValue(false));
        } catch (final InterruptedException ie) {
            // do something ...
        }
    }
}
````

#### Example #2: Switch on/off lamp with byte values
* byte ``0x01`` (``0000 0001``) => validates if ``0x01`` is compatible with ``DPT1#SWITCH`` and sent to KNX Net/IP device
* byte ``0x00`` (``0000 0000``) => validates if ``0x00`` is compatible with ``DPT1#SWITCH`` and sent to KNX Net/IP device

````java
public class KnxMain {
    private static final String IP_ADDRESS = "192.168.0.10";
    private static final GroupAddress GROUP_ADDRESS = GroupAddress.of(1,2,3);
    
    public static void main(final String[] args) {     
        try (final var client = DefaultKnxClient.createStarted(IP_ADDRESS)) {   
            // switch on (byte: 0000 0001)
            client.writeRequest(GROUP_ADDRESS, DPT1.SWITCH.toValue((byte)0x01));
            // wait 3 seconds
            Thread.sleep(3000);
            // switch off (byte: 0000 0000)
            client.writeRequest(GROUP_ADDRESS, DPT1.SWITCH.toValue((byte)0x00));
        } catch (final InterruptedException ie) {
            // do something ...
        }
    }
}
````

#### Example #3: Switch on/off lamp with human-friendly texts
* text ``on`` => validates if ``on`` is compatible with ``DPT1#SWITCH``, translated to ``0x01`` and sent to KNX Net/IP device
* text ``off`` => validates if ``off`` is compatible with ``DPT1#SWITCH``, translated to ``0x00`` and sent to KNX Net/IP device

````java
public class KnxMain {
    private static final String IP_ADDRESS = "192.168.0.10";
    private static final GroupAddress GROUP_ADDRESS = GroupAddress.of(1,2,3);
    
    public static void main(final String[] args) {     
        try (final var client = DefaultKnxClient.createStarted(IP_ADDRESS)) {   
            // switch on (will be parsed using DPT1#SWITCH)
            client.writeRequest(GROUP_ADDRESS, DPT1.SWITCH.toValue("on"));
            // wait 3 seconds
            Thread.sleep(3000);
            // switch off (will be parsed using DPT1#SWITCH)
            client.writeRequest(GROUP_ADDRESS, DPT1.SWITCH.toValue("off"));
        } catch (final InterruptedException ie) {
            // do something ...
        }
    }
}
````

#### Example #4: Inverse the status of lamp
This example inverses the status of lamp
* if lamp is ``on`` then lamp will be ``off``
* if lamp is ``off`` then lamp will be ``on``
 
````java
public class KnxMain {
    private static final String IP_ADDRESS = "192.168.0.10";
    private static final GroupAddress GROUP_ADDRESS = GroupAddress.of(1,2,3);
    
    public static void main(final String[] args) {     
        try (final var client = DefaultKnxClient.createStarted(IP_ADDRESS)) {
            // Sends the read request
            // The returned instance is the acknowledge sent by KNX Net/IP indicating that read request was received
            final var readRequestAck = client.readRequest(GROUP_ADDRESS).get();
            log.debug("READ ACK: {}", readRequestAck);

            // Wait bit for update (usually few 10ms, but up to 1 sec max)
            // If communication and read flags on KNX group address are set the state of lamp will be forwarded by the
            // KNX Net/IP and status pool will be updated by KNX client with the actual lamp status
            client.getStatusPool().isUpdated(address, 1, TimeUnit.SECONDS);

            // read lamp state
            final var lampStatus = client.getStatusPool().getValue(GROUP_ADDRESS, DPT1.SWITCH).getBooleanValue();
            log.debug("STATUS BEFORE SWITCH: {}", lampStatus);

            // Sends the write request
            // The returned instance is the acknowledge sent by KNX Net/IP indicating that write request was received
            final var writeRequestAck = client.writeRequest(GROUP_ADDRESS, DPT1.SWITCH.toValue(!lampStatus)).get();
            log.debug("WRITE ACK: {}", writeRequestAck);

            // Wait bit for update (usually few 10ms, but up to 1 sec max)
            // If communication and write flags on KNX group address are set the state of lamp will be changed.
            // The state of lamp will be forwarded by the KNX Net/IP and status pool will be updated by KNX client
            client.getStatusPool().isUpdated(GROUP_ADDRESS, 1, TimeUnit.SECONDS);

            log.debug("STATUS AFTER SWITCH: {}", client.getStatusPool().getValue(GROUP_ADDRESS, DPT1.SWITCH).getBooleanValue());
        } catch (final Throwable t) {
            log.error("THROWABLE. Reason: {}", t.getMessage(), t);
        }
    }
}
````
