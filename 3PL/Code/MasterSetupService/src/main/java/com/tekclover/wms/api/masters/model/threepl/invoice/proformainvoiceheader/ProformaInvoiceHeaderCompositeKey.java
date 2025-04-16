package com.tekclover.wms.api.masters.model.threepl.invoice.proformainvoiceheader;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProformaInvoiceHeaderCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`PROFORMA_BILL_NO`,`PARTNER_CODE`
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String proformaBillNo;
    private String partnerCode;
}
