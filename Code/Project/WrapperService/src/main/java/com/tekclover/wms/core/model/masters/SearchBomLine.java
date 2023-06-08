package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class SearchBomLine {

    /*
     * WH_ID
     * CHILD_ITM_CODE
     * BOM_NO
     * LANG_ID
     * CTD_BY
     * CTD_ON
     * UTD_BY
     * UTD_ON
     */

    private List<String> warehouseId;
    private List<Long> bomNumber;
    private List<String> childItemCode;
    private List<String>companyCode;
    private List<String>languageId;

    private List<String> createdBy;
    private List<String> updatedBy;

    private Date startCreatedOn;
    private Date endCreatedOn;

    private Date startUpdatedOn;
    private Date endUpdatedOn;
}
