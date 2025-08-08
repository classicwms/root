package com.tekclover.wms.core.model.dto;


import lombok.Data;

import java.util.Date;

@Data
public class PutawayHeader {

    private String refDocNumber;
    private String huSerialNo;
    private String type;
    private String message;
    private String sapDocumentNo;
    private Date matDocDate;

//    private String companyCode;
//    private String plantId;
//    private String warehouseId;
//    private String languageId;
}
