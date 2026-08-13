package com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic;

import java.io.Serializable;

import lombok.Data;

@Data
public class PeriodicHeaderCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `CC_TYP_ID`, `CC_NO`
	 */
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private Long cycleCountTypeId;
	private String cycleCountNo;
}
