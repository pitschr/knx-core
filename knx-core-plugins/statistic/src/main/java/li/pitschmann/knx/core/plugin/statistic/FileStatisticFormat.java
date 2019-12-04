package li.pitschmann.knx.core.plugin.statistic;

/**
 * Format for {@link FileStatisticPlugin}
 * <p/>
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
                    "\"description\":{\"request\":0,\"response\":%7$s}," +
                    "\"connect\":{\"request\":0,\"response\":%9$s}," +
                    "\"connectionState\":{\"request\":0,\"response\":%11$s}," +
                    "\"tunneling\":{\"request\":%13$s,\"acknowledge\":%15$s}," +
                    "\"indication\":{\"request\":0,\"response\":%21$s}," +
                    "\"disconnect\":{\"request\":%17$s,\"response\":%19$s}" +
                "}," +
                "\"outbound\":{" +
                    "\"total\":{\"packets\":%3$s,\"bytes\":%4$s}," +
                    "\"description\":{\"request\":%8$s,\"response\":0}," +
                    "\"connect\":{\"request\":%10$s,\"response\":0}," +
                    "\"connectionState\":{\"request\":%12$s,\"response\":0}," +
                    "\"tunneling\":{\"request\":%14$s,\"acknowledge\":%16$s}," +
                    "\"indication\":{\"request\":%22$s,\"response\":0}," +
                    "\"disconnect\":{\"request\":%18$s,\"response\":%20$s}" +
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

            "Inbound Description Requests\tInbound Description Responses\t" +
            "Inbound Connect Requests\tInbound Connect Responses\t" +
            "Inbound Connection State Requests\tInbound Connection State Responses\t" +
            "Inbound Disconnect Requests\tInbound Disconnect Responses\t" +
            "Inbound Tunneling Requests\tInbound Tunneling Acknowledges\t" +
            "Inbound Indication Requests\tInbound Indication Responses\t" +

            "Outbound Description Requests\tOutbound Description Responses\t" +
            "Outbound Connect Requests\tOutbound Connect Responses\t" +
            "Outbound Connection State Requests\tOutbound Connection State Responses\t" +
            "Outbound Disconnect Requests\tOutbound Disconnect Responses\t" +
            "Outbound Tunneling Requests\tOutbound Tunneling Acknowledges\t" +
            "Outbound Indication Requests\tOutbound Indication Responses",

            // Body Template
            "" +
            "%1$s\t%2$s\t" +                                     // inbound total
            "%3$s\t%4$s\t" +                                     // outbound total
            "%5$s\t%6$.2f\t" +                                   // error total
            "0\t%7$s\t0\t%9$s\t0\t%11$s\t%17$s\t%19$s\t" +       // inbound description, connect, connectionState, disconnect
            "%13$s\t%15$s\t0\t%21$s\t" +                         // inbound tunneling, indication
            "%8$s\t0\t%10$s\t0\t%12$s\t0\t%18$s\t%20$s\t" +      // outbound description, connect, connectionState, disconnect
            "%14$s\t%16$s\t%22$s\t0"                             // outbound tunneling, indication
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
            "\t[Description     ] Request: 0, Response: %7$s%n" +                 // line #2
            "\t[Connect         ] Request: 0, Response: %9$s%n" +                 // line #3
            "\t[Connection State] Request: 0, Response: %11$s%n" +                // line #4
            "\t[Tunneling       ] Request: %13$s, Acknowledge: %15$s%n" +         // line #5
            "\t[Indication      ] Request: 0, Response: %21$s%n" +                // line #6
            "\t[Disconnect      ] Request: %17$s, Response: %19$s%n" +            // line #7
            "%3$s packets sent (%4$s bytes)%n" +                                  // line #8
            "\t[Description     ] Request: %8$s, Response: 0%n" +                 // line #9
            "\t[Connect         ] Request: %10$s, Response: 0%n" +                // line #10
            "\t[Connection State] Request: %12$s, Response: 0%n" +                // line #11
            "\t[Tunneling       ] Request: %14$s, Acknowledge: %16$s%n" +         // line #12
            "\t[Indication      ] Request: %22$s, Response: 0%n" +                // line #13
            "\t[Disconnect      ] Request: %18$s, Response: %20$s%n" +            // line #14
            "%5$s errors (%6$.2f%%)%n" +                                          // line #15
            "-----------------------------------------------------------------"   // line #16
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
