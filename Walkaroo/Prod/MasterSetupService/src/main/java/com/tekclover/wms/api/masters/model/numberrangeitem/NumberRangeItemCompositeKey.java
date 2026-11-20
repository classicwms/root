package com.tekclover.wms.api.masters.model.numberrangeitem;

import lombok.Data;
import java.io.Serializable;


@Data
public class NumberRangeItemCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_TYP_ID`,'SEQ_NO'
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long itemTypeId;
    private Long sequenceNo;
}
