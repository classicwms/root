package com.tekclover.wms.api.outbound.transaction.model.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ImBasicData1CompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;

	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`, `UOM_ID`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String uomId;
	private String manufacturerPartNo;
}