package com.tekclover.wms.api.transaction.model.tng;

import lombok.Data;

import java.util.List;


@Data
public class FetchStockResponse {
    private List<FetchStock> data;
    private String errorMessage;
    private String errorCode;
    private Boolean success;

}