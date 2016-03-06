package com.rest.indexer.service;

import com.rest.indexer.common.SparkHandler;
import com.rest.indexer.entity.Scrub;
import org.springframework.stereotype.Service;

/**
 *
 * @author vijay | Scrub service to generate indexing object
 */
@Service
public class ScrubService {

    public Scrub index(String csvFileLocation) {
        return SparkHandler.indexCsv(csvFileLocation);
    }

}
