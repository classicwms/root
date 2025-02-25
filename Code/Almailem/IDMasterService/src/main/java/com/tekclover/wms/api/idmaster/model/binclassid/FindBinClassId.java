package com.tekclover.wms.api.idmaster.model.binclassid;

import lombok.Data;

import java.util.List;

@Data
public class FindBinClassId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> binClassId;
    private String binClass;
    private List<String> languageId;
}
