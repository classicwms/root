package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchImBasicData1 {
	/*
	 * WH_ID
	 * ITM_CODE
	 * TEXT
	 * ITM_TYP_ID
	 * ITM_GRP_ID	 
	 * SUB_ITM_GRP_ID
	 * CTD_BY
	 * CTD_ON
	 * UTD_BY
	 * UTD_ON
	 */
	 
	private List<String> warehouseId;	
	private List<String> itemCode;
	private List<String> description;
	private List<Long> itemType;
	private List<Long> itemGroup;
	private List<Long> subItemGroup;
	private List<String> companyCodeId;
	private List<String> manufacturerPartNo;
	private List<String> plantId;
	private List<String> languageId;
	private List<String> createdBy;
	private List<String> updatedBy;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
	
	private Date startUpdatedOn;
	private Date endUpdatedOn;

	/*----------------Walkaroo changes------------------------------------------------------*/
	private List<String> barcodeId;
	private List<String> materialNo;
	private List<String> priceSegment;
	private List<String> articleNo;
	private List<String> gender;
	private List<String> color;
	private List<String> size;
	private List<String> noPairs;
}
