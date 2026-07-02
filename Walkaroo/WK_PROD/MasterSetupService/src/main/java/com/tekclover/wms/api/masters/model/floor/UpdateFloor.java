package com.tekclover.wms.api.masters.model.floor;

import lombok.Data;

@Data
public class UpdateFloor {

	private Long floorId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private String languageId;
	private String description;
	private String updatedBy;
}
