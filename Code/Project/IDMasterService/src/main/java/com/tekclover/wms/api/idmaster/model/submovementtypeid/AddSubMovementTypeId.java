package com.tekclover.wms.api.idmaster.model.submovementtypeid;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddSubMovementTypeId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	private String movementTypeId;
	@NotNull(message = "Sub Movement Type Id is mandatory")
	private String subMovementTypeId;
	private String languageId;
	private String subMovementType;
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
