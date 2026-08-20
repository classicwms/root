package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class FindPackingLine {

    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<String> itemCode;
    private List<Long> lineNumber;
    private List<String> packingNo;
}
