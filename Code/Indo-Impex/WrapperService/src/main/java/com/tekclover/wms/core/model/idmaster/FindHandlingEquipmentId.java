package com.tekclover.wms.core.model.idmaster;

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
