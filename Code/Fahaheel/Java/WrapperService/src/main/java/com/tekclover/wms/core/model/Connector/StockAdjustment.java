package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;

@Data
public class StockAdjustment {

    private Long stockAdjustmentId;

    private String companyCode;

    private String branchCode;

    private String branchName;

    private Date dateOfAdjustment;

    private String isCycleCount;

    private String isDamage;

    private String itemCode;

    private String itemDescription;

    private Double adjustmentQty;

    private String unitOfMeasure;

    private String manufacturerCode;

    private String manufacturerName;

    private String remarks;

    private String amsReferenceNo;

    private String isCompleted;

    private Date updatedOn;

    private Long processedStatusId;

    private Date orderReceivedOn;

    private Date orderProcessedOn;
}