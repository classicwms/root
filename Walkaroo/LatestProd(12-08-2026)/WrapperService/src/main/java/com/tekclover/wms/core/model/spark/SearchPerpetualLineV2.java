package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class SearchPerpetualLineV2 {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> cycleCountNo;
    private List<Long> lineStatusId;
    private List<String> cycleCounterId;

    private Date startCreatedOn;
    private Date endCreatedOn;

    private List<String> itemCode;
    private List<String> storageBin;
    private List<String> packBarcodes;
    private List<Long> stockTypeId;
    private List<String> manufacturerPartNo;
    private List<String> storageSectionId;


}
