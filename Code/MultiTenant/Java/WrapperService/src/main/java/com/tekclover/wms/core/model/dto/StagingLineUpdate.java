package com.tekclover.wms.core.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StagingLineUpdate {

    private String companyCode;
    private String languageId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
    private Date manufacturerDate;
    private Date expiryDate;
    private String refDocNumber;
    private String stagingNo;
    private String printLabel;
}
