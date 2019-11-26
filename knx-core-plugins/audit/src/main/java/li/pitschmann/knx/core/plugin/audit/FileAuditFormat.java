package li.pitschmann.knx.core.plugin.audit;

import li.pitschmann.knx.core.utils.Json;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Format for {@link FileAuditPlugin}
 * <p/>
 * JSON and TSV are supported.
 */
public enum FileAuditFormat {
    // @formatter:off
    /**
     * Audit format should be in JSON format.
     */
    JSON(
            // Signal Template (JSON Format)
            "{" +
                    "\"time\":%1$s.%2$s," +
                    "\"type\":%3$s" +
            "}",
            // Body Template (JSON Format)
            "{" +
                "\"time\":%1$s.%2$s," +
                "\"type\":%3$s," +
                "\"header\":{" +
                    "\"totalLength\":%4$s," +
                    "\"raw\":%5$s" +
                "}," +
                "\"body\":{" +
                    "\"service\":{" +
                        "\"code\":%6$s," +
                        "\"text\":%7$s" +
                    "}," +
                    "\"raw\":%8$s" +
                "}" +
            "}",
            // Error Template (JSON Format)
            "{" +
                    "\"time\":%1$s.%2$s," +
                    "\"type\":%3$s," +
                    "\"message\":%4$s," +
                    "\"stacktrace\":%5$s" +
            "}",
            // Escaper for JSON message
            Json::toJson
    ),
    /**
     * Audit format should be in TSV text format
     */
    TSV(
            // Signal Template (TSV Format)
            "%1$s.%2$s\t" +
                "%3$s",                        // type
            // Body Template (TSV Format)
            "%1$s.%2$s\t"+
                    "%3$s\t"+                  // type
                    "%4$s\t" +                 // header.totalLength
                    "%5$s\t" +                 // header.raw
                    "%6$s\t" +                 // body.service.code
                    "%7$s\t" +                 // body.service.text
                    "%8$s",                    // body.raw
            // Error Template (TSV Format)
            "%1$s.%2$s\t" +
                "%3$s"+                        // type
                "\t\t\t\t\t\t" +               // reserved for (body)
                "%4$s\t" +                     // message
                "%5$s",                        // stacktrace
            // Escaper for TSV message
                (obj) -> {
                    return ((obj instanceof Object[]) ? Arrays.toString((Object[])obj) : String.valueOf(obj))
                            // remove all \t
                            .replace("\t", "");
                }
    );
    // @formatter:on

    private final String signalTemplate;
    private final String bodyTemplate;
    private final String errorTemplate;
    private final Function<Object, String> escaper;

    FileAuditFormat(final String signalTemplate, final String bodyTemplate, final String errorTemplate, final Function<Object, String> escaper) {
        this.signalTemplate = signalTemplate;
        this.bodyTemplate = bodyTemplate;
        this.errorTemplate = errorTemplate;
        this.escaper = escaper;
    }

    @Nonnull
    public String getSignalTemplate() {
        return signalTemplate;
    }

    @Nonnull
    public String getBodyTemplate() {
        return bodyTemplate;
    }

    @Nonnull
    public String getErrorTemplate() {
        return errorTemplate;
    }

    @Nonnull
    public String escape(final @Nullable Object str) {
        return str == null ? "" : escaper.apply(str);
    }
}
