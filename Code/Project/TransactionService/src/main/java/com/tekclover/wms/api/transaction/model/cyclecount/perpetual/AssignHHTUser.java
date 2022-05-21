package com.tekclover.wms.api.transaction.model.cyclecount.perpetual;

import lombok.Data;

@Data
public class AssignHHTUser {

	private String cycleCounterId;
	private String cycleCounterName;
	
	// UK Fields
	private String warehouseId;
	private String cycleCountNo;
	private String storageBin;
	private String itemCode;
	private String packBarcodes;
}
