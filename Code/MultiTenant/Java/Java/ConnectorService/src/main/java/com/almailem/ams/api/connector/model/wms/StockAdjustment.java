package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class StockAdjustment {

    private String orderId;
    private Long stockAdjustmentId;
//    private Long stockAdjustmentId;

    @Column(nullable = false)
    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "Stock Count Branch Code is mandatory")
    private String branchCode;

    private String branchName;

    @Column(nullable = false)
    @NotNull(message = "Date of Adjustment is mandatory")
    private Date dateOfAdjustment;

    private String isCycleCount;
    private String isDamage;

    @Column(nullable = false)
    @NotBlank(message = "Item Code is mandatory")
    private String itemCode;

    private String itemDescription;

    @Column(nullable = false)
    @NotNull(message = "Adjustment Qty is mandatory")
    private Double adjustmentQty;

    @Column(nullable = false)
    @NotBlank(message = "Unit of Measure is mandatory")
    private String unitOfMeasure;

    @Column(nullable = false)
    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    @Column(nullable = false)
    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    private String remarks;
    private String amsReferenceNo;
    private String isCompleted;
    private Date updatedOn;

    private Long processedStatusId;
    private Date orderReceivedOn;
    private Date orderProcessedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}