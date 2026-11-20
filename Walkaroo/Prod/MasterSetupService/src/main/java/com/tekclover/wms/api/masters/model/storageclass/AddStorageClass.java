package com.tekclover.wms.api.masters.model.storageclass;

import lombok.Data;

@Data
public class AddStorageClass {

	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long storageClassId;
	private String hazardMaterialClass;
	private String waterPollutionClass;
	private String remarks;
	private String description;
	private Long deletionIndicator;

}
