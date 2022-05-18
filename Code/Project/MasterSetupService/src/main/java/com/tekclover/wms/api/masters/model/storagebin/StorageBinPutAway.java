package com.tekclover.wms.api.masters.model.storagebin;

import java.util.List;

import lombok.Data;

@Data
public class StorageBinPutAway {

	private List<String> storageBin;
	private List<String> storageSectionIds;
}
