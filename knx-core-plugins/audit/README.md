# KNX Core Plugin: Audit

Sometimes it is helpful to have an auditing file that logs all incoming 
and outgoing KNX packets for further analysis. It also writes all signals 
(initialization, start, stop) and errors like corrupted/non-standard packets.

JSON:
```
{"time":1574758245.851271000,"type":"init"}
{"time":1574758245.851271000,"type":"start"}
{"time":1574761224.357372400,"type":"incoming","header":{"totalLength":9,"raw":"0x06 10 02 05 00 09"},"body":{"service":{"code":"0x02 05","text":"CONNECT_REQUEST"},"raw":"0x11 22 33"}}
{"time":1574761224.948031001,"type":"outgoing","header":{"totalLength":8,"raw":"0x06 10 04 21 00 08"},"body":{"service":{"code":"0x04 21","text":"TUNNELING_ACK"},"raw":"0x22 33"}}
{"time":1574761223.947779200,"type":"error","message":"I'm a \"Runtime\tException\"!","stacktrace":["org.class.Foo.add(Foo.java:123)","org.class.Bar.addAll(Bar.java:456)"]}
{"time":1574758245.854273300,"type":"shutdown"}
```

TSV (tab-separated):
```
Time	Type	Header Total Length	Header Raw	Body Service Code	Body Service Text	Body Raw	Error Message	Error Stack Trace
1574760615.827808800	init
1574760615.828812000	start
1574760615.828812000	incoming	9	0x06 10 02 05 00 09	0x02 05	CONNECT_REQUEST	0x33 44 55
1574760615.851834800	outgoing	8	0x06 10 04 21 00 08	0x04 21	TUNNELING_ACK	0x44 55
1574760615.569226800	error						I'm a "RuntimeException"!	[org.class.Foo.add(Foo.java:123), org.class.Bar.addAll(Bar.java:456)]
1574760615.829808700	shutdown
```

## Configuration

### Path

Defines the path of audit file. Will be daily rolled and appended with date format `yyyyMMdd-HHmmss-SSS`.

**Type:** `PathConfigValue` \
**Default Value:** `knx-audit.log` (in working directory) \
**Config in Code:** `FileAuditPlugin.PATH`  \
**Config in File:** `plugin.config.FileAuditPlugin.path`

### File Format

Defines which file format should be used for audit output.

**Type:** `EnumConfigValue` \
**Default Value:** `FileAuditFormat.JSON` \
**Config in Code:** `FileAuditPlugin.FORMAT` \
**Config in File:** `plugin.config.FileAuditPlugin.format`

| File Format | Config Value |
| --- | --- |
| JSON | `FileAuditFormat.JSON` |
| TSV (tab-separated) | `FileAuditFormat.TSV` |
