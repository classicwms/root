package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindRefDocTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> referenceDocumentTypeId;
    private List<String> languageId;
}
