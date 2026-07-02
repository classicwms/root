package com.tekclover.wms.api.transaction.model.inbound.stock.v2;

import com.tekclover.wms.api.transaction.model.inbound.stock.SearchInventoryStock;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchInventoryStockV2 extends SearchInventoryStock {
	/*
	 * WH_ID
	 * PACK_BARCODE
	 * ITM_CODE
	 * ST_BIN
	 * STCK_TYP_ID
	 * SP_ST_IND_ID
	 */
	 
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> languageId;
}