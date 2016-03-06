package com.rest.indexer.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * Created by vijay on 3/05/16.
 */

public class CORSResponseFilter
implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {

        final String originReq = requestContext.getHeaderString("Origin") != null ? requestContext.getHeaderString("Origin") : "";

        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();

        headers.add("Access-Control-Allow-Credentials", true);
        headers.add("Access-Control-Allow-Origin", originReq);
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.add("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Host, Authorization");
    }

}
