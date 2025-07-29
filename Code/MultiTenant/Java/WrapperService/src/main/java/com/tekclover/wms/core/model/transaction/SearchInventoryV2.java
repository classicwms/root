package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SearchInventoryV2 extends SearchInventory {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

	private List<String> barcodeId;
	private List<String> levelId;
	private List<String> manufacturerCode;
	private List<String> manufacturerName;
	private List<String> referenceDocumentNo;
	private List<String> partnerCode;
	private List<Long> itemType;

	private List<String> alternateUom;
	private List<String> customerId;

}