package com.tekclover.wms.api.idmaster.model.aisleid;
import lombok.Data;
import java.util.List;

@Data
public class FindAisleId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> floorId;
    private List<String> storageSectionId;
    private List<String> aisleId;
    private List<String>languageId;
}
