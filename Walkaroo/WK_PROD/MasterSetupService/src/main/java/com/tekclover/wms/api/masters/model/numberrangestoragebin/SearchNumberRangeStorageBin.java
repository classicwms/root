package com.tekclover.wms.api.masters.model.numberrangestoragebin;

import lombok.Data;
import java.util.List;

@Data
public class SearchNumberRangeStorageBin {

    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> warehouseId;
    private List<Long> floorId;
    private List<String> storageSectionId;
    private List<String> rowId;
    private List<String> aisleNumber;
}
