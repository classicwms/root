package com.tekclover.wms.api.idmaster.model.workcenterid;

import lombok.Data;

import java.util.List;

@Data
public class FindWorkCenterId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> workCenterId;
    private List<String> languageId;
}
