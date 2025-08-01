package com.tekclover.wms.api.outbound.transaction.model.inbound.inboundquality;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchInboundQualityHeader {

	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> languageId;
	private List<String> warehouseId;
	private List<String> refDocNumber;
	private List<String> preInboundNo;
	private List<String> inboundQualityNumber;
	private List<String> itemCode;
	private List<String> batchSerialNumber;
	private List<String> storageSectionId;
	private List<Long> statusId;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
}