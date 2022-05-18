package com.tekclover.wms.api.enterprise.model.batchserial;

import lombok.Data;

@Data
public class AddBatchSerial {
	
	private String storageMethod;
	
    private String companyId;
	
	private String plantId;
    
    private String warehouseId;

	private Long levelId;
	
	private String maintenance;
	
	private String languageId;
	
	private String levelReference;
	
	private String createdBy;
}
