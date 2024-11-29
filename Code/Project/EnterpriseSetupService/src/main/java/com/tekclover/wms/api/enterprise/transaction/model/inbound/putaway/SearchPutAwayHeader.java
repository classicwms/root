package com.tekclover.wms.api.enterprise.transaction.model.inbound.putaway;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPutAwayHeader {
	/*
	 * WH_ID
	 * REF_DOC_NO
	 * PACK_BARCODE
	 * PA_NO
	 * PROP_ST_BIN
	 * PROP_HE_NO
	 * STATUS_ID
	 * PA_CTD_BY
	 * PA_CTD_ON
	 */
	private List<String> warehouseId;
	private List<String> refDocNumber;
	private List<String> packBarcodes;
	private List<String> putAwayNumber;
	private List<String> proposedStorageBin;
	private List<String> proposedHandlingEquipment;
	private List<Long> statusId;
	private List<String> createdBy;

	private Date startCreatedOn;
	private Date endCreatedOn;	
}