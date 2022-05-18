package com.tekclover.wms.api.enterprise.model.floor;

import lombok.Data;

@Data
public class UpdateFloor {

	private Long floorId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private String languageId;
	private String updatedBy;
}
