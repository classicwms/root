package com.tekclover.wms.api.idmaster.model.threepl.billingformatid;

import lombok.Data;
import java.io.Serializable;
@Data
public class BillingFormatIdCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;
    /*
     * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`BILL_FORMAT_ID`
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long billFormatId;
}
