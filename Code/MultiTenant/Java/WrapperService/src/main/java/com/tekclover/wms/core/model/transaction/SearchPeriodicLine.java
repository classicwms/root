package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPeriodicLine {

	private List<String> companyCode;
	private List<String> languageId;
	private String plantId;
	private String warehouseId;
	private String cycleCountNo;
	private List<Long> lineStatusId;
	private List<String> cycleCounterId;
	private Date startCreatedOn;
	private Date endCreatedOn;

	private List<String> itemCode;
	private List<String> packBarcodes;
	private List<String> storageBin;
	private List<Long> stockTypeId;
	private List<String> referenceField9;			//MFR Code
	private List<String> referenceField10;			//Section
}
