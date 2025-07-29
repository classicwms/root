package com.tekclover.wms.api.idmaster.model.threepl.servicetypeid;

import lombok.Data;
import java.util.List;

@Data
public class FindServiceTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> languageId;
    private List<String> moduleId;
    private List<Long> serviceTypeId;
    private List<Long> statusId;
}

