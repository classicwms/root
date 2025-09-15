package com.tekclover.wms.api.transaction.model.inbound.gr;

import java.util.List;

import lombok.Data;

@Data
public class StorageBinPutAway {

	private List<String> storageBin;
	private List<String> storageSectionIds;
	private String storageSectionId;
	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String bin;

	//Almailem
	private Long binClassId;
	private Long statusId;
	private boolean capacityCheck;
	private Double cbm;
	private Double cbmPerQty;
}
