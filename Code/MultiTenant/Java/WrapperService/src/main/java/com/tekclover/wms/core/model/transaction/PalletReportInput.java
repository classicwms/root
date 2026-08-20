package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PalletReportInput {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> warehouseId;
    private List<String> binLocation;
    private List<String> inventoryOwner;
    private List<String> skuCode;
    private List<String> batch;
    private Date fromDate;
    private Date toDate;
}
