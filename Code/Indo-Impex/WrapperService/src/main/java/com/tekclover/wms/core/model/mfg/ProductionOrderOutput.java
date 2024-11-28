package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductionOrderOutput {

        private String companyCodeId;
        private String plantId;
        private String languageId;
        private String warehouseId;
        private String productionOrderNo;
        private Date orderStartDate;
        private Date orderEndDate;
        private Double totalOrderQuantity;
        private Double totalConfirmedQuantity;
        private Long numberOfBatches;
        private Double receipePercentage;
        private String remarks;
        private Long statusId;
        private String statusDescription;
        private Long deletionIndicator;
        private String referenceField1;
        private String referenceField2;
        private String referenceField3;
        private String referenceField4;
        private String referenceField5;
        private String referenceField6;
        private String referenceField7;
        private String referenceField8;
        private String referenceField9;
        private String referenceField10;
        private String createdBy;
        private Date createdOn;
        private String updatedBy;
        private Date updatedOn;
        private String orderConfirmedBy;
        private Date orderConfirmedOn;
        private String companyDescription;
        private String plantDescription;
        private String warehouseDescription;
        private String parentProductionOrderNo;
        private String productionOrderType;
    public List<OperationLine> productionLines;

}