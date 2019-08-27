package li.pitschmann.knx.daemon.v1.controllers;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.parser.XmlProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Controller;
import ro.pippo.controller.Path;

import javax.annotation.Nonnull;
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
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String PARAMETER_EXPAND = "expand";
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private XmlProject xmlProject;

    @Inject
    private DefaultKnxClient knxClient;


    public final XmlProject getXmlProject() {
        return xmlProject;
    }

    public final DefaultKnxClient getKnxClient() {
        return knxClient;
    }

    /**
     * Returns an array of expand parameters which are comma-separated
     *
     * @return array of expand parameters
     */
    protected final String[] getExpandParameters() {
        final var expandParameter = getRequest().getParameter(PARAMETER_EXPAND);
        if (expandParameter == null || expandParameter.isNull()) {
            return EMPTY_STRING_ARRAY;
        } else {
            return expandParameter.toString().split(",");
        }
    }

    /**
     * Checks if the given {@code name} expand parameter is in request.
     * Consider case-sensitivity.
     * <p/>
     * Special rule: if expand parameter contains '*' (star) then all parameters are subject to be considered
     * and therefore the method will always return {@code true} in this case.
     *
     * @param name
     * @return {@code true} if searched expand parameter exists, otherwise {@code false}
     */
    protected final boolean containsExpand(final @Nonnull String name) {
        Preconditions.checkNotNull(name);
        for (final var expandParameter : getExpandParameters()) {
            if ("*".equals(expandParameter) || name.equals(expandParameter)) {
                return true;
            }
        }
        return false;
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
        Preconditions.checkArgument(start >= 0, "Start should be 0 or greater: %s", start);
        Preconditions.checkArgument(limit >= 0, "Limit should be 0 or greater: %s", limit);

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
