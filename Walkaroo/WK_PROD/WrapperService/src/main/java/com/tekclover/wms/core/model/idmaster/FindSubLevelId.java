package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindSubLevelId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> levelId;
    private List<String> subLevelId;
    private List<String> languageId;
}
