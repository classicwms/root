package com.tekclover.wms.api.enterprise.transaction.model.cyclecount.periodic;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPeriodicLine {
	
	private String plantId;
	private String warehouseId;
	private String cycleCountNo;
	private List<Long> lineStatusId;
	private List<String> cycleCounterId;
	private Date startCreatedOn;
	private Date endCreatedOn;

	private List<String> itemCode;
	private List<String> storageBin;
	private List<String> packBarcodes;
	private List<Long> stockTypeId;
	private List<String> referenceField9;			//MFR Code
	private List<String> referenceField10;			//Section
}