package com.tekclover.wms.api.outbound.transaction.model.outbound;

import lombok.Data;

import java.util.List;

@Data
public class StorageBinPutAway {

	private List<String> storageBin;
	private List<String> storageSectionIds;
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
