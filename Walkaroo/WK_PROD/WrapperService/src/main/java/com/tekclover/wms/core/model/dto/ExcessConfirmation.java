package com.tekclover.wms.core.model.dto;


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
