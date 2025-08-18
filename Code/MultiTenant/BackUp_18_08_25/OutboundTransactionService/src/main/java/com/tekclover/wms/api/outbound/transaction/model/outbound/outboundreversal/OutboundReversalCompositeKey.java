package com.tekclover.wms.api.outbound.transaction.model.outbound.outboundreversal;

import java.io.Serializable;

import lombok.Data;

@Data
public class OutboundReversalCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `REF_DOC_NO`, `PLANT_ID`, `WH_ID`, `OB_REVERSAL_NO`, `REVERSAL_TYPE`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String outboundReversalNo;
	private String reversalType;
	private String barcodeId;
}