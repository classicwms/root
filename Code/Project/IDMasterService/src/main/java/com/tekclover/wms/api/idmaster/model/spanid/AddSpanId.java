package com.tekclover.wms.api.idmaster.model.spanid;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddSpanId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotBlank(message = "Floor Id is mandatory")
	private String floorId;
	
	private String storageSectionId;
	private String aisleId;
	private String rowId;
	private String spanId;
	private String languageId;
	private String spanDescription;
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
