package com.tekclover.wms.api.inbound.transaction.model.report;

public interface PickingProductivityImpl {

	Double getLeadTime();
	Double getPartsPerHr();
	Double getAvgLeadTime();
	Double getParts();
	Double getOrders();
	String getCompanyCodeId();
	String getPlantId();
	String getLanguageId();
	String getWarehouseId();
	String getCompanyDescription();
	String getPlantDescription();
	String getWarehouseDescription();
	String getAssignedPickerId();
	Double getTotalPartsPerHr();
	Double getTotalLeadTime();
	Double getAvgTotalLeadTime();
	Double getTotalParts();
	Double getTotalOrders();
}
