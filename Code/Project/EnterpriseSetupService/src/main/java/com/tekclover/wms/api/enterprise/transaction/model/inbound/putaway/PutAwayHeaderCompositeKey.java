package com.tekclover.wms.api.enterprise.transaction.model.inbound.putaway;

import lombok.Data;

import java.io.Serializable;

@Data
public class PutAwayHeaderCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_IB_NO`, `REF_DOC_NO`, `GR_NO`, `PAL_CODE`, `CASE_CODE`, `PACK_BARCODE`, `PA_NO`, `PROP_ST_BIN`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preInboundNo;
	private String refDocNumber;
	private String goodsReceiptNo;
	private String palletCode;
	private String caseCode;
	private String packBarcodes;
	private String putAwayNumber;
	private String proposedStorageBin;
}