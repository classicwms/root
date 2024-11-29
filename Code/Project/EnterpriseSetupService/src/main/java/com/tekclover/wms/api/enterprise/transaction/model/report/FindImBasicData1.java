package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindImBasicData1 {
	
	private String warehouseId;
	private List<String> itemCode;
	private Date fromCreatedOn;
	private Date toCreatedOn;
}