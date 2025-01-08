package com.tekclover.wms.core.model.enterprise;

import java.util.Date;
import lombok.Data;
@Data
public class StorageType {

	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long storageClassId;
	private Long storageTypeId;
	private String description;
	private String storageTemperatureFrom;
	private String storageTemperatureTo;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String storageClassIdAndDescription;
	private String storageUom;
	private Short capacityCheck;
	private Short capacityByWeight;
	private Short capacityByQty;
	private Short mixToStock;
	private Short addToExistingStock;
	private Short returnToSameStorageType;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
