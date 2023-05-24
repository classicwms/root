package com.tekclover.wms.core.model.enterprise;

import lombok.Data;

import java.util.List;

@Data
public class UpdateBatchSerial {

	private String storageMethod;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private Long levelId;
	private String maintenance;
	private String languageId;
	private List<LevelReference> levelReferences;
	private String description;
	private Long deletionIndicator;
}
