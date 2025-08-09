package com.tekclover.wms.api.inbound.orders.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Warehouse { 
	
	private String companyCode;
	private String companyCodeId;
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
