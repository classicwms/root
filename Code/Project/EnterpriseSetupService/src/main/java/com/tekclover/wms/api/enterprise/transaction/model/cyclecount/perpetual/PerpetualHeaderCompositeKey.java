package com.tekclover.wms.api.enterprise.transaction.model.cyclecount.perpetual;

import lombok.Data;

import java.io.Serializable;

@Data
public class PerpetualHeaderCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `CC_TYP_ID`, `CC_NO`, `MVT_TYP_ID`, `SUB_MVT_TYP_ID`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long cycleCountTypeId;
	private String cycleCountNo;
	private Long movementTypeId;
	private Long subMovementTypeId;
}