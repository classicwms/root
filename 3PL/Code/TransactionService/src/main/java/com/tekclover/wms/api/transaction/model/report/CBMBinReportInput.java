package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

@Data
public class CBMBinReportInput {

    public String companyCode;

    public String languageId;

    public String plantId;

    public String warehouseId;

    public String threePLPartnerId;
}
