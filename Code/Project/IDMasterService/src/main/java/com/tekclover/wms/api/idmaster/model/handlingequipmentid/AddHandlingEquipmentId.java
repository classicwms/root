package com.tekclover.wms.api.idmaster.model.handlingequipmentid;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddHandlingEquipmentId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;

	private String handlingEquipmentId;
	private String handlingEquipmentNumber;
	
	private String languageId;
	private String handlingEquipmentDescription;
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
