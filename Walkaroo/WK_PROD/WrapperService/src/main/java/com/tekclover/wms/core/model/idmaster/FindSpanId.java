package com.tekclover.wms.core.model.idmaster;
import lombok.Data;
import java.util.List;

@Data
public class FindSpanId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> floorId;
    private List<String> storageSectionId;
    private List<String> aisleId;
    private List<String> rowId;
    private List<String> spanId;
    private List<String>languageId;
}
