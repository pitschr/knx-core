package li.pitschmann.knx.core.plugin.api.v1.controllers;

import com.google.inject.Inject;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.knxproj.XmlProject;
import li.pitschmann.knx.core.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Controller;
import ro.pippo.controller.Path;

import li.pitschmann.knx.core.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract Controller containing common methods for concrete controller
 * implementations.
 */
@Path("/api/v1")
abstract class AbstractController extends Controller {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private XmlProject xmlProject;

    @Inject
    private KnxClient knxClient;

    @Nullable
    public final XmlProject getXmlProject() {
        return xmlProject;
    }

    public final KnxClient getKnxClient() {
        return knxClient;
    }

    /**
     * Returns a range of {@code T} elements from list.
     * May be limited using {@code start} and {@code limit} request parameters.
     *
     * @param list
     * @param <T>
     * @return a new list of elements from {@link Collection}
     */
    protected final <T> List<T> limitAndGetAsList(final Collection<T> list) {
        final int start = getRequest().getParameter("start").toInt(0);
        final int limit = getRequest().getParameter("limit").toInt(Integer.MAX_VALUE);
        Preconditions.checkArgument(start >= 0, "Start should be 0 or greater: {}", start);
        Preconditions.checkArgument(limit >= 0, "Limit should be 0 or greater: {}", limit);

        if (start == 0 && limit == Integer.MAX_VALUE) {
            log.trace("No range defined.");
            // no limit
            return new ArrayList<>(list);
        } else {
            log.trace("Range defined: start={}, limit={}", start, limit);
            // limit
            return list.stream().skip(start).limit(limit).collect(Collectors.toList());
        }
    }
}
