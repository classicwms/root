package com.tekclover.wms.api.masters.model.batchserial;

import lombok.Data;

@Data
public class UpdateBatchSerial {

	private String storageMethod;
	private Long id;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private Long levelId;
	private String maintenance;
	private String languageId;
	private String levelReferences;
	private String description;
	private Long deletionIndicator;
}
