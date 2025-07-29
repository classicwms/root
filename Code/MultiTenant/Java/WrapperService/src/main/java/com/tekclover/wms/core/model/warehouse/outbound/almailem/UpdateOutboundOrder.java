package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateOutboundOrder {

    private String refDocumentNo; 			                // REF_DOCument_NO
    private String refDocumentType; 		                // REF_DOC_TYPE
    private String partnerCode; 			                // PARTNER_CODE
    private String partnerName; 			                // PARTNER_NM
    private Date requiredDeliveryDate;		                // REQ_DEL_DATE
    private Date orderReceivedOn; 			                // order_received_on
    private Date orderProcessedOn;
    private Long lineReference;								// IB_LINE_NO
    private String itemCode; 								// ITM_CODE
    private String itemText; 								// ITEM_TEXT
    private Double orderedQty;								// ORD_QTY
    private String uom;										// ORD_UOM
    private String warehouseID; 			                // WH_ID
    private Long processedStatusId;			                // processed_status_id
    private Long outboundOrderTypeID;						// ORD_UOM
    private String refField1ForOrderType;                    // REF_FIELD_1
}
