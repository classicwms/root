package com.tekclover.wms.core.model.warehouse.mastersorder;

import com.tekclover.wms.core.model.masters.ImBasicData1;
import lombok.Data;

@Data
public class ImBasicData1V2 extends ImBasicData1 {

    private String manufacturerName;
    private String brand;
    private String supplierPartNumber;
}
