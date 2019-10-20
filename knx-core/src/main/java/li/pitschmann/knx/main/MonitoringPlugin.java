package li.pitschmann.knx.main;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.ConnectRequestBody;
import li.pitschmann.knx.link.body.RoutingIndicationBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.parser.KnxprojParser;
import li.pitschmann.knx.parser.XmlProject;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Sleeper;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class MonitoringPlugin implements ObserverPlugin, ExtensionPlugin {
    private final PrintStream out;
    private final AtomicInteger numberOfIncomingBodies = new AtomicInteger();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
    private XmlProject xmlProject;

    public MonitoringPlugin() {
        this(System.out);
    }

    public MonitoringPlugin(final PrintStream out) {
        this.out = out;

        try {
            Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "tput cols 2> /dev/tty"});
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("cols=" + line);
            }
            p.waitFor();
            in.close();

            Process p2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", "tput lines 2> /dev/tty"});
            BufferedReader in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
            String line2;
            while ((line2 = in2.readLine()) != null) {
                System.out.println("lines=" + line2);
            }
            p.waitFor();
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("columns: " + System.getenv("COLUMNS"));
        System.out.println("lines: " + System.getenv("LINES"));

        Sleeper.seconds(10);
    }

    private static final String getHeader() {
        return "  #      | Date / Time         | Source    | Target    | Type     | Hex Value";
    }

    private static final String getHeaderSeparator() {
        return "---------+---------------------+-----------+-----------+----------+-------------------------------------------";
    }

    private static final String getEmptyLine() {
        return "         |                     |           |           |          |";
    }


    @Override
    public void onInitialization(KnxClient client) {
        out.println(String.format("\033[0;37mClient initialized (%s)\033[0m", client));

        final var knxprojFile = client.getConfig().getProjectPath();
        if (Files.isReadable(knxprojFile)) {
            xmlProject = KnxprojParser.parse(knxprojFile);
            out.println(String.format("\033[0;37mKNXPROJ File: %s\033[0m", client.getConfig().getProjectPath()));
        } else {
            out.println("\033[0;37mNo KNXPROJ file provided.\033[0m");
        }
    }

    @Override
    public void onStart() {
        out.println("\033[0;37mClient started.\033[0m");
        printInitialScreen();
    }

    @Override
    public void onShutdown() {
        out.println("\033[0;37mClient stopped.\033[0m");
    }

    @Override
    public void onIncomingBody(@Nonnull Body item) {
        if (item instanceof TunnelingRequestBody
                || item instanceof RoutingIndicationBody) {
            printLineInTable(item);
        } else if (item instanceof ConnectRequestBody) {
            out.println("\033[0;32m" + item + "\033[0m");
        } else {
            out.println(item);
        }
    }

    @Override
    public void onOutgoingBody(@Nonnull Body item) {
        // out.println(String.format(String.format("\033[0;37m<- OUTGOING] %s: %s\033[0m", item.getServiceType().getFriendlyName(), item.getRawDataAsHexString()));
    }

    @Override
    public void onError(@Nonnull Throwable throwable) {
        out.println(String.format("\033[0;31m[ ERROR  ] %s\033[0m", throwable.getMessage()));
    }

    /**
     * Prints the initial screen, containing header, footer and empty table
     */
    private final void printInitialScreen() {
        // clear whole screen
        out.println("\033[0;0H\033[2J");

        out.println();
        out.println("\033[1;32m" + getHeader() + "\033[0m");
        out.println("\033[0;32m" + getHeaderSeparator() + "\033[0m");

        for (int i = 0; i < 10; i++) {
            out.println("\033[0;32m" + getEmptyLine() + "\033[0m");
        }
        out.println("\033[0;32m" + getHeaderSeparator() + "\033[0m");
        out.println();
        out.println("[Q]uit");
    }

    /**
     * Print line in table
     *
     * @param item
     */
    private final void printLineInTable(final Body item) {
        final var sb = new StringBuilder();
        sb.append("\033[0;32m")
                .append(String.format("%8s", numberOfIncomingBodies.incrementAndGet()))
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
                .append(ByteFormatter.formatHexAsString(cemi.getApciData()))
                .append("\033[0m");
        out.println(sb);
    }
}
