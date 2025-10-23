package com.tekclover.wms.api.transaction.model.tng;

import lombok.Data;

@Data
public class CancelShipmentOrderResponse {

    public DataResponse data;
    public String errorMessage;
    public String errorCode;
    public Boolean success;
}
