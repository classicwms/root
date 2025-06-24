package com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound;

import java.io.Serializable;

import lombok.Data;

@Data
public class PreOutboundLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `REF_DOC_NO`, `PRE_OB_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `ITM_CODE`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String refDocNumber;
	private String preOutboundNo;
	private String partnerCode;
	private Long lineNumber;
	private String itemCode;
//	private String barcodeId;
}