package com.tekclover.wms.api.enterprise.model.storagetype;

import lombok.Data;

@Data
public class AddStorageType {

	private Long storageTypeId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private Long storageClassId;
	private String languageId;
	private String storageTemperatureFrom;
	private String storageTemperatureTo;
	private String storageUom;
	private Short capacityCheck;
	private Short mixToStock;
	private Short addToExistingStock;	
	private Short allowNegativeStock;
	private Short returnToSameStorageType;
	private String createdBy;
}
