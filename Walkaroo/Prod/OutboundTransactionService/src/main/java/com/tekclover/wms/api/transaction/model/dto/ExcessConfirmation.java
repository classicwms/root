package com.tekclover.wms.api.transaction.model.dto;


import lombok.Data;

@Data
public class ExcessConfirmation {

    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String languageId;
    private double quantity;
    private String barcodeId;

}
