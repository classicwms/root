package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.util.List;

@Data
public class FindInventoryV2 {

    private List<String> warehouseId;
    private List<String> packBarcodes;
    private List<String> itemCode;
    private List<String> storageBin;
    private List<String> storageSectionId;
    private List<Long> stockTypeId;
    private List<Long> specialStockIndicatorId;
    private List<Long> binClassId;
    private List<String> description;
    private List<Long> itemTypeId;

    // V2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> barcodeId;
    private List<String> manufacturerCode;
    private List<String> manufacturerName;
    private List<String> referenceDocumentNo;
    private List<String> partnerCode;
    private List<String> levelId;
    private List<String> customerId;
    private List<String> customerName;

}