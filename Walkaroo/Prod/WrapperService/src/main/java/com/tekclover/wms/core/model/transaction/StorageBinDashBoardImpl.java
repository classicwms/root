package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class StorageBinDashBoardImpl {

	Double quantity;
	String storageBin;
	String statusId;
	String statusDescription;
}