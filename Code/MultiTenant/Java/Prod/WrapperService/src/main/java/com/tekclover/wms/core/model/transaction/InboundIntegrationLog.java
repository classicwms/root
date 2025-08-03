package com.tekclover.wms.core.model.transaction;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class InboundIntegrationLog { 
	
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String integrationLogNumber;
	private String refDocNumber;
	private String successFileName;
	private String errorFileName;
	private Date orderReceiptDate;
	private String integrationStatus;
	private String remarks;
	private Long deletionIndicator;
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
	private String createdBy;
	private Date createdOn;
}
