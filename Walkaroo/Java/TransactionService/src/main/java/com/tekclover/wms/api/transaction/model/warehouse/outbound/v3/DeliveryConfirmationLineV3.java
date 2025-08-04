package com.tekclover.wms.api.transaction.model.warehouse.outbound.v3;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DeliveryConfirmationLineV3 {
    private Long deliveryId;
    private String outbound;                              
    private String customerCode;                                      
    private String customer;                           
    private String skuCode;  
    private String material;  
    private String priceSegement;  
    private String articleNumber;  
    private String gender;  
    private String color;  
    private String size;  
    private String noOfPairs;  
    private String huSerialNo;  
    private Double pickedQty;
    private String plant;
    private String storageLocation;
}
