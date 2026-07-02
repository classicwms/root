package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchDeliveryConfirmation {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> warehouseId;
	private List<String> loginUserId;
	private List<String> huSerialNo;
	private List<String> outbound;
	private List<String> skuCode;
	private List<String> gender;
	private List<String> articleNumber;
	private List<Long> deliveryId;
	private List<Long> processedStatusId;

	private Date fromDate;
	private Date toDate;
}