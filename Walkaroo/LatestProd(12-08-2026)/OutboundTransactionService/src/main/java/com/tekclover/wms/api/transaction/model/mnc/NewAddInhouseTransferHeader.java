package com.tekclover.wms.api.transaction.model.mnc;

import lombok.Data;

import java.util.List;

@Data
public class NewAddInhouseTransferHeader {

    private String companyCodeId;

    private String languageId;

    private String plantId;

    private String warehouseId;

    private List<NewAddInhouseTransferLine> inhouseTransferLine;
}
