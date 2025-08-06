package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class DeliveryConfirmation {

    private Long deliveryId;
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String loginUserId;
    private Date orderReceivedOn;
    private Date orderProcessedOn;
    private Long processedStatusId;
    private String outbound;
    private String customerCode;
    private String customer;
    private String skuCode;
    private String material;
    private String priceSegment;
    private String articleNumber;
    private String gender;
    private String color;
    private String size;
    private String noOfPairs;
    private String huSerialNo;
    private Double pickedQty;
    private String plant;
    private String storageLocation;
    private String remark;
}