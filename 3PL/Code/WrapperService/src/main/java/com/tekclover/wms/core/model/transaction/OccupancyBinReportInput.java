package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class OccupancyBinReportInput {

    public String companyCode;

    public String languageId;

    public String plantId;

    public String warehouseId;

    public String threePLPartnerId;

}
