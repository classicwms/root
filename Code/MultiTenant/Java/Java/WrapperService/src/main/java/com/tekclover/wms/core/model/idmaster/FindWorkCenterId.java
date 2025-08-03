package com.tekclover.wms.core.model.idmaster;

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
