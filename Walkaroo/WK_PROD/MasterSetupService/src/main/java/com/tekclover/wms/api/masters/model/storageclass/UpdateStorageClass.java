package com.tekclover.wms.api.masters.model.storageclass;

import lombok.Data;

@Data
public class UpdateStorageClass {


	private String hazardMaterialClass;
	private String waterPollutionClass;
	private String remarks;
	private String description;
	private String updatedBy;
}
