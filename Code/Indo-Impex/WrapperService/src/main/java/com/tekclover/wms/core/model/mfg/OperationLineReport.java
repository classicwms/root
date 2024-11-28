package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OperationLineReport {

    private String companyCodeId;

    private String plantId;

    private String languageId;

    private String warehouseId;

    private String productionOrderNo;

    private String itemCode;

    private String itemDescription;

    private Double batchQuantity;

    private Double orderQuantity;

    private Double expectedQuantity;

    private Double actualQuantity;

    private String batchNumber;

    private Date batchDate;

    private String operationNumber;

    private String phaseNumber;

    private String operationDescription;

    private String phaseDescription;

    private Double yieldPercentage;

    private Double lossQuantity;

    private Double lossPercentage;

    private Long statusId;

    private String statusDescription;

    private String createdBy;

    private Date createdOn;

    private String updatedBy;

    private Date updatedOn;

    private String orderConfirmedBy;

    private Date orderConfirmedOn;

    private String uom;

    List<OperationConsumptionImpl> operationConsumptionReports;

    private List<ProcessReport> process;
}