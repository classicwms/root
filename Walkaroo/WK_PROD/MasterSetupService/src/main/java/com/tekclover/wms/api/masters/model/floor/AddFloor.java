package com.tekclover.wms.api.masters.model.floor;

import lombok.Data;

@Data
public class AddFloor {

	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
	private String description;
	private Long deletionIndicator;
}
