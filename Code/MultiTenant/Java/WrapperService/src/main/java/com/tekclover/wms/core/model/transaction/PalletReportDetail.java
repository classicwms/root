package com.tekclover.wms.core.model.transaction;


import lombok.Data;

@Data
public class PalletReportDetail {

    private String itemCode;
    private String location;
    private String inventoryOwner;
    private String batch;
    private Double currentQty;
    private Double bestFit;
    private Double inventoryQty;
}
