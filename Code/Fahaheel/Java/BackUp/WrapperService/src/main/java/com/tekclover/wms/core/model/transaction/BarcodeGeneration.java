package com.tekclover.wms.core.model.transaction;

import lombok.Data;


@Data
public class BarcodeGeneration {

    private String companyCodeId;

    private String plantId;

    private String languageId;

    private String warehouseId;

    private String itemCode;

    private String itemDescription;

    private String weight;

    private String barcodeId;


}
