package com.tekclover.wms.api.idmaster.model.inboundordertypeid;

import lombok.Data;

import java.io.Serializable;

@Data
public class InboundOrderTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `IB_ORD_TYP_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String inboundOrderTypeId;
	private String languageId;
}
