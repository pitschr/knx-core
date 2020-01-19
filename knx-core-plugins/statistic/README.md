# KNX Core Plugin: Statistic

Used to log the statistic of KNX traffic in a pre-defined interval (default: 60 seconds).
The format of statistic file can be either in: JSON, TSV (tab-separated) and plain text.

JSON:
```
{"inbound":{"total":{"packets":10,"bytes":11},"search":{"request":100,"response":110},"description":{"request":0,"response":210},"connect":{"request":0,"response":310},"connectionState":{"request":0,"response":410},"tunneling":{"request":500,"acknowledge":510},"indication":{"request":0,"response":600},"disconnect":{"request":700,"response":710}},"outbound":{"total":{"packets":12,"bytes":13},"search":{"request":120,"response":130},"description":{"request":220,"response":0},"connect":{"request":320,"response":0},"connectionState":{"request":420,"response":0},"tunneling":{"request":520,"acknowledge":530},"indication":{"request":610,"response":0},"disconnect":{"request":720,"response":730}},"error":{"total":{"packets":14,"rate":1.50}}}
```

TSV (tab-separated):
```
Inbound Packets	Inbound Bytes	Outbound Packets	Outbound Bytes	Error Packets	Error Rate (%)	Inbound Search Requests	Inbound Search Responses	Inbound Description Requests	Inbound Description Responses	Inbound Connect Requests	Inbound Connect Responses	Inbound Connection State Requests	Inbound Connection State Responses	Inbound Disconnect Requests	Inbound Disconnect Responses	Inbound Tunneling Requests	Inbound Tunneling Acknowledges	Inbound Indication Requests	Inbound Indication Responses	Outbound Search Requests	Outbound Search Responses	Outbound Description Requests	Outbound Description Responses	Outbound Connect Requests	Outbound Connect Responses	Outbound Connection State Requests	Outbound Connection State Responses	Outbound Disconnect Requests	Outbound Disconnect Responses	Outbound Tunneling Requests	Outbound Tunneling Acknowledges	Outbound Indication Requests	Outbound Indication Responses
10	11	12	13	14	1.50	100	110	0	210	0	310	0	410	700	710	500	510	0	600	120	130	220	0	320	0	420	0	720	730	520	530	610	0
```

Plain text:
```
10 packets received (11 bytes)
	[Search          ] Request: 100, Response: 110
	[Description     ] Request: 0, Response: 210
	[Connect         ] Request: 0, Response: 310
	[Connection State] Request: 0, Response: 410
	[Tunneling       ] Request: 500, Acknowledge: 510
	[Indication      ] Request: 0, Response: 600
	[Disconnect      ] Request: 700, Response: 710
12 packets sent (13 bytes)
	[Search          ] Request: 120, Response: 130
	[Description     ] Request: 220, Response: 0
	[Connect         ] Request: 320, Response: 0
	[Connection State] Request: 420, Response: 0
	[Tunneling       ] Request: 520, Acknowledge: 530
	[Indication      ] Request: 610, Response: 0
	[Disconnect      ] Request: 720, Response: 730
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
| TSV (tab-separated) | `FileStatisticFormat.TSV` |
| Plain Text | `FileStatisticFormat.TEXT` |

### Interval

Defines the interval in milliseconds how frequent the statistic should be 
logged to the file. To avoid a high CPU utilization the interval must be
10'000 milliseconds (=10 sec) or higher.

**Type:** `LongConfigValue` \
**Default Value:** `300'000` milliseconds (=5 minutes) \
**Config in Code:** `FileStatisticPlugin.INTERVAL` \
**Config in File:** `plugin.config.FileStatisticPlugin.intervalMs`
