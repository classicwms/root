package com.tekclover.wms.api.enterprise.model.batchserial;

import lombok.Data;
import java.util.Set;

@Data
public class AddBatchSerial {

	private String storageMethod;
	private Long id;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long levelId;
	private String maintenance;
	private String levelIdAndDescription;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String languageId;
	private String levelReferences;
	private String description;
	private Long deletionIndicator;
}
