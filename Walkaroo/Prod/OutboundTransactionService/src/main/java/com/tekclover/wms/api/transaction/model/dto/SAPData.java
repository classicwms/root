package com.tekclover.wms.api.transaction.model.dto;


import lombok.Data;

@Data
public class SAPData {


    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String languageId;
    private String barcodeId;
    private String refDocNumber;
}
