package com.tekclover.wms.core.model.idmaster;
import lombok.Data;
import java.util.List;

@Data
public class FindAdhocModuleId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> moduleId;
    private List<String> adhocModuleId;
    private List<String>languageId;

}
