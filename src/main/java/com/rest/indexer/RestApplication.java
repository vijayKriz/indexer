package com.rest.indexer;

import org.glassfish.jersey.server.ResourceConfig;

import com.rest.indexer.filter.CORSResponseFilter;
import com.rest.indexer.filter.LoggingResponseFilter;

/**
 * Created by vijay on 3/05/16.
 * Registers the components to be used by the JAX-RS application
 */
public class RestApplication extends ResourceConfig {

    /**
     * Register JAX-RS application components.
     */
    public RestApplication() {
        packages("com.rest.indexer");
        register(LoggingResponseFilter.class);
        register(CORSResponseFilter.class);
    }
}
