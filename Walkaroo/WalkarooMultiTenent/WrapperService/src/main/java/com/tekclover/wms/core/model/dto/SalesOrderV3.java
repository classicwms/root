package com.tekclover.wms.core.model.dto;


import lombok.Data;

@Data
public class SalesOrderV3 {

	private String salesOrderNumber;    // REF_DOC_NO;
	private String branchCode;			// PLANT_ID
    private String warehouseId;			// WH_ID
    private Long itm;					// MATERIAL_NO
    private String customerCode;		// PARTNER_ID
    private String customer;			// PARTNER_NAME
    private String shipToCode;			// SHIP_TO
    private String shipToParty;			// SHIP_TO_NAME
    private String priceSegment;		// PRICE_SEGMENT
    private Double qty;					// ORD_QTY
    private String specialStock;		// SP_STOCK_IND
    private String mtoNumber;			// MTO_NUMBER
    private String skuCode;				// ITM_CODE
    private Double pairQty;				// PAIR_QTY
    private String materialNo;			// MATERIAL_NO
    private Long lineReference;         // LINE_NO

}
