package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class StorageType { 
	
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
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
	private Short capacityByWeight;
	private Short capacityByQty;
}
