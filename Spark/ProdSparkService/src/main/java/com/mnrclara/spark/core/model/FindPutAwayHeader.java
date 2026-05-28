package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPutAwayHeader {
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
}
