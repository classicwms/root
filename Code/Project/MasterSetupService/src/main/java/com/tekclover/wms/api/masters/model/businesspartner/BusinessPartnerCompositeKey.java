package com.tekclover.wms.api.masters.model.businesspartner;

import java.io.Serializable;

import lombok.Data;

@Data
public class BusinessPartnerCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PARTNER_TYP`, `PARTNER_CODE`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long businessPartnerType;
	private String partnerCode;
}
