package com.tekclover.wms.api.transaction.model.report;

import java.util.Date;

public interface TransactionDetailDashBoardImpl {

	Double getQuantity();
	String getProcess();
	String getItemCode();
	String getBarcodeId();
	Date getCreatedOn();
}