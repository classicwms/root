package com.tekclover.wms.api.idmaster.model.refdoctypeid;

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
