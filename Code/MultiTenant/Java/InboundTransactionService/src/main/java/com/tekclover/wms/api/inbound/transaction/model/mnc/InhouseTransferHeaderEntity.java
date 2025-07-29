package com.tekclover.wms.api.inbound.transaction.model.mnc;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InhouseTransferHeaderEntity {

	private String languageId;
	
	private String companyCodeId;
	
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotBlank(message = "transfer Number is mandatory")
	private String transferNumber;
	
	@NotNull(message = "transfer Type Id is mandatory")
	private Long transferTypeId;	
	
	private String transferMethod;	
	
	private Long statusId;
	
	private String remarks;	
	
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
	
	private Long deletionIndicator;
	
	private String createdBy;

    private Date createdOn = new Date();

    private String updatedBy;

	private Date updatedOn = new Date();

	//Almailem Code
	private String manufacturerName;
	
	private List<InhouseTransferLineEntity> inhouseTransferLine;
}
