package com.mnrclara.spark.core.model.Almailem;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ImBasicData1V3 {

    private String uomId;
//    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
    private String manufacturerPartNo;
    private String description;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String createdBy;
    private Timestamp createdOn;
//    private String updatedBy;
//    private Timestamp updatedOn;
}
