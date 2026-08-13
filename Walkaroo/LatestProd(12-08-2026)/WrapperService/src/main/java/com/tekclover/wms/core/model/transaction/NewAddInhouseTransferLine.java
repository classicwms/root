package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class NewAddInhouseTransferLine {

    private String sourceBarcodeId;
    private String targetStorageBin;

}
