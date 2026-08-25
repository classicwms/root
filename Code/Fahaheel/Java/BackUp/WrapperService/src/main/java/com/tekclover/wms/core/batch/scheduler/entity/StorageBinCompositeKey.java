package com.tekclover.wms.core.batch.scheduler.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class StorageBinCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ST_BIN`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String storageBin;
}
