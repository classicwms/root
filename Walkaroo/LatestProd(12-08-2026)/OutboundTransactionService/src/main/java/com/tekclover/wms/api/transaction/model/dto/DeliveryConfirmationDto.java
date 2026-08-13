package com.tekclover.wms.api.transaction.model.dto;

import java.util.Date;

public interface DeliveryConfirmationDto {

    public String getCompanyDesc();
    public String getPlantDesc();
    public String getWarehouseDesc();
    public String getCustomerCode();
    public String getCustomerName();
    public String getHuSerialNo();
    public String getMaterial();
    public String getOutboundNo();
    public String getSkuCode();
    public String getShipToParty();
    public String getShipToCode();
    public Double getPickQty();
    public Date getOrderProcessedOn();
}
