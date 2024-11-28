package com.tekclover.wms.core.model.warehouse.outbound.walkaroo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DeliveryConfirmationLineV3 {
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
    private String pickedQty;  
}
