package com.tekclover.wms.api.enterprise.model.storageclass;

import lombok.Data;

@Data
public class UpdateStorageClass {

	private Long storageClassId;
    private String companyId;
	private String plantId;
    private String warehouseID;
	private String languageId;
	private String hazardMaterialClass;
	private String waterPollutionClass;
	private String remarks;
	private String updatedBy;
}
