package com.tekclover.wms.api.outbound.transaction.model.report;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OutwardReportInput {
    public List<String> companyCodeId;
    public List<String> plantId;
    public List<String> languageId;
    private List<String> warehouseId;
    public List<String> customerName;
    public List<String> orderNo;
    public List<String> sku;
    public List<String> batchNo;
    public List<Long> statusId;
    public Date fromDate;
    public Date toDate;
    private List<String> inventoryOwner;
}
