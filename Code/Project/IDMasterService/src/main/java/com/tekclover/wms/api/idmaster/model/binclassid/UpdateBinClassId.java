package com.tekclover.wms.api.idmaster.model.binclassid;

import lombok.Data;

@Data
public class UpdateBinClassId {

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long binClassId;
	private String languageId;
	private String binClass;
	private Long deletionIndicator;
	private String updatedBy;
}
