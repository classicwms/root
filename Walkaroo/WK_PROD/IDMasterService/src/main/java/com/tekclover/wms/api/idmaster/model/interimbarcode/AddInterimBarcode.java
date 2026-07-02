package com.tekclover.wms.api.idmaster.model.interimbarcode;

import java.util.Date;

import lombok.Data;

@Data
public class AddInterimBarcode { 
	
	/*
	 * Storage Bin(String) , Item code(String) and Barcode(string) 
	 */

	private String storageBin;
	private String itemCode;
	private String barcode;
	private Long deletionIndicator = 0L;
	private String referenceField1; 								// Mfr code
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
