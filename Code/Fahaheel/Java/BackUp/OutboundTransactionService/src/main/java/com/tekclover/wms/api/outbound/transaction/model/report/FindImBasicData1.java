package com.tekclover.wms.api.outbound.transaction.model.report;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class FindImBasicData1 {
	
	private String warehouseId;
	private String companyCodeId;
	private String plantId;
	private String languageId;
	private List<String> itemCode;
	private String manufacturerName;
	private Date fromCreatedOn;
	private Date toCreatedOn;
}