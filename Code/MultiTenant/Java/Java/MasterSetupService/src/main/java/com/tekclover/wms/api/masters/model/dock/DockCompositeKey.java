package com.tekclover.wms.api.masters.model.dock;

import lombok.Data;
import java.io.Serializable;

@Data
public class DockCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `DK_TYPE`, `DK_ID`
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String dockType;
    private String dockId;
}
