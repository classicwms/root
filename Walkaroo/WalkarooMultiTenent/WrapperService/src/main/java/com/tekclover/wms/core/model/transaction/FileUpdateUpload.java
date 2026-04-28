package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class FileUpdateUpload {

    private String barcodeId;
    private String materialNo;
    private String itemCode;
    private String priceSegment;

}
