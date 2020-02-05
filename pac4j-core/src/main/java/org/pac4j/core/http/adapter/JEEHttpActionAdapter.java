package org.pac4j.core.http.adapter;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The HTTP action adapter for the {@link JEEContext}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class JEEHttpActionAdapter implements HttpActionAdapter<Object, JEEContext> {

    public static final JEEHttpActionAdapter INSTANCE = new JEEHttpActionAdapter();

    @Override
    public Object adapt(final HttpAction action, final JEEContext context) {
        if (action != null) {
            int code = action.getCode();
            final HttpServletResponse response = context.getNativeResponse();

            if (code < 400) {
                response.setStatus(code);
            } else {
                try {
                    response.sendError(code);
                } catch (final IOException e) {
                    throw new TechnicalException(e);
                }
            }

            if (action instanceof WithLocationAction) {
                final WithLocationAction withLocationAction = (WithLocationAction) action;
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, withLocationAction.getLocation());

            } else if (action instanceof WithContentAction) {
                final WithContentAction withContentAction = (WithContentAction) action;
                final String content = withContentAction.getContent();

                if (content != null) {
                    try {
                        response.getWriter().write(content);
                    } catch (final IOException e) {
                        throw new TechnicalException(e);
                    }
                }
            }

            return null;
        }

        throw new TechnicalException("No action provided");
    }

    /**
     * Return the best HTTP action adapter: first, the local one if defined or the one from the config if defined
     * or otherwise the default one.
     *
     * @param localAdapter a local adapter
     * @param config the pac4j config
     * @return the most appropriate adapter
     */
    public static HttpActionAdapter<Object, JEEContext> findBestAdapter(final HttpActionAdapter<Object, JEEContext> localAdapter,
                                                                        final Config config) {
        if (localAdapter != null) {
            return localAdapter;
        } else if (config.getHttpActionAdapter() != null) {
            return config.getHttpActionAdapter();
        } else {
            return INSTANCE;
        }
    }
}