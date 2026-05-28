package com.mnrclara.spark.core.model;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindImBasicData1 {

    private List<String> warehouseId;
    private List<String> itemCode;
    private List<String> description;
    private List<Long> itemType;
    private List<Long> itemGroup;
    private List<Long> subItemGroup;

    private List<String> createdBy;
    private List<String> updatedBy;

    private Date startCreatedOn;
    private Date endCreatedOn;

    private Date startUpdatedOn;
    private Date endUpdatedOn;

}
