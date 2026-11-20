package com.tekclover.wms.api.transaction.model.kafka;


import lombok.Data;

@Data
public class TransferLines {

    private String sourceItemCode;
    private String sourceBarcodeId;
    private String sourceStorageBin;
    private String targetItemCode;
    private String targetBarcodeId;
    private String targetStorageBin;
}
