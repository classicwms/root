package com.tekclover.wms.api.masters.model.storagebin;

import java.util.List;

import lombok.Data;

@Data
public class StorageBinPutAway {

	private List<String> storageBin;
	private List<String> storageSectionIds;
	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;

	//Almailem
	private String bin;
	private Long binClassId;
	private Long statusId;
	private boolean capacityCheck;
	private Double cbm;
	private Double cbmPerQty;
}
