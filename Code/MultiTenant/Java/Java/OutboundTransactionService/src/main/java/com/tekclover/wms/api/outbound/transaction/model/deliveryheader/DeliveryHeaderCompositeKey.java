package com.tekclover.wms.api.outbound.transaction.model.deliveryheader;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryHeaderCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `DLV_NO`,
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long deliveryNo;
}
