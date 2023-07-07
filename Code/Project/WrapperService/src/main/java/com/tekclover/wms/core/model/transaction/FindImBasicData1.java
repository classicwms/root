package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class FindImBasicData1 {
	
	private String warehouseId;
	private List<String> itemCode;
	private Date fromCreatedOn;
	private Date toCreatedOn;
}