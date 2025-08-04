package com.tekclover.wms.api.transaction.model.threepl.invoiceheader;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvoiceHeaderCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`INVOICE_NO`,'PARTNER_CODE'
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String invoiceNumber;
    private String partnerCode;

}
