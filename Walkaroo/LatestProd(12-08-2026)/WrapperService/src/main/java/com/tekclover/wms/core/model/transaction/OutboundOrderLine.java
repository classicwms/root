package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class OutboundOrderLine {
    private Long id;

    private Long lineReference;                                // IB_LINE_NO
    private String itemCode;                                // ITM_CODE
    private String itemText;                                // ITEM_TEXT
    private Double orderedQty;                                // ORD_QTY
    private String uom;                                        // ORD_UOM
    private String refField1ForOrderType;                    // REF_FIELD_1
    private String orderId;
}
