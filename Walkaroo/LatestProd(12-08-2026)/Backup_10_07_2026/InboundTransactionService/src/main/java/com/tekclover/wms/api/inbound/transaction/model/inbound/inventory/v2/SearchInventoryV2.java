package com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2;

import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.SearchInventory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
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

	/*----------------Walkaroo changes------------------------------------------------------*/
	private List<String> materialNo;
	private List<String> priceSegment;
	private List<String> articleNo;
	private List<String> gender;
	private List<String> color;
	private List<String> size;
	private List<String> noPairs;
}