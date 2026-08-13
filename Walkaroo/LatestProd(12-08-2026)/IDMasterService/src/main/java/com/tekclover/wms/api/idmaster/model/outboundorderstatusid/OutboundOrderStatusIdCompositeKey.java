package com.tekclover.wms.api.idmaster.model.outboundorderstatusid;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutboundOrderStatusIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `OB_ORD_STATUS_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String outboundOrderStatusId;
	private String languageId;
}
