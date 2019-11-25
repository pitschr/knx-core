# KNX Core Plugin: Statistic

Used to log the statistic of KNX traffic in a pre-defined interval (default: 60 seconds).

JSON file format:
```
{"inbound":{"total":{"packets":10,"bytes":11},"description":{"request":0,"response":21},"connect":{"request":0,"response":31}, ... 
```

CSV file format:
```
"Inbound Total Packets","Inbound Total Bytes","Outbound Total Packets","Outbound Total Bytes", ... 
10,11,12,13,14,1.50,0,21,0,31,0,41,70, ...
```

Text file format:
```
10 packets received (11 bytes)
	[Description     ] Request: 0, Response: 21
	[Connect         ] Request: 0, Response: 31
	[Connection State] Request: 0, Response: 41
	[Tunneling       ] Request: 50, Acknowledge: 51
	[Indication      ] Request: 0, Response: 60
	[Disconnect      ] Request: 70, Response: 71
12 packets sent (13 bytes)
	[Description     ] Request: 22, Response: 0
	[Connect         ] Request: 32, Response: 0
	[Connection State] Request: 42, Response: 0
	[Tunneling       ] Request: 52, Acknowledge: 53
	[Indication      ] Request: 61, Response: 0
	[Disconnect      ] Request: 72, Response: 73
14 errors (1.50%)
```

## Configuration

### Path

Defines the path of statistic file. Will be daily rolled and appended with date format `yyyyMMdd-HHmmss-SSS`.

**Type:** `PathConfigValue` \
**Default Value:** `knx-statistic.log` (in working directory) \
**Config in Code:** `FileStatisticPlugin.PATH`  \
**Config in File:** `plugin.config.FileStatisticPlugin.path`

### File Format

Defines which file format should be used for statistic output.

**Type:** `EnumConfigValue` \
**Default Value:** `FileStatisticFormat.JSON` \
**Config in Code:** `FileStatisticPlugin.FORMAT` \
**Config in File:** `plugin.config.FileStatisticPlugin.format`

| File Format | Config Value |
| --- | --- |
| JSON | `FileStatisticFormat.JSON` |
| Comma Separated | `FileStatisticFormat.CSV` |
| Text | `FileStatisticFormat.TEXT` |

### Interval

Defines the interval in milliseconds how frequent the statistic should be 
logged to the file. To avoid a high CPU utilization the interval must be
10'000 milliseconds (=10 sec) or higher.

**Type:** `LongConfigValue` \
**Default Value:** `300'000` milliseconds (=5 minutes) \
**Config in Code:** `FileStatisticPlugin.INTERVAL` \
**Config in File:** `plugin.config.FileStatisticPlugin.intervalMs`
