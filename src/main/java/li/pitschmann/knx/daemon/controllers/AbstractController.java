package li.pitschmann.knx.daemon.controllers;

import ro.pippo.controller.Controller;

/**
 * Abstract Controller containing common methods for concrete controller
 * implementations.
 */
abstract class AbstractController extends Controller {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String PARAMETER_EXPAND = "expand";

    /**
     * Returns an array of expand parameters which are comma-separated
     *
     * @return array of expand parameters
     */
    protected String[] getExpandParameters() {
        final var expandParameter = getRequest().getParameter(PARAMETER_EXPAND);
        if (expandParameter.isNull()) {
            return EMPTY_STRING_ARRAY;
        } else {
            return expandParameter.toString().split(",");
        }
    }

    /**
     * Checks if the given {@code name} expand parameter is in request.
     * Consider case-sensitivity.
     *
     * @param name
     * @return {@code true} if searched expand parameter exists, otherwise {@code false}
     */
    protected boolean containsExpand(final String name) {
        for (final var expandParameter : getExpandParameters()) {
            if (name.equals(expandParameter)) {
                return true;
            }
        }
        return false;
    }
}
