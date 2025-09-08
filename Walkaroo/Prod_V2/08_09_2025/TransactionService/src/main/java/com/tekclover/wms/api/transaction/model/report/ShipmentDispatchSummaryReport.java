package com.tekclover.wms.api.transaction.model.report;

import java.util.List;

import lombok.Data;

@Data
public class ShipmentDispatchSummaryReport {

	private List<ShipmentDispatch> shipmentDispatch;
}
