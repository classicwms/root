package com.tekclover.wms.api.enterprise.model.storagebintype;

import lombok.Data;

@Data
public class UpdateStorageBinType {

	private Long storageBinTypeId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private Long storageTypeId;
	private Long storageClassId;
	private String languageId;
	private String description;
	private Double length;
	private Double width;
	private Double height;
	private Double totalVolume;
	private String volumeUom;
	private Boolean storageBinTypeBlock;
	private String dimensionUom;
	private String updatedBy;
}
