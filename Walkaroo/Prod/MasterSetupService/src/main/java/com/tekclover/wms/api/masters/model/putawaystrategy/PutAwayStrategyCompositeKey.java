package com.tekclover.wms.api.masters.model.putawaystrategy;

import lombok.Data;

import java.io.Serializable;

@Data
public class PutAwayStrategyCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `BRAND`, 'ARTICLE', 'CATEGORY'
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String brand;
    private String article;
    private String category;
    private String gender;
}
