package com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class InboundIntegration {

	@JsonIgnore
	private Long id;
	
	@NotBlank(message = "Referrence Document Number cannot be blank")
	private String referrenceDocumentNo; 	// REF_DOC_NO
	
	@NotBlank(message = "Inbound LineNumber cannot be blank")
	private Integer lineNumber; 			// IB__LINE_NO
	
	@NotBlank(message = "WarehouseID cannot be blank")
	private String warehouseID; 			// WH_ID
	
	private String invoiceNo; 				// INV_NO
	
	@NotBlank(message = "ItemCode cannot be blank")
	private String itemCode; 				// ITM_CODE
	
	private Date expectedArrivalDate; 	// EA_DATE
	
	@NotBlank(message = "Item description cannot be blank")
	private String itemDescription; 		// ITEM_TEXT
	
	@NotNull(message = "Expected Quantity cannot be blank")
	private Double expectedQuantity; 		// EXP_QTY
	
	private String orderedUnitOfMeasure; 	// ORD_UOM
	private Double itemCaseQty; 			// ITM_CASE_QTY
	private String storerKey; 				// STORER_KEY
	private String partnerCode; 			// PARTNER_CODE
	private String partnerName; 			// PARTNER_NM
	private String manufacturerPartNo; 		// MFR_PART
	private String manufacturer; 			// MFR
	private String manufacturerName; 		// MFR_NM
	private String storageSectionId;
	private String itemStatus; 				// ITEM_STATUS
	private String asnType; 				// ASN_TYPE
	private String mainCategoryCode; 		// MAIN_CAT_CODE
	private String itemCategoryName; 		// ITM_CAT_NAME
	private String remarks; 				// REMARKS
	private String currencyCode; 			// CURR_CODE
	private String exchangeRate; 			// EXCHANGE_RATE
	private String unitPrice; 				// UNIT_PRICE
	private String discountRate; 			// DISCOUNT_RATE
	private String type; 					// TYPE
	private String variantBarcode; 			// VAR_BARCODE
	private String variantDescription; 		// VAR_DESCRIPTION
	
	@JsonIgnore
	private Integer deletionIndicator = 0; 	// Is_deleted
	
	@JsonIgnore
	private Date orderReceivedOn; 			// ORD_REC_ON
	
	@JsonIgnore
	private String orderReceivedBy; 			// ORD_REC_ON
}
