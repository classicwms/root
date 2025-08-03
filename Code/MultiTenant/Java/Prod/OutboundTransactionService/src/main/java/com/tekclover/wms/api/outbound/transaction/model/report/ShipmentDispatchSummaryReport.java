package com.tekclover.wms.api.outbound.transaction.model.report;

import java.util.List;

import lombok.Data;

@Data
public class ShipmentDispatchSummaryReport {

	private List<ShipmentDispatch> shipmentDispatch;
}
