package com.tekclover.wms.core.model.idmaster;

import lombok.Data;
import java.util.Date;

@Data
public class NumberRange {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long numberRangeCode;
    private Long fiscalYear;
    private String numberRangeObject;
    private Long numberRangeFrom;
    private Long numberRangeTo;
    private String numberRangeCurrent;
    private Long deletionIndicator;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
}
