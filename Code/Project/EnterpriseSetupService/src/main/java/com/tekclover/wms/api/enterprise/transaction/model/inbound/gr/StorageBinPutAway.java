package com.tekclover.wms.api.enterprise.transaction.model.inbound.gr;

import lombok.Data;

import java.util.List;

@Data
public class StorageBinPutAway {

	private List<String> storageBin;
	private List<String> storageSectionIds;
	private String warehouseId;
}