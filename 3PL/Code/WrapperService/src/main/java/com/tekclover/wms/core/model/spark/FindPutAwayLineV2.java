package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPutAwayLineV2 {

    private List<String> warehouseId;
    private List<String> goodsReceiptNo;
    private List<String> preInboundNo;
    private List<String> refDocNumber;
    private List<String> putAwayNumber;
    private List<Long> lineNo;
    private List<String> itemCode;
    private List<String> proposedStorageBin;
    private List<String> confirmedStorageBin;
    private List<String> packBarCodes;

    private Date fromConfirmedDate;
    private Date toConfirmedDate;
    private Date fromCreatedDate;
    private Date toCreatedDate;

    // V2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> barcodeId;
    private List<String> manufacturerCode;
    private List<String> manufacturerName;
    private List<String> origin;
    private List<String> brand;
    private List<Long> statusId;
    private List<String> confirmedBy;
    private List<Long> inboundOrderTypeId;
    private List<String> businessPartnerCode;
}
