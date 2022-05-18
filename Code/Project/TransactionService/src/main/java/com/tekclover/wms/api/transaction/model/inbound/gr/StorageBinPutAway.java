package com.tekclover.wms.api.transaction.model.inbound.gr;

import java.util.List;

import lombok.Data;

@Data
public class StorageBinPutAway {

	private List<String> storageBin;
	private List<String> storageSectionIds;
}
