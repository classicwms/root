package com.tekclover.wms.api.enterprise.model.storageclass;

import lombok.Data;

@Data
public class AddStorageClass {

	private Long storageClassId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private String languageId;
	private String hazardMaterialClass;
	private String waterPollutionClass;
	private String remarks;
	private String createdBy;
}
