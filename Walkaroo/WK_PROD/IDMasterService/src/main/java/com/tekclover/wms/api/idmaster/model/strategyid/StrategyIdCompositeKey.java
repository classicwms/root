package com.tekclover.wms.api.idmaster.model.strategyid;

import java.io.Serializable;

import lombok.Data;

@Data
public class StrategyIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `STR_TYP_ID`, `ST_NO`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long strategyTypeId;
	private String strategyNo;
	private String languageId;
}
