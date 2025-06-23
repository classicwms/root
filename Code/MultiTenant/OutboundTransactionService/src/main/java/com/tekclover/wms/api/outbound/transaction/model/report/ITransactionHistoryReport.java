package com.tekclover.wms.api.outbound.transaction.model.report;

public interface ITransactionHistoryReport {

	public Double getValue();
	public String getItemCode();
	public String getDescription();

	public Double getClosingStock();
	public Double getOpeningStock();
	public Double getInboundQty();
	public Double getOutboundQty();
	public Double getStockAdjustmentQty();

	public String getWarehouseId();
	public String getItemDescription();
}
