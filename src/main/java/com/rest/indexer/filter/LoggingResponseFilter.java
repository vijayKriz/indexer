package com.rest.indexer.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by vijay on 3/05/16.
 */
@Slf4j
public class LoggingResponseFilter
implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext,
            final ContainerResponseContext responseContext) throws IOException {
        final String method = requestContext.getMethod();
        log.debug("Requesting " + method + " for path " + requestContext.getUriInfo().getPath());
    }

}