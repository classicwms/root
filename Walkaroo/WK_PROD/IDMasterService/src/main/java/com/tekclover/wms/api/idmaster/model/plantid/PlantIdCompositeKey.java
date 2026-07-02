package com.tekclover.wms.api.idmaster.model.plantid;

import java.io.Serializable;

import lombok.Data;

@Data
public class PlantIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String languageId;
}
