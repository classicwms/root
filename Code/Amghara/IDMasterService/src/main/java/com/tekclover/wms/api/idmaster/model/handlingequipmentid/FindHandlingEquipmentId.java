package com.tekclover.wms.api.idmaster.model.handlingequipmentid;

import lombok.Data;
import java.util.List;

@Data
public class FindHandlingEquipmentId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> handlingEquipmentNumber;
    private List<String> languageId;

}
