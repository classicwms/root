package com.tekclover.wms.api.enterprise.model.doors;

import lombok.Data;

@Data
public class UpdateDoors {

	private String doorId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private String languageId;
	private String doorType;
	private String updatedBy;
}
