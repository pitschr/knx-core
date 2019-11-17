package li.pitschmann.knx.plugins.statistic;

import com.vlkan.rfos.RotatingFileOutputStream;
import com.vlkan.rfos.RotationConfig;
import com.vlkan.rfos.policy.DailyRotationPolicy;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.ConnectResponseBody;
import li.pitschmann.knx.link.body.ConnectionStateRequestBody;
import li.pitschmann.knx.link.body.ConnectionStateResponseBody;
import li.pitschmann.knx.link.body.DescriptionRequestBody;
import li.pitschmann.knx.link.body.DescriptionResponseBody;
import li.pitschmann.knx.link.body.DisconnectRequestBody;
import li.pitschmann.knx.link.body.DisconnectResponseBody;
import li.pitschmann.knx.link.body.RoutingIndicationBody;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.config.EnumConfigValue;
import li.pitschmann.knx.link.plugin.config.LongConfigValue;
import li.pitschmann.knx.link.plugin.config.PathConfigValue;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Executors;
import li.pitschmann.utils.Sleeper;
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
    public static final PathConfigValue PATH = new PathConfigValue("path", () -> Paths.get("."), null);
    /**
     * File format (e.g. JSON, TEXT)
     */
    public static final EnumConfigValue<FileStatisticFormat> FORMAT = new EnumConfigValue<>("format", FileStatisticFormat.class, () -> FileStatisticFormat.JSON);
    /**
     * Interval in milliseconds. Default 60000ms (1 min). Minimum 10000ms (10 sec).
     */
    public static final LongConfigValue INTERVAL = new LongConfigValue("intervalMs", () -> 60000L, x -> x >= 10000);

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
        path = client.getConfig().getSetting(FileStatisticPlugin.PATH);
        format = client.getConfig().getSetting(FileStatisticPlugin.FORMAT);
        final var intervalMs = client.getConfig().getSetting(FileStatisticPlugin.INTERVAL);

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
