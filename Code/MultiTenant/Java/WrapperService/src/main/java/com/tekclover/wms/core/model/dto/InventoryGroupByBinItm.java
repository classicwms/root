package com.tekclover.wms.core.model.dto;

import lombok.Data;

@Data
public class InventoryGroupByBinItm {

    public String itemText;
    public String languageId;
    public String companyCodeId;
    public String plantId;
    public String warehouseId;
    public Double invQty;
    public Double allQty;
    public Double totQty;
    public Double noBags;
    public Double bagSizes;
    public String manufacturerName;
    public String storageBin;
    public String itemCode;

}
