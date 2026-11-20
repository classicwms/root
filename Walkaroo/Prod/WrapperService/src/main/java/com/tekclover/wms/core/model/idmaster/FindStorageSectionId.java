package com.tekclover.wms.core.model.idmaster;
import lombok.Data;
import java.util.List;
@Data
public class FindStorageSectionId {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> floorId;
    private List<String> storageSectionId;
    private List<String> storageSection;
    private List<String> languageId;
}
