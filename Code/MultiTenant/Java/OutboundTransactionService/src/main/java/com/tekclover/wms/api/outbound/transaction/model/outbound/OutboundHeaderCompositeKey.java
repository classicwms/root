package com.tekclover.wms.api.outbound.transaction.model.outbound;

import java.io.Serializable;

import lombok.Data;

@Data
public class OutboundHeaderCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
}
