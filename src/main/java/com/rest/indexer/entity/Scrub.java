package com.rest.indexer.entity;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.Date;

/**
 * Created by vijay on 2/15/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scrub{

    private static final long serialVersionUID = -1284654508625552327L;

    private Object[] rows_parsed;

    private Object aggregations;

}
