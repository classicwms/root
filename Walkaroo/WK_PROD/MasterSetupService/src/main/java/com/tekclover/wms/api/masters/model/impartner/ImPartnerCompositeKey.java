package com.tekclover.wms.api.masters.model.impartner;

import java.io.Serializable;

import lombok.Data;

@Data
public class ImPartnerCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`, `PARTNER_TYP`, `PARTNER_CODE`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String businessPartnerType;
	private String businessPartnerCode;
	private String partnerItemBarcode;
	private String manufacturerName;
}
