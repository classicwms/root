package com.tekclover.wms.api.idmaster.model.threepl.billingmodeid;

import lombok.Data;
import java.io.Serializable;
@Data
public class BillingModeIdCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;
    /*
     * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`BILL_MODE_ID`
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long billModeId;
}
