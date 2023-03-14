package com.tekclover.wms.api.masters.model.threepl.pricelist;

import lombok.Data;

import java.io.Serializable;

@Data
public class PriceListCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`MOD_ID`,`PRICE_LIST_ID`,'SER_TYP_ID'
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long module;
    private Long priceListId;
    private Long serviceTypeId;
}
