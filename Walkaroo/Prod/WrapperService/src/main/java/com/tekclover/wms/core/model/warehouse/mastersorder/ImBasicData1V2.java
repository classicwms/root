package com.tekclover.wms.core.model.warehouse.mastersorder;

import com.tekclover.wms.core.model.masters.ImBasicData1;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImBasicData1V2 extends ImBasicData1 {

    private String manufacturerName;
    private String brand;
    private String supplierPartNumber;

    private String itemTypeDescription;
    private String itemGroupDescription;
//    private String batchQuantity;
}