package com.tekclover.wms.core.model.idmaster;

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
	private String createdBy;
    private Date createdOn;
    private String updatedBy;
	private Date updatedOn;
}
