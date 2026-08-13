package com.tekclover.wms.api.idmaster.model.transfertypeid;

import lombok.Data;

import java.io.Serializable;

@Data
public class TransferTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `TR_TYP_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String transferTypeId;
	private String languageId;
}
