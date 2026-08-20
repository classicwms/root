package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class RewindingInput {

    private List<String> sourceBarcodeId;

    private List<String> targetBarcodeId;

    private List<String> companyCodeId;

    private List<String> plantId;

    private List<String> languageId;

    private List<String> warehouseId;

    private List<String> targetStorageBin;

    private Double transferQty;
}
