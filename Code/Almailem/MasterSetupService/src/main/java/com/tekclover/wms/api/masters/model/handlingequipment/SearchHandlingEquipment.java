package com.tekclover.wms.api.masters.model.handlingequipment;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchHandlingEquipment {
	/*
	 * WH_ID
	 * HE_ID
	 * CATEGORY
	 * HU_UNIT
	 * STATUS_ID
	 * CTD_BY
	 * CTD_ON
	 * UTD_BY
	 * UTD_ON
	 */
	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> warehouseId;
	private List<String> handlingEquipmentId;
	private List<String> category;
	private List<String> handlingUnit;	
	private List<Long> statusId;


}
