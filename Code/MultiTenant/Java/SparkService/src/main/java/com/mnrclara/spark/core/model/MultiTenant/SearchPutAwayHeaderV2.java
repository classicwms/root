package com.mnrclara.spark.core.model.MultiTenant;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class SearchPutAwayHeaderV2 {

    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<String> packBarcodes;
    private List<String> putAwayNumber;
    private List<String> proposedStorageBin;
    private List<String> proposedHandlingEquipment;
    private List<Long> statusId;
    private List<String> createdBy;
    private Date startCreatedOn;
    private Date endCreatedOn;

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> barcodeId;
    private List<String> manufacturerCode;
    private List<String> manufacturerName;
    private List<String> origin;
    private List<String> brand;
    private List<String> approvalStatus;
    private List<String> customerId;
    private List<String> palletCode;

}
