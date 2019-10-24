package li.pitschmann.knx.link.plugin.monitor;

import com.google.common.base.Strings;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.RoutingIndicationBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.body.cemi.MessageCode;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.datapoint.DPT8;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TTY Monitor Plugin
 * <p>
 * This plugin is designed to for monitoring
 */
public final class TTYMonitorPlugin implements ObserverPlugin, ExtensionPlugin {
    private static final Logger log = LoggerFactory.getLogger(TTYMonitorPlugin.class);
    private static final int DEFAULT_SIZE_COLUMN = 80;
    private static final int DEFAULT_SIZE_LINES = 20;
    private static final ExecutorService es = Executors.newFixedThreadPool(4);
    private final PrintStream out;
    private final int columns;
    private final int lines;
    private final AtomicBoolean emptyTable = new AtomicBoolean(true);
    private final AtomicInteger numberOfIncomingBodies = new AtomicInteger();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
    private KnxClient knxClient;

    public TTYMonitorPlugin() {
        this(System.out);
    }

    public TTYMonitorPlugin(final PrintStream out) {
        this(out, getTerminalColumns(), getTerminalLines());
    }

    public TTYMonitorPlugin(final PrintStream out, final int columns, final int lines) {
        this.out = out;
        this.columns = columns;
        this.lines = lines;
        log.info("Terminal initialized with: out={}, columns={}, lines={}", this.out, this.columns, this.lines);
    }

    /**
     * Returns the column size of terminal, falls back to {@link #DEFAULT_SIZE_COLUMN} if it could not
     * obtained for some reasons.
     *
     * @return width of terminal in number of columns
     */
    private static final int getTerminalColumns() {
        final var strColumns = getTerminalOutput("tput cols");
        return strColumns == null ? DEFAULT_SIZE_COLUMN : Integer.valueOf(strColumns);
    }

    /**
     * Returns the lines size of terminal, falls back to {@link #DEFAULT_SIZE_LINES} if it could not
     * obtained for some reasons.
     *
     * @return height of terminal in number of lines
     */
    private static final int getTerminalLines() {
        final var strLines = getTerminalOutput("tput lines");
        return strLines == null ? DEFAULT_SIZE_LINES : Integer.valueOf(strLines);
    }

    /**
     * Internal helper to get the terminal output.
     * <p>
     * See: {@link #getTerminalColumns()} and {@link #getTerminalLines()}
     *
     * @param command
     * @return output from terminal, {@code null} if something went wrong
     */
    @Nullable
    private static final String getTerminalOutput(final @Nonnull String command) {
        final var pb = new ProcessBuilder().command("bash", "-c", command + " 2> /dev/tty");
        try {
            final var process = pb.start();
            try (final var in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = in.readLine();
                if (line != null) {
                    return line;
                }
            }
        } catch (final IOException ioe) {
            log.error("I/O Exception during terminal output: ", command, ioe);
        }
        return null;
    }

    private final String getHeader() {
        return "  #        | Date / Time         | Source    | Target    | Type     | Hex Value";
    }

    private final String getHeaderSeparator() {
        return "-----------+---------------------+-----------+-----------+----------+" + Strings.repeat("-", columns - 70);
    }

    private final String getEmptyLine() {
        return "           |                     |           |           |          |";
    }

    @Override
    public void onInitialization(final KnxClient knxClient) {
        this.knxClient = knxClient;
    }

    @Override
    public void onStart() {
        // creates the screen
        printInitialScreen();

        // define scroll area
        out.print("\033[5;" + (lines - 3) + "r\033[5;0H\0337");
        es.execute(new TimeRunnable());
        // es.execute(new DataRunnable());
    }

    @Override
    public void onShutdown() {
        es.shutdownNow();
        out.println("\033[r\033[1J\033[2J");
    }

    @Override
    public void onIncomingBody(@Nonnull Body item) {
        if (item instanceof TunnelingRequestBody
                || item instanceof RoutingIndicationBody) {
            printLineInTable(item);
        }
    }

    @Override
    public void onOutgoingBody(@Nonnull Body item) {
        // NO-OP
    }

    @Override
    public void onError(@Nonnull Throwable throwable) {
        printToTerminal(String.format("[ ERROR ] %s", throwable.getMessage()), "\033[K\033[0;31m");
    }

    /**
     * Prints the initial screen, containing header, footer and empty table
     */
    private final void printInitialScreen() {
        // clear whole screen
        out.print("\033[r\033[0;0H\033[2J");

        // print initial screen
        out.println(String.format("\033[0;0HKNX MONITOR (%s x %s, Routing: %s, NAT: %s)", columns, lines, knxClient.getConfig().isRoutingEnabled(), knxClient.getConfig().isNatEnabled()));
        out.println();
        out.println("\033[1;32m" + getHeader() + "\033[0m");
        out.println("\033[1;32m" + getHeaderSeparator() + "\033[0m");

        for (int i = 0; i < lines - 5 - 2; i++) {
            out.println("\033[0;32m" + getEmptyLine() + "\033[0m");
        }
        out.println("\033[1;32m" + getHeaderSeparator() + "\033[0m");
        out.println("Press CTRL+C to quit");
    }

    /**
     * Print line in table
     *
     * @param item
     */
    private final void printLineInTable(final Body item) {
        final var sb = new StringBuilder();
        sb.append(String.format("%10s", numberOfIncomingBodies.incrementAndGet()))
                .append(" | ")
                .append(String.format("%19s", dateTimeFormatter.format(LocalDateTime.now())))
                .append(" | ");

        final CEMI cemi;
        if (item instanceof TunnelingRequestBody) {
            cemi = ((TunnelingRequestBody) item).getCEMI();
        } else if (item instanceof RoutingIndicationBody) {
            cemi = ((RoutingIndicationBody) item).getCEMI();
        } else {
            throw new AssertionError();
        }

        sb.append(String.format("%9s", cemi.getSourceAddress().getAddress()))
                .append(" | ");
        if (cemi.getDestinationAddress() instanceof GroupAddress) {
            final var ga = (GroupAddress) cemi.getDestinationAddress();
            sb.append(String.format("%9s", ga.getAddressLevel3()));
        }

        final String apciString;
        switch (cemi.getApci()) {
            case GROUP_VALUE_READ:
                apciString = "Read";
                break;
            case GROUP_VALUE_WRITE:
                apciString = "Write";
                break;
            case GROUP_VALUE_RESPONSE:
                apciString = "Response";
                break;
            default:
                apciString = "N/A";
        }

        sb.append(" | ")
                .append(String.format("%8s", apciString))
                .append(" | ")
                .append(ByteFormatter.formatHexAsString(cemi.getApciData()));
        printToTerminal(sb.toString());
    }

    public final void printToTerminal(final String str) {
        printToTerminal(str, "\033[0;32m");
    }


    private final void printToTerminal(final String str, final String escapeCode) {
        out.print(String.format("\0338\033[K%s%s%s\033[0m\0337", escapeCode, emptyTable.getAndSet(false) ? "" : System.lineSeparator(), str));
    }

    /**
     * Runnable for updating the time
     */
    private class TimeRunnable implements Runnable {
        private final String timePattern = "yyyy-MM-dd HH:mm:ss";
        private final String timePosition = "\033[1;70H\033[K\033[" + (columns - timePattern.length()) + "G";

        @Override
        public void run() {
            final var pattern = DateTimeFormatter.ofPattern(timePattern);

            do {
                final var nowStr = pattern.format(LocalDateTime.now());
                System.out.print(String.format("\0338%s%s\0338", timePosition, nowStr));
            } while (Sleeper.seconds(1));
        }
    }


    /**
     * Runnable for fake data (helpful for debugging purposes)
     */
    private class DataRunnable implements Runnable {
        private final CEMI dummyCEMI = CEMI.useDefault(MessageCode.L_DATA_IND, GroupAddress.of(1, 2, 3), APCI.GROUP_VALUE_WRITE, DPT8.VALUE_2_OCTET_COUNT.toValue(4711));
        private final Body dummyBody = TunnelingRequestBody.of(1, 1, dummyCEMI);

        @Override
        public void run() {
            do {
                printLineInTable(dummyBody);
            } while (Sleeper.milliseconds(10));
        }
    }
}
