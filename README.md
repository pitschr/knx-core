# KNXClient

A simple, reactive KNX Client to communicate with your KNX Net/IP router.

## Known limitations

* Only *tunnelling* supported (no routing!)
* No *NAT* supported

## Getting Started
 
### Prerequisites

* **Java 10+**
  * Make sure that you have Java 10 installed and running as Java 10.
* **KNX Net/IP Router** 
  * IP-Address of your KNX Net/IP router
  * Some group addresses

## Examples

This project is more intended for developers which are interested in KNX communication.
However, it contains ready-to-use main classes to get a quick start.

### Sending a WRITE frame to your KNX Net/IP router

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

and will perform *DPT1 - Switch (DPT 1.001)* actions on group address ``1/2/100`` - connected to a KNX switch actuator to switch ``on`` and then switch ``off`` a lamp. For demo purposes the delay between commands is hardcoded with 2 seconds.

Given command ``on off on off`` would perform the lamp to switch ``on``, ``off``, ``on`` and then ``off`` with a delay of 2 seconds between each command.
 
## Sending a READ frame to your KNX Net/IP router

**Class:** ``li.pitschmann.knx.main.KnxMainRead``

**Arguments**
* ``-r`` the IP-Address of your KNX Net/IP router (default: ``192.168.1.16``) 
* ``-ga`` the group address you want to communicate with (default: ``1/2/100``)
* ``-n`` number of echos to send to obtain the most recent status from group address (default: ``50``)

Given example
````
java -cp <file>.jar li.pitschmann.knx.main.KnxMainRead -n 10
````
 will send a *READ* request frame to KNX Net/IP router on group address ``1/2/100`` at first to get the most recent status of group address. Afterwards it will poll up to ``50`` times to get the most recent status of the group address. All data requests of all group addresses that is sent by KNX Net/IP router will be fetched by the KNX client internally.


## Start monitoring the KNX Net/IP router

