package com.tekclover.wms.api.inbound.orders.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StrategiesCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `STR_TYP_ID`, `SEQ_IND`, `ST_NO`
	 */
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long strategyTypeId;
	private Long sequenceIndicator;
}
