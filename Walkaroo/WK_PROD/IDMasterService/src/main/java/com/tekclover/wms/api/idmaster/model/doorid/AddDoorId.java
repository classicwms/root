package com.tekclover.wms.api.idmaster.model.doorid;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AddDoorId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotBlank(message = "Door Id is mandatory")
	private String doorId;
	
	private String languageId;
	private String doorDescription;
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
