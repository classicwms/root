package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class CBMUtilizationReportInput {

    public String companyCode;

    public String languageId;

    public String plantId;

    public String warehouseId;

    public String businessPartnerCode;

    public Date fromDate;

    public Date toDate;
}
