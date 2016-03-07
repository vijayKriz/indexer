package com.rest.indexer.common;

/**
 * Created by vijay on 3/5/16.
 */

import com.rest.indexer.entity.Data;
import com.rest.indexer.entity.Scrub;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.*;

@Slf4j
public class SparkHandler {

    public static Scrub indexCsv(String fileName) {
        BufferedReader br = null;
        String line = "";
        List<Data> financialRecords = new ArrayList<>();
        List<String[]> rows = new ArrayList<>();
        try {
            Map<String, String> maps = new HashMap<String, String>();
            br = new BufferedReader(new FileReader(fileName));
            List<String> header = Arrays.asList(br.readLine().split(","));
            while (header != null && header.size() > 6 && (line = br.readLine()) != null) {
                Data data = new Data();
                String[] row = line.split(",");
                rows.add(row);
                data.setYear(row[header.indexOf("Year")]);
                data.setMonth(row[header.indexOf("Month")]);
                data.setFund_id(Integer.parseInt(row[header.indexOf("Fund ID")]));
                data.setDepartment_id(Integer.parseInt(row[header.indexOf("Department ID")]));
                data.setFund_name(row[header.indexOf("Fund Name")]);
                data.setDepartment_name(row[header.indexOf("Department Name")]);
                data.setAmount(Double.parseDouble(row[header.indexOf("Amount")]));
                financialRecords.add(data);
            }
        } catch (IOException e) {

        }
        Map<String, Map<String, List<Data>>> revenueByDept = financialRecords.stream().filter(x -> x.getAmount() >= 0).collect(Collectors.groupingBy(Data::getYear, Collectors.groupingBy(Data::getDepartment_name)));
        Map<String, Map<String, List<Data>>> expenseByDept = financialRecords.stream().filter(x -> x.getAmount() < 0).collect(Collectors.groupingBy(Data::getYear, Collectors.groupingBy(Data::getDepartment_name)));
        Set<String> depts = financialRecords.stream().map(x -> x.getDepartment_name()).collect(Collectors.toSet());
       // Double totalRevenue = financialRecords.stream().filter(x -> x.getAmount() > 0).mapToDouble(x -> x.getAmount()).sum();
       // Double totalExpenses = financialRecords.stream().filter(x -> x.getAmount() < 0).mapToDouble(x -> x.getAmount()).sum();
        Set<String> revenueYears = revenueByDept.keySet();
        Set<String> expenseYears = expenseByDept.keySet();
        Set<String> years = new HashSet<>();
        years.addAll(revenueYears);
        years.addAll(expenseYears);
        Map<String, Object> aggregations = new HashMap<>();
        for (String year : years) {
            Map<String, Object> yearFunds = new HashMap<>();
            Map<String, Object> yearRevenuesFunds = new HashMap<>();
            Map<String, Object> yearExpenseFunds = new HashMap<>();
            getYearlyData(yearRevenuesFunds, revenueByDept.get(year), year, depts);
            yearFunds.put("revenues", yearRevenuesFunds);
            getYearlyData(yearExpenseFunds, expenseByDept.get(year), year, depts);
            yearFunds.put("expenses", yearExpenseFunds);
            aggregations.put(year, yearFunds);
        }
        HelperUtil.deleteFile(fileName);
        return new Scrub(rows, aggregations);
    }

    public static void getYearlyData(Map<String, Object> yearRevenuesFunds, Map<String, List<Data>> fundByDept, String year, Set<String> depts) {
        Map<String, Double> deptExpense = new HashMap<>();
        Double expenseByYear = 0D;
        if(fundByDept == null) fundByDept = new HashMap();;
        for(Map.Entry<String, List<Data>> entry: fundByDept.entrySet()) {
            deptExpense.put(entry.getKey(),entry.getValue().stream().mapToDouble(x -> x.getAmount()).sum());
            expenseByYear += entry.getValue().stream().mapToDouble(x -> x.getAmount()).sum();
        }
        for(String dept: depts){
            if (deptExpense.get(dept) ==null) deptExpense.put(dept, 0.0D);
        }

        HashMap fundTotal = new HashMap();
        fundTotal.put("General", expenseByYear);
        yearRevenuesFunds.put("funds", fundTotal);
        yearRevenuesFunds.put("departments", deptExpense);
        yearRevenuesFunds.put("total", expenseByYear);
    }

}

