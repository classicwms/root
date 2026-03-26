package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class FileUpdateUpload {

    private String barcodeId;
    private Double inventoryQuantity;
    private Double referenceField4;

}
