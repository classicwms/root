package com.tekclover.wms.api.enterprise.transaction.model.inbound.gr;

import lombok.Data;

import java.io.Serializable;

@Data
public class GrHeaderCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_IB_NO`, `REF_DOC_NO`, `STG_NO`, `GR_NO`, `PAL_CODE`, `CASE_CODE`
	 */
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String preInboundNo;
	private String refDocNumber;
	private String stagingNo;
	private String goodsReceiptNo;
	private String palletCode;
	private String caseCode;
}