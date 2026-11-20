package com.tekclover.wms.api.inbound.transaction.model.warehouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class FileUpdateUpload {

    private String barcodeId;
    private String materialNo;
    private String itemCode;
    private String priceSegment;

}
