package com.tekclover.wms.api.idmaster.model.processid;

import lombok.Data;

import java.util.List;

@Data
public class FindProcessId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> processId;
    private List<String> languageId;
}
