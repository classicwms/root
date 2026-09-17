package com.tekclover.wms.api.inbound.transaction.model.report;

import lombok.Data;

import java.util.List;

@Data
public class StorageBinDashBoardInput {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private List<String> storageBin;
	private Long binClassId;
}