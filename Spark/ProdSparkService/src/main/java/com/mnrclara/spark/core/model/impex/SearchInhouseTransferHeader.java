package com.mnrclara.spark.core.model.impex;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchInhouseTransferHeader {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> transferNumber;
    private List<Long> transferTypeId;

    private Date startCreatedOn;
    private Date endCreatedOn;
}