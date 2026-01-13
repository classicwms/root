package com.tekclover.wms.api.masters.model.tng;

import lombok.Data;

@Data
public class SKUResponse {

    public String data;
    public String errorMessage;
    public String errorCode;
    public Boolean success;

}
