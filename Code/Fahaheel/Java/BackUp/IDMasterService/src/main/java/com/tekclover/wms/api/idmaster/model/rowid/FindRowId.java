package com.tekclover.wms.api.idmaster.model.rowid;
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
    private String rowNumber;
    private List<String> aisleId;
    private List<String> languageId;
}
