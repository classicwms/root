package com.tekclover.wms.api.outbound.transaction.model.report;

import java.util.Date;

public interface StockMovementLedgerReport {

    Date getDate();
    Double getOutwardCases();
    Double getOutwardPallets();
    Double getOutwardWeight();
    Double getInwardCases();
    Double getInwardPallets();
    Double getInwardWeight();
    Double getOpeningCases();
    Double getOpeningPallets();
    Double getOpeningWeight();
    Double getClosingCases();
    Double getClosingPallets();
    Double getClosingWeight();
    String getRemarks();
}