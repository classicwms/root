package com.tekclover.wms.core.model.masters;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class AuditLog { 
	
	private String languageId;
	
	private String companyCodeId;
	
	private String plantId;
	
	private String warehouseId;
	
	private String auditFileNumber;
	private Long auditLogNumber;
	
	private Long financialYear;
	
	private String objectName;
	
	private String screennumber;
	
	private String subScreenNumber;
	
	private String modifiedTableName;
	
	private String modifiedField;
	
	private String oldValue;
	
	private String newValue;
	
	private Long statusId;
	
	private String referenceField1;
	
	private String referenceField2;
	
	private String referenceField3;
	
	private String referenceField4;
	
	private String referenceField5;
	
	private String referenceField6;
	
	private String referenceField7;
	
	private String referenceField8;
	
	private String referenceField9;
	
	private String referenceField10;
	
	private String updatedby;
	
	private Date updatedon;

}
