package com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.v2;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.SearchInventory;
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
}