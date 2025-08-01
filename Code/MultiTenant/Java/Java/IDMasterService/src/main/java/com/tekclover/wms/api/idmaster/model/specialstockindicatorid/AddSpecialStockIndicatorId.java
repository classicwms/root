package com.tekclover.wms.api.idmaster.model.specialstockindicatorid;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Data
public class AddSpecialStockIndicatorId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;

	private String stockTypeId;
	@NotBlank(message = "Special Stock Indicator Id is mandatory")
	private String specialStockIndicatorId;
	
	private String languageId;
	private String specialStockIndicatorDescription;
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
