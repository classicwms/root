package com.tekclover.wms.api.inbound.transaction.model.outbound.v2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchPickListLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> manufacturerName;
	private List<String> targetBranchCode;
	private List<String> salesOrderNumber;

	private List<String> warehouseId;
	private List<String> partnerCode;
	private List<Long> lineNumber;
	private List<String> itemCode;

	// For Reports
	private Date fromDeliveryDate;	// DLV_CNF_ON
	private Date toDeliveryDate;	// DLV_CNF_ON
	private List<String> orderType; // REF_FIELD_1
	private List<Long> statusId;	// STATUS_ID
}