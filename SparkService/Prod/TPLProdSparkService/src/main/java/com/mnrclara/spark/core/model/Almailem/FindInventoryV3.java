package com.mnrclara.spark.core.model.Almailem;


import lombok.Data;

import java.util.List;

@Data
public class FindInventoryV3 {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> referenceDocumentNo;
    private List<String> barcodeId;
    private List<String> manufacturerCode;
    private List<String> manufacturerName;
    private List<String> packBarcodes;
    private List<String> itemCode;
    private List<String> storageBin;
    private List<String> description;
    private List<Long> stockTypeId;
    private List<String> storageSectionId;
    private List<String> levelId;
    private List<Long> specialStockIndicatorId;
    private List<Long> binClassId;
    private List<String>threePLPartnerId;

}
