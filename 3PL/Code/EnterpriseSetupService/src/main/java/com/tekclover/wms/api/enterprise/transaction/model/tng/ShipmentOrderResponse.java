package com.tekclover.wms.api.enterprise.transaction.model.tng;

import lombok.Data;

@Data
public class ShipmentOrderResponse {

    public String data;
    public String errorMessage;
    public String errorCode;
    public Boolean success;
}
