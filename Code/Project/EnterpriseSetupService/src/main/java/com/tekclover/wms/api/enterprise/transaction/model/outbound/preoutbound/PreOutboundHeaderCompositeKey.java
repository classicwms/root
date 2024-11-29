package com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound;

import lombok.Data;

import java.io.Serializable;

@Data
public class PreOutboundHeaderCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `REF_DOC_NO`, `PRE_OB_NO`, `PARTNER_CODE`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String refDocNumber;
	private String preOutboundNo;
	private String partnerCode;

}