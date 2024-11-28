package com.tekclover.wms.api.enterprise.model.storagebintype;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;


@Data
public class AddStorageBinType {

	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long storageTypeId;
	private Long storageBinTypeId;
	private Long storageClassId;
	private String description;
	private Double length;
	private Double width;
	private Double height;
	private String dimensionUom;
	private Double totalVolume;
	private String volumeUom;
	private Boolean storageBinTypeBlock;
	private Long deletionIndicator;
}
