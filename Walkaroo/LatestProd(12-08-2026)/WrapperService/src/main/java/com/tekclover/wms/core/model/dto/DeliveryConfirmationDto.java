package com.tekclover.wms.core.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DeliveryConfirmationDto {

    public String companyDesc;
    public String plantDesc;
    public String warehouseDesc;
    public String customerCode;
    public String customerName;
    public String huSerialNo;
    public String material;
    public String outboundNo;
    public String skuCode;
    public String shipToParty;
    public String shipToCode;
    public Double pickQty;
    public Date orderProcessedOn;
}
