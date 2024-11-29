package com.tekclover.wms.api.enterprise.transaction.model.mnc;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchInhouseTransferLine {
	/*
	 * WH_ID
	 * TR_NO
	 * SRCE_ITM_CODE
	 * SRCE_STCK_TYP_ID
	 * SRCE_ST_BIN
	 * TGT_ITM_CODE
	 * TGT_STCK_TYP_ID
	 * TGT_ST_BIN
	 * TR_CNF_QTY
	 * PACK_BARCODE
	 * AVL_QTY
	 * STATUS_ID
	 * REMARK
	 * IT_CTD_BY
	 * IT_CTD_ON
	 * IT_CNF_BY
	 * IT_CNF_ON
	 */
	 
	private List<String> warehouseId;
	private List<String> transferNumber;
	private List<String> sourceItemCode;
	private List<Long> sourceStockTypeId;
	private List<String> sourceStorageBin;
	private List<String> targetItemCode;
	private List<Long> stockTypeId;
	private List<String> targetStorageBin;
	private List<Double> transferConfirmedQty;
	private List<String> packBarcodes;
	private List<Double> availableQty;
	private List<Long> statusId;
	private List<String> remarks;
	private List<String> createdBy;
	private List<String> confirmedBy;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
	private Date startConfirmedOn;
	private Date endConfirmedOn;
}