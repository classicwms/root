package com.tekclover.wms.api.idmaster.model.companyid;

import java.io.Serializable;

import lombok.Data;

@Data
public class CompanyIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `LANG_ID`
	 */
	 
	private String companyCodeId;
	private String languageId;
}
