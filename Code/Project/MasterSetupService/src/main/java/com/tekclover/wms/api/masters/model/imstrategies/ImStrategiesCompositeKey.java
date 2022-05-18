package com.tekclover.wms.api.masters.model.imstrategies;

import java.io.Serializable;

import lombok.Data;

@Data
public class ImStrategiesCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`, `STR_TYP_ID`, `SEQ_IND`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String strategeyTypeId;
	private Long sequenceIndicator;
}
