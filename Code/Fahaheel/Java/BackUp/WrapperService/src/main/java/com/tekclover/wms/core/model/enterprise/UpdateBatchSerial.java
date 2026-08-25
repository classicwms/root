package com.tekclover.wms.core.model.enterprise;

import lombok.Data;

@Data
public class UpdateBatchSerial {

	private String storageMethod;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private Long levelId;
	private Long id;
	private String maintenance;
	private String languageId;
	private String levelReferences;
	private String description;
	private Long deletionIndicator;
}
