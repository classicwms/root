package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.List;


@Data
public class SearchBomLine {

    /*
     * WH_ID
     * CHILD_ITM_CODE
     * BOM_NO
     * LANG_ID

     */

    private List<String> warehouseId;
    private List<Long> bomNumber;
    private List<String> childItemCode;
    private List<String>companyCode;
    private List<String>languageId;
    private List<String> plantId;
    private List<Long> sequenceNo;
}
