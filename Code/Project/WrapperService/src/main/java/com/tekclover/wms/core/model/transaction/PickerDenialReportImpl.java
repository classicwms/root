package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class PickerDenialReportImpl {

//    String companyCodeId;

//    String plantId;

//    String languageId;

    String warehouseId;

//    String preOutboundNo;

//    String refDocNumber;

//    String partnerCode;

    Long lineNumber;

    String pickupNumber;

    String itemCode;

//    String actualHeNo;

    String pickedStorageBin;

    String pickedPackCode;

//    Long outboundOrderTypeId;

//    Double pickConfirmQty;

//    Double allocatedQty;

//    String pickUom;

    String description;

    String assignedPickerId;

//    String pickupCreatedBy;

//    String pickupConfirmedBy;

//    Date denialDate;
//    Date deliverConfirmedOn;

//    String sDeliveryDate;
//    String sDenialDate;
    String remarks;
//    String partnerName;
//    String orderType;
//    Long denialCount;
//    Long skuDenied;
//    Double percentageShipped;
    Long orderedQty;
    Long shippedQty;
//    Long skuOrdered;
//    Long skuShipped;
}