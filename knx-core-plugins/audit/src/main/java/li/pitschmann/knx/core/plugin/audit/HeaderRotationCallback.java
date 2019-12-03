package li.pitschmann.knx.core.plugin.audit;

import com.vlkan.rfos.RotationCallback;
import com.vlkan.rfos.policy.RotationPolicy;
import li.pitschmann.knx.core.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Rotation Callback for files that requires header line (e.g. TSV files)
 * <p/>
 * This callback is used to append a header when a new file is opened or rotated.
 *
 * @author PITSCHR
 */
final class HeaderRotationCallback implements RotationCallback {
    private static final Logger log = LoggerFactory.getLogger(HeaderRotationCallback.class);
    private byte[] headerLineAsBytes;

    /**
     * (package-protected) Creates {@link RotationCallback} for TSV files.
     *
     * @param headerLine the header for TSV file that should be applied for each rotation file
     */
    HeaderRotationCallback(final @Nonnull String headerLine) {
        this.headerLineAsBytes = Bytes.concat(
                headerLine.getBytes(StandardCharsets.UTF_8),
                System.lineSeparator().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public void onTrigger(RotationPolicy rotationPolicy, Instant instant) {
        // NO-OP
    }

    @Override
    public void onOpen(RotationPolicy rotationPolicy, Instant instant, OutputStream outputStream) {
        try {
            outputStream.write(headerLineAsBytes);
        } catch (final IOException ex) {
            log.error("Something went wrong writing the header. Permission issue?", ex);
        }
    }

    @Override
    public void onClose(RotationPolicy rotationPolicy, Instant instant, OutputStream outputStream) {
        // NO-OP
    }

    @Override
    public void onSuccess(RotationPolicy rotationPolicy, Instant instant, File file) {
        // NO-OP
    }

    @Override
    public void onFailure(RotationPolicy rotationPolicy, Instant instant, File file, Exception e) {
        // NO-OP
    }
}
