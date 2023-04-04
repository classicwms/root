package com.tekclover.wms.api.idmaster.model.threepl.servicetypeid;


import lombok.Data;

import java.io.Serializable;
@Data
public class ServiceTypeIdCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;
    /*
     * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,'MOD_ID',`SER_TYP_ID`
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long moduleId;
    private Long serviceTypeId;
}
