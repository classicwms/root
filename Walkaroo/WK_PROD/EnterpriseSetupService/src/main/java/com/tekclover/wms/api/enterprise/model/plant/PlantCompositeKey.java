package com.tekclover.wms.api.enterprise.model.plant;

import java.io.Serializable;

import lombok.Data;

@Data
public class PlantCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`
	 */
	 
	private String languageId;
	private String companyId;
	private String plantId;
}
