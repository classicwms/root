package com.mnrclara.spark.core.model.wmscorev2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchImBasicData1 {
    private List<String> warehouseId;
    private List<String> itemCode;
    private List<String> description;
    private List<Long> itemType;
    private List<Long> itemGroup;
    private List<Long> subItemGroup;
    private List<String> companyCodeId;
    private List<String> manufacturerPartNo;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> createdBy;
    private List<String> updatedBy;

    private Date startCreatedOn;
    private Date endCreatedOn;

    private Date startUpdatedOn;
    private Date endUpdatedOn;

}
