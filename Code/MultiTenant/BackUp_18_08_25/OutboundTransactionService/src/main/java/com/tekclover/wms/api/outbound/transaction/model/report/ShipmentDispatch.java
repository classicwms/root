package com.tekclover.wms.api.outbound.transaction.model.report;

import java.util.List;

import lombok.Data;

@Data
public class ShipmentDispatch {

	private ShipmentDispatchHeader header;
	private List<ShipmentDispatchList> shipmentDispatchList;
}

