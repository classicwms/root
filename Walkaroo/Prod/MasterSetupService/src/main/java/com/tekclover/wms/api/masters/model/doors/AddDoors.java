package com.tekclover.wms.api.masters.model.doors;

import lombok.Data;

@Data
public class AddDoors {

	private String doorId;
	
    private String companyId;
	
	private String plantId;
    
    private String warehouseId;
	
	private String languageId;
	
	private String doorType;
	
	private String createdBy;
}
