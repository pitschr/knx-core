package li.pitschmann.knx.core.plugin.statistic;

import com.vlkan.rfos.RotatingFileOutputStream;
import com.vlkan.rfos.RotationConfig;
import com.vlkan.rfos.policy.DailyRotationPolicy;
import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectResponseBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DescriptionResponseBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.ExtensionPlugin;
import li.pitschmann.knx.core.plugin.EnumConfigValue;
import li.pitschmann.knx.core.plugin.LongConfigValue;
import li.pitschmann.knx.core.plugin.PathConfigValue;
import li.pitschmann.knx.core.utils.Closeables;
import li.pitschmann.knx.core.utils.Executors;
import li.pitschmann.knx.core.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Statistic plug-in to write the statistic to a file
 * <p/>
 * You can control the file path and how often the statistic
 * should be printed (interval).
 *
 * @author PITSCHR
 */
public final class FileStatisticPlugin implements ExtensionPlugin {
    /**
     * File path
     */
    public static final PathConfigValue PATH = new PathConfigValue("path", () -> Paths.get("knx-statistic.log"), null);
    /**
     * File format (e.g. JSON, TEXT)
     */
    public static final EnumConfigValue<FileStatisticFormat> FORMAT = new EnumConfigValue<>("format", FileStatisticFormat.class, () -> FileStatisticFormat.JSON);
    /**
     * Interval in milliseconds. Default 5 minutes (300000ms) Minimum 10 sec (10000ms).
     */
    public static final LongConfigValue INTERVAL_MS = new LongConfigValue("intervalMs", () -> 5 * 60 * 1000L, x -> x >= 10000);

    private static final Logger log = LoggerFactory.getLogger(FileStatisticPlugin.class);
    private static final String FILE_ROLLOVER_PATTERN = "-%d{yyyyMMdd-HHmmss-SSS}";

    private final ExecutorService executor = Executors.newSingleThreadExecutor(true);
    private KnxClient client;
    private Path path;
    private FileStatisticFormat format;
    private RotatingFileOutputStream fos;

    @Override
    public void onInitialization(final @Nonnull KnxClient client) {
        // configurations
        path = client.getConfig(FileStatisticPlugin.PATH);
        format = client.getConfig(FileStatisticPlugin.FORMAT);
        final var intervalMs = client.getConfig(FileStatisticPlugin.INTERVAL_MS);

        final var baseFile = path.toString();

        // get file pattern for rollover
        final var lastExtensionDotPosition = baseFile.lastIndexOf('.');
        final var rolloverFile = new StringBuilder()
                .append(baseFile, 0, lastExtensionDotPosition)
                .append(FILE_ROLLOVER_PATTERN)
                .append(baseFile.substring(lastExtensionDotPosition))
                .toString();

        final var config = RotationConfig
                .builder()
                .file(baseFile)
                .filePattern(rolloverFile)
                .policy(DailyRotationPolicy.getInstance())
                .append(true)
                .build();

        // start rollover stream
        fos = new RotatingFileOutputStream(config);

        this.client = Objects.requireNonNull(client);
        executor.execute(new FileStatisticIntervalWriter(intervalMs));
        executor.shutdown();
    }

    @Override
    public void onStart() {
        // NO-OP
    }

    @Override
    public void onShutdown() {
        // close the executor
        Closeables.shutdownQuietly(executor);

        // print last statistic
        writeToStatisticFile();

        // close the rollover stream
        Closeables.closeQuietly(fos);
    }

    /**
     * Writes the statistic to file
     */
    private void writeToStatisticFile() {
        final var statistics = this.client.getStatistic();
        final var statisticsFormatted = String.format( //
                format.getTemplate(),

                statistics.getNumberOfBodyReceived(), // %1
                statistics.getNumberOfBytesReceived(), // %2
                statistics.getNumberOfBodySent(), // %3
                statistics.getNumberOfBytesSent(), // %4
                statistics.getNumberOfErrors(), // %5
                statistics.getErrorRate(), // %6
                // Description
                statistics.getNumberOfBodyReceived(DescriptionResponseBody.class), // %7
                statistics.getNumberOfBodySent(DescriptionRequestBody.class), // %8
                // Connect
                statistics.getNumberOfBodyReceived(ConnectResponseBody.class), // %9
                statistics.getNumberOfBodySent(ConnectRequestBody.class), // %10
                // Connection State
                statistics.getNumberOfBodyReceived(ConnectionStateResponseBody.class), // %11
                statistics.getNumberOfBodySent(ConnectionStateRequestBody.class), // %12
                // Tunneling
                statistics.getNumberOfBodyReceived(TunnelingRequestBody.class), // %13
                statistics.getNumberOfBodySent(TunnelingRequestBody.class), // %14
                statistics.getNumberOfBodyReceived(TunnelingAckBody.class), // %15
                statistics.getNumberOfBodySent(TunnelingAckBody.class), // %16
                // Disconnect
                statistics.getNumberOfBodyReceived(DisconnectRequestBody.class), // %17
                statistics.getNumberOfBodySent(DisconnectRequestBody.class), // %18
                statistics.getNumberOfBodyReceived(DisconnectResponseBody.class), // %19
                statistics.getNumberOfBodySent(DisconnectResponseBody.class), // %20
                // Indication
                statistics.getNumberOfBodyReceived(RoutingIndicationBody.class), // %21
                statistics.getNumberOfBodySent(RoutingIndicationBody.class) // %22
        );
        // @formatter:on

        try {
            fos.write(statisticsFormatted.getBytes(StandardCharsets.UTF_8));
            fos.write(System.lineSeparator().getBytes());
        } catch (final IOException e) {
            log.error("Error writing to audit file '{}': {}", path, statisticsFormatted, e);
        }
    }

    /**
     * Runnable to print statistic in every interval
     *
     * @author PITSCHR
     */
    private class FileStatisticIntervalWriter implements Runnable {
        private final long intervalMs;

        private FileStatisticIntervalWriter(final long intervalMilliseconds) {
            this.intervalMs = intervalMilliseconds;
        }

        @Override
        public void run() {
            do {
                FileStatisticPlugin.this.writeToStatisticFile();
            } while (Sleeper.milliseconds(this.intervalMs));
        }
    }
}
