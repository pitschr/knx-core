package li.pitschmann.knx.plugins.statistic;

import javax.annotation.Nonnull;

/**
 * Format for {@link FileStatisticPlugin}
 * <p/>
 * JSON and TEXT are supported.
 */
public enum FileStatisticFormat {
    // @formatter:off
    /**
     * Statistic format should be in JSON format.
     */
    JSON("" +
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
     * Statistic format should be in CSV format.
     */
    CSV("" +
            "%1$s,%2$s," +                               // inbound total
            "%3$s,%4$s," +                               // outbound total
            "%5$s,%6$.2f," +                             // error total
            "0,%7$s,0,%9$s,0,%11$s,%17$s,%19$s," +       // inbound description, connect, connectionState, disconnect
            "%13$s,%15$s,0,%21$s," +                     // inbound tunneling, indication
            "%8$s,0,%10$s,0,%12$s,0,%18$s,%20$s," +      // outbound description, connect, connectionState, disconnect
            "%14$s,%16$s,%22$s,0"                        // outbound tunneling, indication
    ),
    /**
     * Statistic format should be in TEXT format
     */
    TEXT("" +
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

    private final String template;

    FileStatisticFormat(final String template) {
        this.template = template;
    }

    @Nonnull
    public String getTemplate() {
        return template;
    }
}
