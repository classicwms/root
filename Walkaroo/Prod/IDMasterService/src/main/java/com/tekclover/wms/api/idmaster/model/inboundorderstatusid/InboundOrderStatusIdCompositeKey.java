package com.tekclover.wms.api.idmaster.model.inboundorderstatusid;

import lombok.Data;

import java.io.Serializable;

@Data
public class InboundOrderStatusIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `IB_ORD_STATUS_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String inboundOrderStatusId;
	private String languageId;
}
