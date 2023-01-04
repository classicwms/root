package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.Date;

@Data
public class KeyValue {

	private String documentNumber;
	private String total;
	private String notes;
	private String status;
	private String documentType;
	private Date documentDate;

}
