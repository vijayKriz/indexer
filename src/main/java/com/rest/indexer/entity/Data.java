package com.rest.indexer.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Created by vijay on 3/5/16.
 */

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Data {
    private String year;
    private String month;
    private int fund_id;
    private int department_id;
    private String fund_name;
    private String department_name;
    private double amount;
}
