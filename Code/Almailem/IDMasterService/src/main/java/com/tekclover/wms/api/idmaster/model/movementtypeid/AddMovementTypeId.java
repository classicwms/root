package com.tekclover.wms.api.idmaster.model.movementtypeid;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddMovementTypeId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotBlank(message = "Movement Type Id is mandatory")
	private String movementTypeId;
	
	private String languageId;
	private String movementTypeText;
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
