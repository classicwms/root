package com.tekclover.wms.api.inbound.transaction.model;

import lombok.Data;

@Data
public class SAPPostResult {

    private boolean success;
    private String response;
    private String exceptionType;
    private String exceptionMessage;
//    private String exceptionDetails;
    private Integer httpStatus;

    // Getters and Setters
}