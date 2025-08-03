package com.tekclover.wms.api.idmaster.model.handlingunitid;

import lombok.Data;
import java.util.List;

@Data
public class FindHandlingUnitId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> handlingUnitNumber;
    private List<String> languageId;
}
