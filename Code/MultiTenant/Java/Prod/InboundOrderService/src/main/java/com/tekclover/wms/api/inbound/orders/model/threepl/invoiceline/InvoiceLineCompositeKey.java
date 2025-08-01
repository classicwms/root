package com.tekclover.wms.api.inbound.orders.model.threepl.invoiceline;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvoiceLineCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`INVOICE_NO`,`PARTNER_CODE`,'LINE_NO'
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String invoiceNumber;
    private String partnerCode;
    private Long lineNumber;
}
