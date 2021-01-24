# World of Examples for KNX Link

Welcome to the section where you can find various examples how you can use the KNX Link project.


### Example: Read the status of a lamp

The [LampReadExample.java](knx-examples/src/main/java/li/pitschmann/knx/examples/lamp_read/LampReadExample.java)
demonstrates how to send a read request to fetch the lamp status.

```
// send a read request to the KNX actuator
client.readRequest(knxGroupAddress);         

// this is the status pool that holds all status information which have been requested
var statusPool = client.getStatusPool();     
// the status pool will translate the byte array from KNX into boolean value
var isLampOn = statusPool.getValue(knxGroupAddress, DPT1.SWITCH).getValue();  
```

### Example: Switch on the lamp

The [LampOnExample.java](knx-examples/src/main/java/li/pitschmann/knx/examples/lamp_on/LampOnExample.java)
demonstrates how you can switch `on` a lamp for a given KNX Group Address with a pre-defined value. The KNX 
Client is flexible and gives you various ways how to achieve it:

* Switch `on` by a data point type specific value (e.g. for DPT1 it would be `boolean`)
    ```
    client.writeRequest(knxGroupAddress, DPT1.SWITCH.of(true));         
    ```
* Switch `on` by a human-friendly text (e.g. `on`/`off` for `DPT1.SWITCH` and `enable`/`disable` for DPT1.ENABLE`) 
    ```
    client.writeRequest(knxGroupAddress, DPT1.SWITCH.of("on"));
    ```
* Switch `on` by byte or byte-array (more intended for automation processes)
    ```
    client.writeRequest(knxGroupAddress, DPT1.SWITCH.of((byte)0x01));   
    ```

### Example: Switch on/off the lamp twice times (write sequence)

The [LampWriteExample.java](knx-examples/src/main/java/li/pitschmann/knx/examples/lamp_write/LampWriteExample.java)
demonstrates how to send a sequence of values to the KNX actuator. Given example below we want to switch on/off the 
lamp twice times (`on`, `off`, `on`, `off`).

```
var writeGroupAddress = GroupAddress.of(1, 2, 0);
var dpt = "1.001";  // KNX data point type ID (=DPT1.SWITCH)
var values = new String[]{"on", "off", "on", "off"};

// iterate for each value
for (final String value : values) {
    // translate the human-friendy text into a KNX compatible data point value of DPT 1.001
    // and send to the KNX actuator
    var knxValue = DataPointRegistry.getDataPointType(dpt).of(value);
    client.writeRequest(writeGroupAddress, knxValue);
}
```

### Example: Toggle the lamp

The [LampToggleExample.java](knx-examples/src/main/java/li/pitschmann/knx/examples/lamp_toggle/LampToggleExample.java)
demonstrates how to toggle the lamp:
* If the lamp is `off` then it will be switch `on`. 
* If the lamp is `on` then it will switch `off`.

The workflow is to request a read status of the lamp to distinguish if the lamp is currently `on` or `off`. As soon
we know the status the current status will be inverted (`off` -> `on` and `on` -> `off`) and sent to the KNX actuator.

In KNX it is common that we have a separate KNX Group Address for status and for setting.

```
var readGroupAddress = GroupAddress.of(1, 2, 3);    // the KNX Group address to fetch the current lamp status
var writeGroupAddress = GroupAddress.of(1, 2, 0);   // the KNX Group Address for set new lamp value ('on' or 'off')

// send read request and fetch the current lamp status 
client.readRequest(readGroupAddress);
var lampStatus = client.getStatusPool().getValue(readGroupAddress, DPT1.SWITCH).getValue();

// toggle the value (off -> on, on -> off)
var lampStatusInverted = !lampStatus;

// now send the write request to set the new lamp status
client.writeRequest(writeGroupAddress, DPT1.SWITCH.of(lampStatusInverted));
```

For more examples, see the [examples package](knx-examples/src/main/java/li/pitschmann/knx/examples) 
