package com.tekclover.wms.api.outbound.transaction.model.report;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LedgerReportInput {

    public String companyId;
    public String plantId;
    public String languageId;
    public String warehouseId;

    private List<String> inventoryOwner;
    public Date fromDate;
    public Date toDate;

}