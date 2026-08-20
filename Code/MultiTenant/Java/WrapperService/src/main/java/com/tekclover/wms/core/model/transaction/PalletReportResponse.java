package com.tekclover.wms.core.model.transaction;


import lombok.Data;

import java.util.List;

@Data
public class PalletReportResponse {

    //    private String itemCode;

    private String batch;

    private List<PalletReportDetail> details;

    private PalletReportTotals totals;
}
