package com.tekclover.wms.api.idmaster.model.adhocmoduleid;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddAdhocModuleId {

	private String companyCodeId;
	private String plantId;
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	@NotNull(message = "Module Id is mandatory")
	private String moduleId;
	@NotNull(message = "Adhoc Module Id is mandatory")
	private String adhocModuleId;
	private String languageId;
	private String adhocModuleDescription;
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
