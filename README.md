[![Build Status](https://github.com/pitschr/knx-link/workflows/build/badge.svg?branch=master)](https://github.com/pitschr/knx-link/actions)
[![Coverage Status](https://coveralls.io/repos/github/pitschr/knx-link/badge.svg?branch=master)](https://coveralls.io/github/pitschr/knx-link?branch=master)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Maven Central](https://img.shields.io/maven-central/v/li.pitschmann/knx-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22li.pitschmann%22)

# KNX Link

A reactive, non-blocking Java library for KNX Net/IP communication.

The purpose of this library is designed for developers to allow their applications 
to communicate with KNX world via KNX Net/IP device (either a KNX router or a KNX 
interface) and it supports _tunneling_ and _routing_ modes.
 
For examples, to get a quick start see examples below. It contains few _main classes_ 
to get a quick basic understanding how the communication with KNX can be done in 
programmatically way; see [knx-core](knx-core) and [knx-core-plugins](knx-core-plugins).

For a demo application see [knx-demo-tty-monitor](https://github.com/pitschr/knx-demo-tty-monitor).

### Known limitations

* No KNX Secure which are offered by newest generation of KNX Net/IP devices 
(because I do not have a KNX router that supports KNX secure)
 
### Prerequisites

* **Java 11+**
  * Make sure that you have Java 11+ installed and running as Java 11+
* **Tunneling: KNX Router or Interface**
  * One free tunneling connection available
  * IP-Address (optional, if not provided - the auto-discovery service will be used)
* **Routing: KNX Router only**
  * Filter table on your KNX router device is properly configured, otherwise packets won't 
  be forwarded
  * IP Multicast Address that is used by your KNX router (optional, if not provided, the 
  default 224.0.23.12 multicast address will be used)

### Architecture

![Architecture](./assets/readme_architecture.png)

The communication between KNX Net/IP device and the KNX client is reactive and non-blocking 
which allows a very fast communication and we may have multiple channels simultaneously open: 
* _Discovery Channel_ for discovery service (multicast)
* _Description Channel_ for receiving description information
* _Control Channel_ for tunneling; control-related frames like connect, disconnect and connection state
* _Data Channel_ for tunneling; data-related frames like read/write requests from and to KNX
* _Multicast Channel_ for routing; data-related frames

#### Communication mode (Tunneling, NAT, Routing)

According to the KNX specification the communication is defaulted to _tunneling_ mode and without 
Network Address Translation (NAT). If you have multiple KNX Net/IP devices and want to specify
the IP-Address use the `--ip <address[:port]>`. If the communication should be using _routing_ instead
of tunneling then it must be explicitly defined using `--routing` argument.

Using _tunneling_ mode we need Network Address Translation (NAT) in some cases; this must be 
enabled using `--nat` parameter. When NAT enabled, then the _Control Channel_ and _Data Channel_ 
will use a shared channel.  One practical example, where we need NAT would be e.g. dockerized image. 
NAT is suitable for _tunneling_ mode only, in _routing_ mode it has no
effect.

#### KNX Client

All data-related frames of all KNX group addresses that is sent by KNX Net/IP device will be 
fetched and handled by the KNX client and it offers:
* _Status Pool_ to fetch the latest status and data about the KNX group address
* _Event Pool_ for more advanced and detailed diagnosis and stores the original request and 
response frames 
frame belongs to which request frame for more advanced and detailed diagnosis
* _Statistic_ to get basic statistic how many bytes/frames/type of frames were sent or received
* _Plugin Injector_ allowing to extend KNX client with plugin, for example: auditing, long-term 
event logging, and much more.
* _Data Point Type Translator_ to translate human-friendly (e.g. "on", "off"), Java data types 
(e.g. true, false) into KNX compatible byte representation; and vice versa 

### Data Point Types

This KNX library (incl. KNX Client) supports following data point types below and is designed to 
translate data point types in a fluent way into a KNX byte-array compatible format.

| DPT    | Description              | DPT    | Description                      | DPT    | Description             |
| ------ | ------------------------ | ------ | -------------------------------- | ------ | ----------------------- |
| 1.xxx  | Binary                   | 11.xxx | Date (Year: 1990..2089)          | 21.xxx | 8-Bit Flagged Messages  |        
| 2.xxx  | Controlled Binary        | 12.xxx | 4-Octet Unsigned Value           | 22.xxx | 16-Bit Flagged Messages |
| 3.xxx  | Controlled Step/Interval | 13.xxx | 4-Octet Signed Value             | 23.xxx | 2-Bit Enumeration       |
| 4.xxx  | Character                | 14.xxx | 4-Octet Float Value              | 25.xxx | 2-Nibble Set            |
| 5.xxx  | 8-Bit Unsigned Value     | 15.xxx | Access Data                      | 24.xxx | ISO-8859-1 Characters<br>(variable length) |
| 6.xxx  | 8-Bit Signed Value       | 16.xxx | 14-Octet Characters              | 26.xxx | Scene Information       |
| 7.xxx  | 2-Octet Unsigned Value   | 17.xxx | Scene Number                     | 27.xxx | Combined Info On/Off    |
| 8.xxx  | 2-Octet Signed Value     | 18.xxx | Controlled Scene Number          | 28.xxx | UTF-8 Characters<br>(variable length) |
| 9.xxx  | 2-Octet Float Value      | 19.xxx | Date and Time (Year: 1900..2155) | 29.xxx | 8-Octet Signed Value<br>Electrical Energy |
| 10.xxx | Time                     | 20.xxx | 8-Bit Enumeration                |

Based on [article](https://support.knx.org/hc/en-us/articles/115001133744-Datapoint-Type) from KNX Association 
the DPT1 until DPT20 are most often used data point types.

For other DPTs which are not on the list yet, you can either implement the Data Point Type by yourself 
by extending the `DataPointType`. Alternatively use the built-in `DPTRaw` whereas the raw value is in 
byte array format.

## Quick Start Guides

### Send a WRITE request frame to KNX

**Class:** [``li.pitschmann.knx.examples.write.Main``](knx-examples/src/main/java/li/pitschmann/knx/examples/write/Main.java)

**Arguments:**
* `-ga`, `--groupAddress` the KNX group address which has a _write_ flag
* `-dpt`,`--dataPointType` the KNX Data Point Type
* `-v`, `--value` a sequence of commands that is compatible with KNX Data Point Type argument

**Switching lamp on KNX group address `1/2/50`**

Perform a DPT1 - Switch (`1.001`) _write request_ action on KNX group address `1/2/50` to switch 
`on` and then `off` a lamp. For demo purposes the delay between commands is hardcoded with two seconds.

```shell
# Tunneling (auto-discovery)
java -cp <file>.jar li.pitschmann.knx.examples.write.Main -ga 1/2/50 -dpt 1.001 -v on off
# Tunneling (auto-discovery with NAT)
java -cp <file>.jar li.pitschmann.knx.examples.write.Main --nat -ga 1/2/50 -dpt 1.001 -v on off
# Tunneling (IP Address)
java -cp <file>.jar li.pitschmann.knx.examples.write.Main --ip 192.168.1.16 -ga 1/2/50 -dpt 1.001 -v on off
# Tunneling (IP Address with NAT)
java -cp <file>.jar li.pitschmann.knx.examples.write.Main --ip 192.168.1.16 --nat -ga 1/2/50 -dpt 1.001 -v on off
# Routing
java -cp <file>.jar li.pitschmann.knx.examples.write.Main --routing -ga 1/2/50 -dpt 1.001 -v on off
```

For sequence of commands you may use e.g. `(-v|--value) on off on off` to switch on/off the lamp twice
times. 
 
### Send a READ request frame to KNX

**Class:** [`li.pitschmann.knx.examples.read.Main`](knx-examples/src/main/java/li/pitschmann/knx/examples/read/Main.java)

**Arguments:**
* `-ga`, `--groupAddress` the KNX group address which has a _read_ flag
* `-n`, `--loops` number of read requests

**Read the actual status of a lamp on KNX group address `1/2/113`**

Send a _read request_ frames to KNX group address `1/2/113` up to `10` times. For demo purposes the delay 
between read requests is hardcoded with one second.

```shell
# Tunneling (auto-discovery)
java -cp <file>.jar li.pitschmann.knx.examples.read.Main -ga 1/2/113 -n 10
# Tunneling (auto-discovery with NAT)
java -cp <file>.jar li.pitschmann.knx.examples.read.Main --nat -ga 1/2/113 -n 10
# Tunneling (IP Address)
java -cp <file>.jar li.pitschmann.knx.examples.read.Main --ip 192.168.1.16 -ga 1/2/113 -n 10
# Tunneling (IP Address with NAT)
java -cp <file>.jar li.pitschmann.knx.examples.read.Main --ip 192.168.1.16 --nat -ga 1/2/113 -n 10
# Routing
java -cp <file>.jar li.pitschmann.knx.examples.read.Main --routing -ga 1/2/113 -n 10
```

For more examples there is a [dedicated page](knx-examples) available.

## Programming

#### Core Development

For development with KNX Core there is a [dedicated page](knx-core) available.

#### Plugin Development

For plugin there is a [dedicated page](knx-core-plugins) available.
