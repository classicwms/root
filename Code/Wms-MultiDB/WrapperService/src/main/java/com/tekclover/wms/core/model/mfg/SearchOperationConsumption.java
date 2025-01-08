package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchOperationConsumption {

	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> languageId;
	private List<String> warehouseId;
	private List<String> productionOrderNo;
	private List<String> itemCode;
	private List<String> bomItem;
	private List<String> batchNumber;
	private List<String> issuedBy;
	private List<String> orderConfirmedBy;
	private List<Long> statusId;
	private List<Long> productionOrderLineNo;
	
	private Date startCreatedOn;
	private Date endCreatedOn;

	private Date startConfirmedOn;
	private Date endConfirmedOn;
}