package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound;

import lombok.Data;

@Data
public class FileUpdateUpload {

    private String barcodeId;
    private Double inventoryQuantity;
    private Double referenceField4;

}
