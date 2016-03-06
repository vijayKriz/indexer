package com.rest.indexer.resource;

import com.rest.indexer.common.HelperUtil;
import com.rest.indexer.entity.Scrub;
import com.rest.indexer.service.ScrubService;
import com.wordnik.swagger.annotations.Api;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created by vijay on 3/05/16.
 */

@Path("scrub")
@Produces({ MediaType.APPLICATION_JSON })
@Resource
@Api(value = "/scrub", description = "indexing files.")
public class ScrubResource {

    @Autowired
    @Getter
    @Setter
    private ScrubService service;

    @POST
    public Scrub indexing(final InputStream inputStream) throws Exception {
        String tempFile = System.currentTimeMillis()+".csv";
        HelperUtil.convertInputToFile(inputStream, tempFile);
        Scrub scrub = service.index(tempFile);
        return scrub;
    }

}
