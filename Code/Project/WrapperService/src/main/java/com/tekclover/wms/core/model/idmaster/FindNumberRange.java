package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindNumberRange {
    private String warehouseId;
    private List<Long> numberRangeCode;
    private List<Long> fiscalYear;
}
