package com.rest.indexer.common;

/**
 * Created by vijay on 3/5/16.
 */

import com.rest.indexer.entity.Data;
import com.rest.indexer.entity.Scrub;
import lombok.extern.slf4j.Slf4j;
/*
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
*/

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
        List<String> rows = new ArrayList<>();
        try {
            Map<String, String> maps = new HashMap<String, String>();
            br = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            while (header != null && (line = br.readLine()) != null) {
                Data data = new Data();
                rows.add(line);
                String row[] = line.split(",");
                data.setYear(row[0]);
                data.setMonth(row[1]);
                data.setFund_id(Integer.parseInt(row[2]));
                data.setDepartment_id(Integer.parseInt(row[3]));
                data.setFund_name(row[4]);
                data.setDepartment_name(row[5]);
                data.setAmount(Double.parseDouble(row[6]));
                financialRecords.add(data);
            }
        } catch (IOException e) {

        }
        /*SparkConf conf = new SparkConf().setMaster("local[4]").setAppName("indexer");
        JavaSparkContext context = new JavaSparkContext(conf);
        SQLContext sqlCtxt = new SQLContext(context.sc());
        DataFrame revenueData = sqlCtxt.read().format("com.databricks.spark.csv").option("inferSchema", "true")
                .option("header", "true").load(fileName);
        revenueData.registerTempTable("fileData");
        Row[] row = revenueData.collect();
        revenueData = sqlCtxt.sql("select year, month, fund_id, department, fund_name, department, amount from fileData where amount > 0");
        DataFrame sum_revenueData_dept = revenueData.groupBy("year, department").sum("amount");
        DataFrame count_revenueData_dept = sum_revenueData_dept.withColumnRenamed("sum(amount)", "amount");
        List<Data> revenueByDept = count_revenueData_dept.javaRDD().map(new Function<Row, Data>() {
            public Data call(Row row) {
                Data data = new Data();
                data.setYear(row.getString(0));
                data.setMonth(row.getString(1));
                data.setFund_id(row.getInt(2));
                data.setDepartment_id(row.getInt(3));
                data.setFund_name(row.getString(4));
                data.setDepartment_name(row.getString(5));
                data.setAmount(row.getDouble(6));
                return data;
            }
        }).collect();
        DataFrame expenseData = sqlCtxt.sql("select year, month, fund_id, department, fund_name, department, amount from fileData where amount < 0");
        DataFrame sum_expenseData_dept = expenseData.groupBy("year, department").sum("amount");
        DataFrame count_expenseData_dept = sum_expenseData_dept.withColumnRenamed("sum(amount)", "amount");

        List<Data> expenseByDept = count_expenseData_dept.javaRDD().map(new Function<Row, Data>() {
            public Data call(Row row) {
                Data data = new Data();
                data.setYear(row.getString(0));
                data.setMonth(row.getString(1));
                data.setFund_id(row.getInt(2));
                data.setDepartment_id(row.getInt(3));
                data.setFund_name(row.getString(4));
                data.setDepartment_name(row.getString(5));
                data.setAmount(row.getDouble(6));
                return data;
            }
        }).collect();
        */
        Map<String, Map<String, List<Data>>> revenueByDept = financialRecords.stream().filter(x -> x.getAmount() > 0).collect(Collectors.groupingBy(Data::getYear, Collectors.groupingBy(Data::getDepartment_name)));
        Map<String, Map<String, List<Data>>> expenseByDept = financialRecords.stream().filter(x -> x.getAmount() < 0).collect(Collectors.groupingBy(Data::getYear, Collectors.groupingBy(Data::getDepartment_name)));
       // List<Data> expenseByDept = financialRecords.stream().filter(x -> x.getAmount() < 0).collect(Collectors.toList());
        Double totalRevenue = financialRecords.stream().filter(x -> x.getAmount() > 0).mapToDouble(x -> x.getAmount()).sum();
        Double totalExpenses = financialRecords.stream().filter(x -> x.getAmount() < 0).mapToDouble(x -> x.getAmount()).sum();
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
            getYearlyData(yearRevenuesFunds, revenueByDept.get(year), year, totalRevenue);
            yearFunds.put("revenues", yearRevenuesFunds);
            getYearlyData(yearExpenseFunds, expenseByDept.get(year), year, totalExpenses);
            yearFunds.put("expenses", yearExpenseFunds);
            aggregations.put(year, yearFunds);
        }
        HelperUtil.deleteFile(fileName);
        return new Scrub(rows.toArray(), aggregations);
    }

    public static void getYearlyData(Map<String, Object> yearRevenuesFunds, Map<String, List<Data>> fundByDept, String year, Double total) {
        Map<String, Double> deptExpense = new HashMap<>();
        Double expenseByYear = 0D;
        if(fundByDept == null) return;
        for(Map.Entry<String, List<Data>> entry: fundByDept.entrySet()) {
            deptExpense.put(entry.getKey(),entry.getValue().stream().mapToDouble(x -> x.getAmount()).sum());
            expenseByYear += entry.getValue().stream().mapToDouble(x -> x.getAmount()).sum();
        }
        yearRevenuesFunds.put("funds", new HashMap() {{
            put("General", total);
        }});
        yearRevenuesFunds.put("departments", deptExpense);
        yearRevenuesFunds.put("total", expenseByYear);
    }

}

