package com.tekclover.wms.api.enterprise.model.storagetype;

import lombok.Data;

@Data
public class UpdateStorageType {

	private Long storageTypeId;
    private String companyId;
	private String plantId;
    private String warehouseID;
	private Long storageClassId;
	private String languageId;
	private String description;
	private String storageTemperatureFrom;
	private String storageTemperatureTo ;
	private String storageUom;
	private Short capacityCheck;
	private Short mixToStock;
	private Short addToExistingStock;	
	private Short allowNegativeStock;
	private Short returnToSameStorageType;
	private String updatedBy;
}
