# KNX Core Plugin: Statistic

Used to log the statistic of KNX traffic in a pre-defined interval (default: 60 seconds).
The format of statistic file can be either in: JSON, TSV (tab-separated) and plain text.

JSON:
```
{"inbound":{"total":{"packets":10,"bytes":11},"description":{"request":0,"response":21},"connect":{"request":0,"response":31},"connectionState":{"request":0,"response":41},"tunneling":{"request":50,"acknowledge":51},"indication":{"request":0,"response":60},"disconnect":{"request":70,"response":71}},"outbound":{"total":{"packets":12,"bytes":13},"description":{"request":22,"response":0},"connect":{"request":32,"response":0},"connectionState":{"request":42,"response":0},"tunneling":{"request":52,"acknowledge":53},"indication":{"request":61,"response":0},"disconnect":{"request":72,"response":73}},"error":{"total":{"packets":14,"rate":1.50}}}
```

TSV (tab-separated):
```
10	11	12	13	14	1.50	0	21	0	31	0	41	70	71	50	51	0	60	22	0	32	0	42	0	72	73	52	53	61	0
```

Plain text:
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
