package com.tekclover.wms.core.model.idmaster;
import lombok.Data;

import java.util.List;

@Data
public class FindModuleId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> moduleId;
    private List<String>languageId;
    private List<Long> menuId;
    private List<Long> subMenuId;
}
