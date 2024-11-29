package com.tekclover.wms.api.enterprise.transaction.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Warehouse { 
	
	private String companyCode;
	private String warehouseId;
	private String languageId;
	private String plantId;
	private String warehouseDesc;
	private Long deletionIndicator = 0L;
	private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
	private Date updatedOn = new Date();
}