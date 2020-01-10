package li.pitschmann.knx.core.plugin.statistic;

/**
 * Format for {@link FileStatisticPlugin}
 * <p>
 * JSON, TSV and TEXT are supported.
 */
public enum FileStatisticFormat {
    // @formatter:off
    /**
     * Statistic format should be in JSON format.
     */
    JSON(
            // Header
            "",
            // Body Template
            "" +
            "{" +
                "\"inbound\":{" +
                    "\"total\":{\"packets\":%1$s,\"bytes\":%2$s}," +
                    "\"search\":{\"request\":%7$s,\"response\":%8$s}," +
                    "\"description\":{\"request\":0,\"response\":%11$s}," +
                    "\"connect\":{\"request\":0,\"response\":%13$s}," +
                    "\"connectionState\":{\"request\":0,\"response\":%15$s}," +
                    "\"tunneling\":{\"request\":%17$s,\"acknowledge\":%18$s}," +
                    "\"indication\":{\"request\":0,\"response\":%25$s}," +
                    "\"disconnect\":{\"request\":%21$s,\"response\":%22$s}" +
                "}," +
                "\"outbound\":{" +
                    "\"total\":{\"packets\":%3$s,\"bytes\":%4$s}," +
                    "\"search\":{\"request\":%9$s,\"response\":%10$s}," +
                    "\"description\":{\"request\":%12$s,\"response\":0}," +
                    "\"connect\":{\"request\":%14$s,\"response\":0}," +
                    "\"connectionState\":{\"request\":%16$s,\"response\":0}," +
                    "\"tunneling\":{\"request\":%19$s,\"acknowledge\":%20$s}," +
                    "\"indication\":{\"request\":%26$s,\"response\":0}," +
                    "\"disconnect\":{\"request\":%23$s,\"response\":%24$s}" +
                "}," +
                "\"error\":{" +
                    "\"total\":{\"packets\":%5$s,\"rate\":%6$.2f}" +
                "}" +
            "}"
    ),
    /**
     * Statistic format should be in TSV format.
     */
    TSV(
            // Header
            "" +
            "Inbound Packets\tInbound Bytes\t" +
            "Outbound Packets\tOutbound Bytes\t" +
            "Error Packets\tError Rate (%)\t" +

            "Inbound Search Requests\tInbound Search Responses\t" +
            "Inbound Description Requests\tInbound Description Responses\t" +
            "Inbound Connect Requests\tInbound Connect Responses\t" +
            "Inbound Connection State Requests\tInbound Connection State Responses\t" +
            "Inbound Disconnect Requests\tInbound Disconnect Responses\t" +
            "Inbound Tunneling Requests\tInbound Tunneling Acknowledges\t" +
            "Inbound Indication Requests\tInbound Indication Responses\t" +

            "Outbound Search Requests\tOutbound Search Responses\t" +
            "Outbound Description Requests\tOutbound Description Responses\t" +
            "Outbound Connect Requests\tOutbound Connect Responses\t" +
            "Outbound Connection State Requests\tOutbound Connection State Responses\t" +
            "Outbound Disconnect Requests\tOutbound Disconnect Responses\t" +
            "Outbound Tunneling Requests\tOutbound Tunneling Acknowledges\t" +
            "Outbound Indication Requests\tOutbound Indication Responses",

            // Body Template
            "" +
            "%1$s\t%2$s\t" +       // inbound total
            "%3$s\t%4$s\t" +       // outbound total
            "%5$s\t%6$.2f\t" +     // error total
            "%7$s\t%8$s\t" +       // inbound search
            "0\t%11$s\t" +         // inbound description
            "0\t%13$s\t" +         // inbound connect
            "0\t%15$s\t" +         // inbound connectionState
            "%21$s\t%22$s\t" +     // inbound disconnect
            "%17$s\t%18$s\t" +     // inbound tunneling
            "0\t%25$s\t" +         // inbound indication
            "%9$s\t%10$s\t" +      // outbound search
            "%12$s\t0\t" +         // outbound description
            "%14$s\t0\t" +         // outbound connect
            "%16$s\t0\t" +         // outbound connectionState
            "%23$s\t%24$s\t" +     // outbound disconnect
            "%19$s\t%20$s\t" +     // outbound tunneling
            "%26$s\t0"             // outbound indication
    ),
    /**
     * Statistic format should be in TEXT format
     */
    TEXT(
            // Header
            "",
            // Body Template
            "" +
            "" +
            "%1$s packets received (%2$s bytes)%n" +                              // line #1
            "\t[Search          ] Request: %7$s, Response: %8$s%n" +              // line #2
            "\t[Description     ] Request: 0, Response: %11$s%n" +                // line #3
            "\t[Connect         ] Request: 0, Response: %13$s%n" +                // line #4
            "\t[Connection State] Request: 0, Response: %15$s%n" +                // line #5
            "\t[Tunneling       ] Request: %17$s, Acknowledge: %18$s%n" +         // line #6
            "\t[Indication      ] Request: 0, Response: %25$s%n" +                // line #7
            "\t[Disconnect      ] Request: %21$s, Response: %22$s%n" +            // line #8
            "%3$s packets sent (%4$s bytes)%n" +                                  // line #9
            "\t[Search          ] Request: %9$s, Response: %10$s%n" +             // line #10
            "\t[Description     ] Request: %12$s, Response: 0%n" +                // line #11
            "\t[Connect         ] Request: %14$s, Response: 0%n" +                // line #12
            "\t[Connection State] Request: %16$s, Response: 0%n" +                // line #13
            "\t[Tunneling       ] Request: %19$s, Acknowledge: %20$s%n" +         // line #14
            "\t[Indication      ] Request: %26$s, Response: 0%n" +                // line #15
            "\t[Disconnect      ] Request: %23$s, Response: %24$s%n" +            // line #16
            "%5$s errors (%6$.2f%%)%n" +                                          // line #17
            "-----------------------------------------------------------------"   // line #18
    );
    // @formatter:on

    private final String header;
    private final String template;

    FileStatisticFormat(final String header, final String template) {
        this.header = header;
        this.template = template;
    }

    public String getHeader() {
        return header;
    }

    public String getTemplate() {
        return template;
    }
}
