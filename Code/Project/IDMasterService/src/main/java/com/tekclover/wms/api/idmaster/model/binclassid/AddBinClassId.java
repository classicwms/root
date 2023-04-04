package com.tekclover.wms.api.idmaster.model.binclassid;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AddBinClassId {

	private String companyCodeId;
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotBlank(message = "bin Class Id is mandatory")
	private Long binClassId;
	
	private String languageId;
	private String binClass;
	private Long deletionIndicator;
	private String createdBy;
}
