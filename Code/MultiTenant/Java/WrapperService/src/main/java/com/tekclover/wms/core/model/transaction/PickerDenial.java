package com.tekclover.wms.core.model.transaction;


import lombok.Data;

@Data
public class PickerDenial {

    private String barcodeId;
    private String itemCode;
    private String companyCodeId;
    private String languageId;
    private String warehouseId;
    private String plantId;
    private String refDocNo;

    private String palletCode;

}
