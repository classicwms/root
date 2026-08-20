package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MergingPelletReportInput {

    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> languageId;
    private List<String> inventoryOwner;
    private Date fromDate;
    private Date toDate;
}
