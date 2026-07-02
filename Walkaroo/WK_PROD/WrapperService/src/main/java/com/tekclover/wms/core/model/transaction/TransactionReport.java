package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class TransactionReport {

    private List<String> companyId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> itemCode;
    private List<String> huNumber;


}
