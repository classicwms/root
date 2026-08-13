package com.tekclover.wms.core.model.idmaster;
import lombok.Data;
import java.util.List;

@Data
public class FindRowId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> floorId;
    private List<String> storageSectionId;
    private List<String> rowId;
    private List<String> aisleId;
    private String rowNumber;
    private List<String> languageId;
}
