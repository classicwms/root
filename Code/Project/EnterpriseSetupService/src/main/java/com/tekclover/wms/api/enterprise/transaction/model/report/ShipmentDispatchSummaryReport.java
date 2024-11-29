package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

import java.util.List;

@Data
public class ShipmentDispatchSummaryReport {

	private List<ShipmentDispatch> shipmentDispatch;
}