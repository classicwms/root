package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

@Data
public class UpdateNumberRange {

    private String numberRangeObject;
    private Long numberRangeFrom;
    private Long numberRangeTo;
    private String numberRangeCurrent;
    private Long deletionIndicator;
}
