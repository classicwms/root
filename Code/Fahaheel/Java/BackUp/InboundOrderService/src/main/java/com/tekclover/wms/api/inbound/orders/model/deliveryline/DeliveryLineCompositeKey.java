package com.tekclover.wms.api.inbound.orders.model.deliveryline;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryLineCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `DLV_NO`,'ITM_CODE','OB_LINE_NO','INVOICE_NO','REF_DOC_NO'
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long deliveryNo;
    private String itemCode;
    private Long lineNumber;
    private String refDocNumber;
    private String invoiceNumber;
}
