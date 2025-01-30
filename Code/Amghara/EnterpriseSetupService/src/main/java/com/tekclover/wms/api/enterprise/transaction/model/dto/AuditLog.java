package com.tekclover.wms.api.enterprise.transaction.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AuditLog { 
	private String companyCode;
	private String plantID;
	private String warehouseId;
	private String auditLogNumber;
	private Integer fiscalYear;
	private String objectName;
	private Integer screenNo;
	private Integer subScreenNo;
	private String tableName;
	private String modifiedField;
	private String oldValue;
	private String newValue;
	private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
	private Date updatedOn = new Date();
}