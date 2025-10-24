package com.mnrclara.spark.core.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.mnrclara.spark.core.model.MultiTenant.InvoiceHeader;
import com.mnrclara.spark.core.model.MultiTenant.SearchInvoiceHeader;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SparkTest {

	public List<Client> dbProcess () throws Exception {
		// Create SparkSession in spark 2.x or later
		val spark = SparkSession.builder().master("local[*]")
		    .appName("SparkByExamples.com")
		    .getOrCreate();

		// Connection Properties
		val connProp = new Properties();
		connProp.setProperty("driver", "com.mysql.cj.jdbc.Driver");
		connProp.put("user", "root");
		connProp.put("password", "9SOwjgFjm0sM7qMOFz16mICJUx");

		// Read from MySQL Table
//		val df = spark.read().jdbc("jdbc:mysql://10.10.22.24:3306/MNRCLARA", "tblmattergenaccid", connProp);
//		val df1 = spark.read().jdbc("jdbc:mysql://10.10.22.24:3306/MNRCLARA", "tblclientgeneralid", connProp);
		val df2 = spark.read().jdbc("jdbc:mysql://10.10.22.24:3306/MNRCLARA", "tblinvoiceheader", connProp);
		df2.createOrReplaceTempView("tblinvoiceheader");
		
//		df1.createOrReplaceTempView("tblclientgeneralid");
//		Dataset<Row> queryDF = spark.sql("SELECT CLIENT_ID, FIRST_LAST_NM FROM tblclientgeneralid WHERE CLIENT_ID > 500009");
//		queryDF.show();
		
		Dataset<Row> queryDF = spark.sql("SELECT client_id, invoice_no, ctd_on FROM tblinvoiceheader");
//		Dataset<Row> df = spark.sql("SELECT client_id, invoice_no, ctd_on FROM tblinvoiceheader");
//		Dataset<Row> df = spark.sql("SELECT client_id, invoice_no, ctd_on FROM tblinvoiceheader WHERE to_date(ctd_on) >= '2022-01-31' AND to_date(ctd_on) <= '2022-05-31'");
//		df.show();
		//		df.filter(df.col("ctd_on").gt("2022-01-30")).show();
//		df.filter(df.col("invoice_no").gt(15223)).show();
		
		String fromDate = "2021-12-01 00:00:00";
		String toDate = "2022-01-31 23:59:59";
		Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromDate);
		Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toDate);
		log.info("convertStringToDate-------> : " + date1);
			
		Encoder<Client> fakePeopleEncoder = Encoders.bean(Client.class);
		Dataset<Client> selRows1 = queryDF
		.filter(a->a.getTimestamp(2).after(date1) && a.getTimestamp(2).before(date2)).as(fakePeopleEncoder);
//		.show();
		selRows1.show();
		
		//-------------------------------------------------
//		Dataset<Row> df = spark.sql("SELECT client_id, invoice_no, ctd_on FROM tblinvoiceheader WHERE ctd_on >= '2022-01-31' ");
//		df.show();
		
//		var jdf = df.as("matter").join(df1.as("client"), df.col("CLIENT_ID").equalTo(df1.col("CLIENT_ID")));
//		Encoder<Client> fakePeopleEncoder = Encoders.bean(Client.class);
//		Dataset<Client> selRows1 = jdf.select("matter.CLIENT_ID", "client.FIRST_LAST_NM").as(fakePeopleEncoder);
//		return selRows1.collectAsList();
		
		// show DataFrame
//		df.show(120);
//		df.select("exp_code_text","ctd_by").show();
//		df.groupBy("cost_typ").count().show();
		return null;
	}
	
	public List<InvoiceHeader> findInvoiceHeaders (SearchInvoiceHeader searchInvoiceHeader) throws Exception {
		 log.info("-----> : " + searchInvoiceHeader);
		val spark = SparkSession.builder().master("local[*]")
			    .appName("SparkByExamples.com")
			    .getOrCreate();

		// Connection Properties
		val connProp = new Properties();
		connProp.setProperty("driver", "com.mysql.cj.jdbc.Driver");
		connProp.put("user", "root");
		connProp.put("password", "9SOwjgFjm0sM7qMOFz16mICJUx");
	
		// Read from MySQL Table
		val df2 = spark.read().jdbc("jdbc:mysql://10.10.22.24:3306/MNRCLARA", "tblinvoiceheader", connProp);
		df2.createOrReplaceTempView("tblinvoiceheader");
			
		Dataset<Row> queryDF = spark.sql("SELECT LANG_ID as languageId, CLASS_ID as classId, \r\n"
				+ "	MATTER_NO as matterNumber, CLIENT_ID as clientId, \r\n"
				+ "	CASE_CATEGORY_ID as caseCategoryId, CASE_SUB_CATEGORY_ID as caseSubCategoryId,\r\n"
				+ "	INVOICE_NO as invoiceNumber, INVOICE_FISCAL_YEAR as invoiceFiscalYear,\r\n"
				+ "	INVOICE_PERIOD as invoicePeriod, PRE_BILL_BATCH_NO as preBillBatchNumber,\r\n"
				+ "	PRE_BILL_NO as preBillNumber, POSTING_DATE as postingDate	\r\n"
				+ "	FROM tblinvoiceheader");
		
//		String fromDate = "2021-12-01 00:00:00";
//		String toDate = "2022-01-31 23:59:59";
		
		String fromDate = searchInvoiceHeader.getStartInvoiceDate() + " 00:00:00";
		String toDate = searchInvoiceHeader.getEndInvoiceDate() + " 23:59:59";
		Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromDate);
		Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toDate);
		log.info("convertStringToDate-------> : " + date1);
			
//		 queryDF
//		.filter(a->a.getTimestamp(2).after(date1) && a.getTimestamp(2).before(date2))
//		.show();
//		 Date[] dateConv = DateUtils.addTimeToDates (searchInvoiceHeader.getStartInvoiceDate(), searchInvoiceHeader.getEndInvoiceDate());
//		 log.info("dateConv[0]-------> : " + dateConv[0]);
//		 log.info("dateConv[1]-------> : " + dateConv[1]);
		 
		 Encoder<InvoiceHeader> invoiceHeaderEncoder = Encoders.bean(InvoiceHeader.class);
		 Dataset<InvoiceHeader> dataSetInvoiceHeaderRows = queryDF
									.filter(a->a.getTimestamp(11).after(date1) && 
											a.getTimestamp(11).before(date2)).as(invoiceHeaderEncoder);
		 dataSetInvoiceHeaderRows.show();
		 List<InvoiceHeader> list = new ArrayList<>();
		 dataSetInvoiceHeaderRows.foreach(a -> {
			 list.add(a);
		 });
		 
		 return list;
	}
	
	public static void main(String[] args) throws Exception {
		log.info("Start--##########################----: " + new Date());
		SparkTest stest = new SparkTest();
		try {
			List<Client> clientData = stest.dbProcess();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fromDate = "2021-12-01 00:00:00";
		String toDate = "2022-01-31 23:59:59";
		Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromDate);
		Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toDate);
		SearchInvoiceHeader searchInvoiceHeader = new SearchInvoiceHeader();
		searchInvoiceHeader.setStartInvoiceDate(date1);
		searchInvoiceHeader.setEndInvoiceDate(date2);
//		List<InvoiceHeader> list = stest.findInvoiceHeaders (searchInvoiceHeader);
//		log.info("-##########################----: " + list);
	}
}
