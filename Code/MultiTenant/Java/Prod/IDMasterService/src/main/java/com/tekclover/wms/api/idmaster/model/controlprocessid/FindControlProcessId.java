package com.tekclover.wms.api.idmaster.model.controlprocessid;

import lombok.Data;

import java.util.List;

@Data
public class FindControlProcessId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> controlProcessId;
    private List<String> languageId;
}
