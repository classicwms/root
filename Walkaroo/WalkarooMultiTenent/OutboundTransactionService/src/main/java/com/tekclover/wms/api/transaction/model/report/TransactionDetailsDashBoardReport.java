package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import java.util.List;

@Data
public class TransactionDetailsDashBoardReport {

    String type;
    List<TransactionDetailDashBoardImpl> lines;
}