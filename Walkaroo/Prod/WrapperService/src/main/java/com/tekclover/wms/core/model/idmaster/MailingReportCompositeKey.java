package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.io.Serializable;

@Data
public class MailingReportCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;

	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `FILE_NAME`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String languageId;
	private String fileName;
}
