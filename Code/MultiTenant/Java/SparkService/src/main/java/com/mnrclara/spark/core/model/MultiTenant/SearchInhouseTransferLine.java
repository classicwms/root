package com.mnrclara.spark.core.model.MultiTenant;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class SearchInhouseTransferLine {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> transferNumber;
    private List<String> sourceItemCode;
    private List<Long> sourceStockTypeId;
    private List<String> sourceStorageBin;
    private List<String> targetItemCode;
    private List<Long> targetStockTypeId;
    private List<String> targetStorageBin;
    private List<Double> transferConfirmedQty;
    private List<String> packBarcodes;
    private List<Double> availableQty;
    private List<Long> statusId;
    private List<String> remarks;
    private List<String> createdBy;
    private List<String> confirmedBy;
    private List<String> referenceField1;

    private Date startCreatedOn;
    private Date endCreatedOn;
    private Date startConfirmedOn;
    private Date endConfirmedOn;
}
