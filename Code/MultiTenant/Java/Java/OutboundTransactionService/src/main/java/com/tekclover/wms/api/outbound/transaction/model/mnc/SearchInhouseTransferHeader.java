package com.tekclover.wms.api.outbound.transaction.model.mnc;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchInhouseTransferHeader {
	/*
	 * WH_ID
	 * TR_NO
	 * TR_TYP_ID
	 * STATUS_ID
	 * IT_CTD_BY
	 * IT_CTD_ON
	 */
	private List<String> warehouseId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> transferNumber;
	private List<Long> transferTypeId;	
	private List<Long> statusId;
	private List<String> createdBy;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
}
