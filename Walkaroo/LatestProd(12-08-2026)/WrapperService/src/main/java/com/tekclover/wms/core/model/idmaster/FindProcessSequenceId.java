package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindProcessSequenceId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> processId;
    private List<Long> processSequenceId;
    private List<String> languageId;
}
