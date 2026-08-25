package com.tekclover.wms.api.idmaster.model.adhocmoduleid;
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
