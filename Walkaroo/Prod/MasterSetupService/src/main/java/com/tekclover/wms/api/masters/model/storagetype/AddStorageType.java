package com.tekclover.wms.api.masters.model.storagetype;

import lombok.Data;

@Data
public class AddStorageType {

	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long storageClassId;
	private Long storageTypeId;
	private String description;
	private String storageTemperatureFrom;
	private String storageTemperatureTo;
	private String storageUom;
	private Short capacityCheck;
	private Short capacityByWeight;
	private Short capacityByQty;
	private Short mixToStock;
	private Short addToExistingStock;
	private Short returnToSameStorageType;
	private Long deletionIndicator;
}
