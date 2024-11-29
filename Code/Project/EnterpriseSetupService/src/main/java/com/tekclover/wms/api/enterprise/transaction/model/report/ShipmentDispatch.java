package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

import java.util.List;

@Data
public class ShipmentDispatch {

	private ShipmentDispatchHeader header;
	private List<ShipmentDispatchList> shipmentDispatchList;
}