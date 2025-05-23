package com.tekclover.wms.api.idmaster.model.approvalid;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class AddApprovalId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	@NotBlank(message = "Approval Id is mandatory")
	private String approvalId;
	private String languageId;
	private String approvalProcessId;
	@NotBlank(message = "Approval Level is mandatory")
	private String approvalLevel;
	private String approverCode;
	private String approvalProcess;
	private String approverName;
	private String designation;
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
}
