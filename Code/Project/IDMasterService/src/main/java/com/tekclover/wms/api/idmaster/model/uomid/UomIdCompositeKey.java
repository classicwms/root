package com.tekclover.wms.api.idmaster.model.uomid;

import java.io.Serializable;

import lombok.Data;

@Data
public class UomIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `UOM_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String uomId;
	private String languageId;
}
