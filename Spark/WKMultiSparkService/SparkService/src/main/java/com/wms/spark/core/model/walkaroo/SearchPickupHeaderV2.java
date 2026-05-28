package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.util.List;

@Data
public class SearchPickupHeaderV2 {

    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<String> partnerCode;
    private List<String> pickupNumber;
    private List<String> itemCode;
    private List<String> proposedStorageBin;
    private List<String> proposedPackCode;
    private List<Long> outboundOrderTypeId;
    private List<Long> statusId;
    private List<String> referenceField1;
    private List<String> assignedPickerId;


    //V2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> levelId;
    private List<String> barcodeId;
    private List<String> materialNo;
    private List<String> priceSegment;
    private List<String> articleNo;
    private List<String> gender;
    private List<String> color;
    private List<String> size;
    private List<String> noPairs;
    private List<String> docStatus;

    private List<String> shipToParty;
    private List<String> shipToCode;

}
