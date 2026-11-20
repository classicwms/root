package com.tekclover.wms.api.idmaster.model.palletizationlevelid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddPalletizationLevelId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotNull(message = "Palletization Level Id is mandatory")
	private String palletizationLevelId;
	
	@NotBlank(message = "Palletization Level is mandatory")
	private String palletizationLevel;
	
	private String languageId;
	private String palletizationLevelReference;
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
