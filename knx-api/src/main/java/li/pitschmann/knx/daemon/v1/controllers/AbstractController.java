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

/**
 * Abstract Controller containing common methods for concrete controller
 * implementations.
 */
@Path("/api/v1")
abstract class AbstractController extends Controller {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String PARAMETER_EXPAND = "expand";

    @Inject
    private XmlProject xmlProject;

    @Inject
    private DefaultKnxClient knxClient;

    /**
     * Returns an array of expand parameters which are comma-separated
     *
     * @return array of expand parameters
     */
    protected String[] getExpandParameters() {
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
    protected boolean containsExpand(final @Nonnull String name) {
        Preconditions.checkNotNull(name);
        for (final var expandParameter : getExpandParameters()) {
            if ("*".equals(expandParameter) || name.equals(expandParameter)) {
                return true;
            }
        }
        return false;
    }

    public XmlProject getXmlProject() {
        return xmlProject;
    }

    public DefaultKnxClient getKnxClient() {
        return knxClient;
    }
}
