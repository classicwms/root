package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddShelfId {

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String floorId;
	private String storageSectionId;
	private String aisleId;
	private String rowId;
	private String spanId;
	private String shelfId;
	private String languageId;
	private String shelfDescription;
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
