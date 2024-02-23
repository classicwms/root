package com.tekclover.wms.core.model.masters;

import lombok.Data;

@Data
public class StorageBinV2 extends StorageBin {

	private boolean capacityCheck;
	private Double allocatedVolume;
	private String capacityUnit;
	private Double length;
	private Double width;
	private Double height;
	private String capacityUom;
	private String quantity;
	private Double weight;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;

}
