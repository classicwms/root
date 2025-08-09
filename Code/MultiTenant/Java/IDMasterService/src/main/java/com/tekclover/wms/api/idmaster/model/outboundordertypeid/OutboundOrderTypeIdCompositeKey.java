package com.tekclover.wms.api.idmaster.model.outboundordertypeid;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutboundOrderTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `OB_ORD_TYP_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String outboundOrderTypeId;
	private String languageId;
}
