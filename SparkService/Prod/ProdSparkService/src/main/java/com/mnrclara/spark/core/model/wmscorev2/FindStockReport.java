package com.mnrclara.spark.core.model.wmscorev2;

import lombok.Data;

import java.util.List;

@Data
public class FindStockReport {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> itemCode;
//    private List<String> itemText;
//    private List<String> stockTypeText;
}
