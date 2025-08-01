package com.mnrclara.spark.core.util;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Data;

@Data
public class Client {
	// CLIENT_ID", "client.FIRST_LAST_NM
	private String CLIENT_ID;
	private String invoice_no;
	private Timestamp CTD_ON;
	
}
