[![Build Status](https://travis-ci.org/pitschr/knx-link.svg?branch=master)](https://travis-ci.org/pitschr/knx-link)
[![Coverage Status](https://coveralls.io/repos/github/pitschr/knx-link/badge.svg?branch=master)](https://coveralls.io/github/pitschr/knx-link?branch=master)

# KNX Link

A library for the KNX Net/IP communication with your KNX Net/IP router.

This library is indented to allow you writing your own application communicating with your KNX network. 
It also contains few main classes for a basic understanding how the KNX communication can be done in 
programmatically way.

This project is more intended for developers which are interested in KNX communication. 
However, it contains ready-to-use main classes to get a quick start. See examples below.

#### Known limitations

* Only *tunnelling* supported (no routing!)
* No *NAT* supported
 
#### Prerequisites

* **Java 10+**
  * Make sure that you have Java 10 installed and running as Java 10.
* **KNX Net/IP Router** 
  * IP-Address of your KNX Net/IP router
  * Some group addresses

#### Architecture

![Architecture](./assets/readme_architecture.png)

## Ready-to-go Examples

This project is more intended for developers which are interested in KNX communication. 
However, it contains ready-to-use main classes to get a quick start.

### Send a WRITE frame to your KNX Net/IP router

**Class:** ``li.pitschmann.knx.main.KnxMainWrite``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP router (default: ``192.168.1.16``) 
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
 
## Send a READ frame to your KNX Net/IP router

**Class:** ``li.pitschmann.knx.main.KnxMainRead``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP router (default: ``192.168.1.16``) 
* ``-ga`` the group address you want to communicate with (default: ``1/2/100``)
* ``-n`` number of echos to send to obtain the most recent status from group address (default: ``50``)

Given example
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainRead -n 10
````
 will send a *READ* request frame to KNX Net/IP router on group address ``1/2/100`` at first to get the 
 most recent status of group address. Afterwards it will poll up to ``10`` times with a hardcoded 
 interval of ``1`` second to get the most recent status of the group address. All data requests of 
 all group addresses that is sent by KNX Net/IP router will be fetched by the KNX client internally.


## KNX Monitoring

**Class:** ``li.pitschmann.knx.main.KnxMainMonitoring``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP router (default: ``192.168.1.16``) 
* ``-t`` the time in seconds how long the monitoring should run (default: ``Long#MAX_VALUE``)

Given example
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainMonitoring -t 3600
````
will monitor the traffic of KNX Net/IP router on default ``192.168.1.16`` for one hour (``3600`` seconds). 
After one hour a disconnect frame will be sent by the KNX client to the router and application will exit.

## API Programming

This tutorial is written for experienced Java developers and explains how to integrate the API with your
own program. It looks basically like:

````
// Creates KNX client and start communication with your KNX Net/IP router.
// The closure of KXN communication will be handled automatically by KNX client.
try (final KnxClient client = new DefaultKnxClient("192.168.0.10")) {
    // do your stuff you want to do ...
} catch (final Throwable t) {
    // catch all throwable you want to handle here (optional)
} finally {
    // do final actions (optional)
}
````

Let's start with some examples. Given assumptions:
* You want to switch ``on`` a lamp on KNX group address ``1/2/3`` and switch ``off`` after three seconds 
* The IP-Address of your KNX Net/IP router is ``192.168.0.10`` and KNX default port is used
* Your KNX group address has proper KNX flags set (e.g. communication, write and read)

#### Example #1: Switch on/off lamp with boolean values
* boolean ``true`` => translated to ``0x01`` and sent to KNX Net/IP router
* boolean ``false`` => translated to ``0x00`` and sent to KNX Net/IP router

````java
public class KnxMain {
    private static final String ROUTER_IP_ADDRESS = "192.168.0.10";
    private static final GroupAddress GROUP_ADDRESS = GroupAddress.of(1,2,3);
    
    public static void main(final String[] args) {     
        try (final DefaultKnxClient client = new DefaultKnxClient(ROUTER_IP_ADDRESS)) {   
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
* byte ``0x01`` (``0000 0001``) => validates if ``0x01`` is compatible with ``DPT1#SWITCH`` and sent to KNX Net/IP router
* byte ``0x00`` (``0000 0000``) => validates if ``0x00`` is compatible with ``DPT1#SWITCH`` and sent to KNX Net/IP router

````java
public class KnxMain {
    private static final String ROUTER_IP_ADDRESS = "192.168.0.10";
    private static final GroupAddress GROUP_ADDRESS = GroupAddress.of(1,2,3);
    
    public static void main(final String[] args) {     
        try (final DefaultKnxClient client = new DefaultKnxClient(ROUTER_IP_ADDRESS)) {   
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
* text ``on`` => validates if ``on`` is compatible with ``DPT1#SWITCH``, translated to ``0x01`` and sent to KNX Net/IP router
* text ``off`` => validates if ``off`` is compatible with ``DPT1#SWITCH``, translated to ``0x00`` and sent to KNX Net/IP router

````java
public class KnxMain {
    private static final String ROUTER_IP_ADDRESS = "192.168.0.10";
    private static final GroupAddress GROUP_ADDRESS = GroupAddress.of(1,2,3);
    
    public static void main(final String[] args) {     
        try (final DefaultKnxClient client = new DefaultKnxClient(ROUTER_IP_ADDRESS)) {   
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
    private static final String ROUTER_IP_ADDRESS = "192.168.0.10";
    private static final GroupAddress GROUP_ADDRESS = GroupAddress.of(1,2,3);
    
    public static void main(final String[] args) {     
        try (final DefaultKnxClient client = new DefaultKnxClient(routerAddress)) {
            // Sends the read request
            // The returned instance is the acknowledge sent by KNX router indicating that read request was received
            final var readRequestAck = client.readRequest(groupAddress).get();
            LOG.debug("READ ACK: {}", readRequestAck);

            // Wait bit for update (up to 1 sec)
            // If communication and read flags on KNX group address are set the state of lamp will be forwarded by the
            // KNX router and status pool will be updated with lamp status
            client.getStatusPool().isUpdated(groupAddress, 1, TimeUnit.SECONDS);

            // read lamp state
            final var lampStatus = client.getStatusPool().getValue(groupAddress, DPT1.SWITCH).getBooleanValue();
            LOG.debug("STATUS BEFORE SWITCH: {}", lampStatus);

            // Sends the write request
            // The returned instance is the acknowledge sent by KNX router indicating that write request was received
            final var writeRequestAck = client.writeRequest(groupAddress, DPT1.SWITCH.toValue(!lampStatus)).get();
            LOG.debug("WRITE ACK: {}", writeRequestAck);

            // Wait bit for update (up to 1 sec)
            // If communication and write flags on KNX group address are set the state of lamp will be changed and
            // the state of lamp will be forwarded by the KNX router which updates the status pool as well
            client.getStatusPool().isUpdated(groupAddress, 1, TimeUnit.SECONDS);

            LOG.debug("STATUS AFTER SWITCH: {}", client.getStatusPool().getValue(groupAddress, DPT1.SWITCH).getBooleanValue());
        } catch (final Throwable t) {
            LOG.error("THROWABLE. Reason: {}", t.getMessage(), t);
        }
    }
}
````
