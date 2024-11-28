package com.tekclover.wms.api.transaction.model.packing;

import lombok.Data;

import java.io.Serializable;

@Data
public class PackingInventoryCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    private String languageId;
    private String companyId;
    private String plantId;
    private String warehouseId;
    private String packingMaterialNo;


}
