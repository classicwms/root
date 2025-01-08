package com.tekclover.wms.api.transaction.model.mnc;

import lombok.Data;

import java.util.List;

@Data
public class ExecuteStockCountInput {

//    private List<String> companyCodeId;
//    private List<String> languageId;
//    private List<String> plantId;
//    private List<String> warehouseId;
    private List<String> itemCode;
    private List<String> manufacturerName;
    private List<String> storageBin;
    private List<String> barcodeId;
    private List<Long> binClassId;
    private List<Long> stockTypeId;
    private List<String> storageSectionId;
}