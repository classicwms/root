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
public class StorageBinType { 
	
	private Long storageBinTypeId;
	private String companyId;
	private String plantId;
	private Long storageClassId;
	private String warehouseId;
	private Long storageTypeId;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String storageClassIdAndDescription;
	private String storageTypeIdAndDescription;
	private String languageId;
	private String description;
	private Double length;
	private Double width;
	private Double height;
	private Boolean storageBinTypeBlock;
	private String dimensionUom;
	private Double totalVolume;
	private String volumeUom;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
