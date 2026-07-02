package com.tekclover.wms.core.model.transaction;

import java.util.List;
import lombok.Data;

@Data
public class ShipmentDispatchSummaryReport {

	private List<ShipmentDispatch> shipmentDispatch;
}
