package com.tekclover.wms.api.idmaster.model.dateformatid;

import lombok.Data;

import java.io.Serializable;

@Data
public class DateFormatIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `DATE_FOR_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String dateFormatId;
	private String languageId;
}
